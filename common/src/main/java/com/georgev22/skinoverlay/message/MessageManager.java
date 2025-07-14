package com.georgev22.skinoverlay.message;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.exceptions.MessageEncryptionException;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.EncryptUtils;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Manages plugin message communication for SkinOverlay.
 * <p>
 * Provides publishing and subscription for skin data and player join events,
 * and utility methods for constructing and parsing plugin message packets with encryption and compression.
 * <p>
 * All messages are encrypted using AES-GCM with compression to ensure confidentiality
 * and reduce message size to fit plugin messaging limits (e.g. {@link Short#MAX_VALUE} bytes).
 */
public abstract class MessageManager {

    /**
     * Reference to the main SkinOverlay plugin instance.
     */
    protected final SkinOverlay mainPlugin = SkinOverlay.getInstance();

    /**
     * Channel name used to send messages to backend systems.
     */
    protected static final String CHANNEL_TO_BACKEND = "skinoverlay:tobackend";

    /**
     * Channel name used to receive messages from backend systems.
     */
    protected static final String CHANNEL_FROM_BACKEND = "skinoverlay:frombackend";

    /**
     * Publishes skin properties for the given player UUID.
     *
     * @param playerUUID UUID of the player
     * @param skin       Skin data to publish
     */
    public abstract void publishSkinProperty(@NotNull UUID playerUUID, @NotNull Skin skin);

    /**
     * Subscribes to skin property updates.
     *
     * @param handler BiConsumer to handle incoming skin updates with player UUID and Skin
     */
    public abstract void subscribeSkinProperty(@NotNull BiConsumer<UUID, Skin> handler);

    /**
     * Publishes a player join event.
     *
     * @param playerUUID UUID of the joined player
     */
    public abstract void publishPlayerJoin(@NotNull UUID playerUUID);

    /**
     * Subscribes to player join events.
     *
     * @param handler Consumer to handle player join events with player UUID
     */
    public abstract void subscribePlayerJoin(Consumer<UUID> handler);

    /**
     * Closes the message manager, cleaning up any resources such as channels or listeners.
     */
    public abstract void close();

    /**
     * Creates a {@link ByteArrayOutputStream} containing the plugin message payload with sub-channel and data.
     * <p>
     * Each data entry is compressed and encrypted before writing to ensure small payload size
     * and confidentiality during transmission.
     *
     * @param subChannel Sub-channel identifier within the plugin message channel
     * @param dataArray  Array of data entries to write (each will be encrypted)
     * @return A {@link ByteArrayOutputStream} containing the constructed payload
     * @throws MessageEncryptionException if encryption fails for any data entry
     */
    @NotNull
    public ByteArrayOutputStream byteArrayDataOutput(@NotNull String subChannel, String @NotNull ... dataArray) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(byteOut);
        try {
            dataOut.writeUTF(subChannel);
            for (String data : dataArray) {
                String encryptedData = EncryptUtils.encrypt(data, OptionsUtil.SECRET.getStringValue());
                dataOut.writeUTF(Objects.requireNonNull(encryptedData));
            }
            dataOut.flush();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write data output", e);
        } catch (GeneralSecurityException e) {
            throw new MessageEncryptionException("Failed to encrypt data", e);
        }
        return byteOut;
    }

    /**
     * Converts the given channel and data entries to a byte array suitable for plugin message sending.
     *
     * @param channel   The sub-channel to use
     * @param dataArray Data entries to include in the message
     * @return Byte array containing the message payload
     * @throws MessageEncryptionException if encryption fails for any data entry
     */
    public byte @NotNull [] toByteArray(@NotNull String channel, String... dataArray) {
        return this.byteArrayDataOutput(channel, dataArray).toByteArray();
    }

    /**
     * Reads and parses a plugin message byte array into its sub-channel and decrypted data entries.
     *
     * @param byteArray The byte array received from a plugin message channel
     * @return A {@link MessageData} object containing the sub-channel and data entries
     * @throws IOException                if an I/O error occurs while reading
     * @throws MessageEncryptionException if decryption fails for any data entry
     */
    public @NotNull MessageData readByteArray(byte @NotNull [] byteArray) throws IOException {
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(byteArray);
             DataInputStream dataIn = new DataInputStream(byteIn)) {
            return readDataInput(dataIn);
        }
    }

    /**
     * Reads and parses a {@link DataInputStream} into its sub-channel and decrypted data entries.
     *
     * @param dataIn The {@link DataInputStream} to read from
     * @return A {@link MessageData} object containing the sub-channel and data entries
     * @throws IOException                if an I/O error occurs while reading
     * @throws MessageEncryptionException if decryption fails for any data entry
     */
    public @NotNull MessageData readDataInput(@NotNull DataInputStream dataIn) throws IOException {
        String subChannel = dataIn.readUTF();
        List<String> dataList = new ArrayList<>();

        while (dataIn.available() > 0) {
            String encryptedData = dataIn.readUTF();
            try {
                String decryptedData = EncryptUtils.decrypt(encryptedData, OptionsUtil.SECRET.getStringValue());
                dataList.add(decryptedData);
            } catch (GeneralSecurityException e) {
                throw new MessageEncryptionException("Failed to decrypt data", e);
            }
        }

        return new MessageData(subChannel, dataList.toArray(new String[0]));
    }
}
