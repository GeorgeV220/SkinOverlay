package com.georgev22.skinoverlay.utilities;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.georgev22.skinoverlay.utilities.Utilities.encrypt;

/**
 * Provides utility methods for sending plugin messages to players or the server.
 */
public abstract class PluginMessageUtils {

    /**
     * The channel used to send the plugin message.
     */
    @Setter
    @Getter
    private String channel;

    /**
     * The object to send in the plugin message.
     */
    @Setter
    @Getter
    private Object object;

    /**
     * The SkinOverlay instance.
     */
    protected final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    /**
     * Sends a plugin message to the server.
     *
     * @param subChannel the sub-channel to send the message on.
     * @param dataArray  the data to send in the message.
     */
    public abstract void sendDataToServer(@NotNull String subChannel, String... dataArray);

    /**
     * Sends a plugin message to a player.
     *
     * @param subChannel the sub-channel to send the message on.
     * @param player     the player to send the message to.
     * @param dataArray  the data to send in the message.
     */
    public abstract void sendDataToPlayer(@NotNull String subChannel, @NotNull PlayerObject player, String... dataArray);

    /**
     * Sends a plugin message to all online players.
     *
     * @param subChannel the sub-channel to send the message on.
     * @param dataArray  the data to send in the message.
     */
    public void sendDataToAllPlayers(@NotNull String subChannel, String... dataArray) {
        skinOverlay.onlinePlayers().forEach(playerObject -> sendDataToPlayer(subChannel, playerObject, dataArray));
    }

    /**
     * Creates a ByteArrayDataOutput object with the given sub-channel and data.
     *
     * @param subChannel the sub-channel to write to the output.
     * @param dataArray  the data to write to the output.
     * @return a new ByteArrayDataOutput object.
     */
    @NotNull
    public ByteArrayDataOutput byteArrayDataOutput(@NotNull String subChannel, String @NotNull ... dataArray) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subChannel);
        for (String data : dataArray) {
            String encryptedData = encrypt(data);
            out.writeUTF(Objects.requireNonNull(encryptedData));
        }
        return out;
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
}
