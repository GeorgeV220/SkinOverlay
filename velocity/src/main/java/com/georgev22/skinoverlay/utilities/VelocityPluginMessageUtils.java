package com.georgev22.skinoverlay.utilities;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import org.jetbrains.annotations.NotNull;

public class VelocityPluginMessageUtils {

    public void sendDataToServer(@NotNull ProxyServer proxyServer, @NotNull ServerInfo serverInfo, @NotNull String channel, String... dataArray) {
        if (proxyServer.getServer(serverInfo.getName()).isPresent()) {
            proxyServer.getServer(serverInfo.getName()).get().sendPluginMessage(() -> "skinoverlay:bungee", this.toByteArray(channel, dataArray));
        }
    }

    public void sendDataTooAllServers(@NotNull ProxyServer proxyServer, @NotNull String channel, String... dataArray) {
        proxyServer.getAllServers().forEach(registeredServer -> this.sendDataToServer(proxyServer, registeredServer.getServerInfo(), channel, dataArray));
    }

    public void sendDataToPlayer(@NotNull String channel, @NotNull Player player, String... dataArray) {
        player.sendPluginMessage(() -> "skinoverlay:bungee", this.toByteArray(channel, dataArray));
    }

    public void sendDataToAllPlayers(ProxyServer proxyServer, @NotNull String channel, String... dataArray) {
        proxyServer.getAllPlayers().forEach(player -> this.sendDataToPlayer(channel, player, dataArray));
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
