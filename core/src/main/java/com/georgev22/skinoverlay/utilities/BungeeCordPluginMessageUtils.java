package com.georgev22.skinoverlay.utilities;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

public final class BungeeCordPluginMessageUtils {

    public void sendDataToServer(@NotNull String channel, @NotNull ServerInfo serverInfo, String... dataArray) {
        serverInfo.sendData("skinoverlay:bungee", this.toByteArray(channel, dataArray));
    }

    public void sendDataTooAllServers(@NotNull String channel, String... dataArray) {
        ProxyServer.getInstance().getServers().forEach((s, serverInfo) -> this.sendDataToServer(channel, serverInfo, dataArray));
    }

    public void sendDataToPlayer(@NotNull String channel, @NotNull ProxiedPlayer proxiedPlayer, String... dataArray) {
        proxiedPlayer.getServer().sendData("skinoverlay:bungee", this.toByteArray(channel, dataArray));
    }

    public void sendDataToAllPlayers(@NotNull String channel, String... dataArray) {
        ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer -> this.sendDataToPlayer(channel, proxiedPlayer, dataArray));
    }

    @NotNull
    public ByteArrayDataOutput byteArrayDataOutput(@NotNull String channel, String @NotNull ... dataArray) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(channel);
        for (String data : dataArray) {
            out.writeUTF(data);
        }
        return out;
    }

    public byte @NotNull [] toByteArray(@NotNull String channel, String... dataArray) {
        return this.byteArrayDataOutput(channel, dataArray).toByteArray();
    }
}
