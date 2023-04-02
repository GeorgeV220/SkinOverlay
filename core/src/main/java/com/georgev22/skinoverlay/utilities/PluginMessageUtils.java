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

public abstract class PluginMessageUtils {

    @Setter
    @Getter
    private String channel;

    @Setter
    @Getter
    private Object object;

    protected final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    public abstract void sendDataToServer(@NotNull String subChannel, String... dataArray);

    public abstract void sendDataToPlayer(@NotNull String subChannel, @NotNull PlayerObject player, String... dataArray);

    public void sendDataToAllPlayers(@NotNull String subChannel, String... dataArray) {
        skinOverlay.onlinePlayers().forEach(playerObject -> sendDataToPlayer(subChannel, playerObject, dataArray));
    }

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

    public byte @NotNull [] toByteArray(@NotNull String channel, String... dataArray) {
        return this.byteArrayDataOutput(channel, dataArray).toByteArray();
    }
}
