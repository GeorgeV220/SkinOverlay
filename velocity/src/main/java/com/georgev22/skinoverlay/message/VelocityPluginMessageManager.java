package com.georgev22.skinoverlay.message;

import com.georgev22.skinoverlay.storage.data.Skin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class VelocityPluginMessageManager implements MessageManager {
    private final ProxyServer proxyServer;
    private final MinecraftChannelIdentifier channelIdentifier;

    public VelocityPluginMessageManager(@NotNull ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
        this.channelIdentifier = MinecraftChannelIdentifier.from("skinoverlay:skinupdate");
        proxyServer.getChannelRegistrar().register(channelIdentifier);
    }

    @Override
    public void publishSkinProperty(@NotNull UUID playerUUID, @NotNull Skin skin) {
        String message = playerUUID + "|" + skin.toBase64();
        byte[] data = message.getBytes(StandardCharsets.UTF_8);

        Optional<Player> playerOptional = proxyServer.getAllPlayers().stream().findAny();
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            player.sendPluginMessage(channelIdentifier, data);
        } else {
            proxyServer.getConsoleCommandSource().sendMessage(
                    Component.text("[SkinOverlay] No players online to send plugin message.")
            );
        }
    }

    @Override
    public void subscribeSkinProperty(@NotNull SkinPropertyHandler handler) {
    }

    @Override
    public void close() {
        proxyServer.getChannelRegistrar().unregister(channelIdentifier);
    }
}
