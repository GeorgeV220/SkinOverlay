package com.georgev22.skinoverlay.message;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import org.jetbrains.annotations.NotNull;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.georgev22.skinoverlay.utilities.Utils.decrypt;
import static com.georgev22.skinoverlay.utilities.Utils.encrypt;

public abstract class MessageManager {

    protected final SkinOverlay mainPlugin = SkinOverlay.getInstance();
    protected static final String CHANNEL_TO_BACKEND = "skinoverlay:tobackend";
    protected static final String CHANNEL_FROM_BACKEND = "skinoverlay:frombackend";

    public abstract void publishSkinProperty(@NotNull UUID playerUUID, @NotNull Skin skin);

    public abstract void subscribeSkinProperty(@NotNull BiConsumer<UUID, Skin> handler);

    public abstract void publishPlayerJoin(@NotNull UUID playerUUID);

    public abstract void subscribePlayerJoin(Consumer<UUID> handler);

    public abstract void close();

    /**
     * Creates a ByteArrayDataOutput object with the given sub-channel and data.
     *
     * @param subChannel the sub-channel to write to the output.
     * @param dataArray  the data to write to the output.
     * @return a new ByteArrayDataOutput object.
     */
    @NotNull
    public ByteArrayOutputStream byteArrayDataOutput(@NotNull String subChannel, String @NotNull ... dataArray) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(byteOut);
        try {
            dataOut.writeUTF(subChannel);
            for (String data : dataArray) {
                String encryptedData = encrypt(data, OptionsUtil.SECRET.getStringValue());
                dataOut.writeUTF(Objects.requireNonNull(encryptedData));
            }
            dataOut.flush();
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("Failed to write data output", e);
        }
        return byteOut;
    }

    /**
     * Converts the data in the given channel and data array to a byte array.
     *
     * @param channel   the channel to use.
     * @param dataArray the data to convert.
     * @return a byte array representation of the data.
     */
    public byte @NotNull [] toByteArray(@NotNull String channel, String... dataArray) {
        return this.byteArrayDataOutput(channel, dataArray).toByteArray();
    }

    /**
     * Reads a byte array into its sub-channel and decrypted data entries.
     *
     * @param byteArray the byte array to read
     * @return a MessageData object containing the sub-channel and decrypted data entries
     * @throws IOException if an I/O error occurs
     */
    public @NotNull MessageData readByteArray(byte @NotNull [] byteArray) throws IOException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteArray);
        DataInputStream dataIn = new DataInputStream(byteIn);
        String subChannel = dataIn.readUTF();

        java.util.List<String> dataList = new java.util.ArrayList<>();
        while (dataIn.available() > 0) {
            String encryptedData = dataIn.readUTF();
            try {
                String decryptedData = decrypt(encryptedData, OptionsUtil.SECRET.getStringValue());
                dataList.add(decryptedData);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException |
                     InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException("Failed to decrypt data", e);
            }
        }

        return new MessageData(subChannel, dataList.toArray(new String[0]));
    }

    /**
     * Reads a DataInputStream into its sub-channel and decrypted data entries.
     *
     * @param dataIn the DataInputStream to read
     * @return a MessageData object containing the sub-channel and decrypted data entries
     * @throws IOException if an I/O error occurs
     */
    public @NotNull MessageData readDataInput(@NotNull DataInputStream dataIn) throws IOException {
        String subChannel = dataIn.readUTF();

        java.util.List<String> dataList = new java.util.ArrayList<>();
        while (dataIn.available() > 0) {
            String encryptedData = dataIn.readUTF();
            try {
                String decryptedData = decrypt(encryptedData, OptionsUtil.SECRET.getStringValue());
                dataList.add(decryptedData);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException |
                     InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException("Failed to decrypt data", e);
            }
        }

        return new MessageData(subChannel, dataList.toArray(new String[0]));
    }

}
