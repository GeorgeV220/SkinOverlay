package com.georgev22.skinoverlay.message;

import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;

public class VelocityPluginMessageManager extends MessageManager {
    @ApiStatus.Internal
    public static final MinecraftChannelIdentifier outChannelIdentifier = MinecraftChannelIdentifier.from(CHANNEL_TO_BACKEND);
    @ApiStatus.Internal
    public static final MinecraftChannelIdentifier inChannelIdentifier = MinecraftChannelIdentifier.from(CHANNEL_FROM_BACKEND);
    private final ProxyServer proxyServer;
    private Consumer<UUID> playerJoinHandler = uuid -> {
    };

    public VelocityPluginMessageManager(@NotNull ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public void publishSkinProperty(@NotNull UUID playerUUID, @NotNull Skin skin) {
        String message = playerUUID + "|" + skin.toBase64();
        byte[] data = toByteArray("skinupdate", message);

        if (OptionsUtil.DEBUG.getBooleanValue()) {
            this.mainPlugin.getLogger().info("Sending plugin message with size " + data.length + " bytes to player " + playerUUID);
        }

        Optional<Player> playerOptional = proxyServer.getPlayer(playerUUID);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            Optional<ServerConnection> currentServer = player.getCurrentServer();
            if (currentServer.isEmpty()) {
                if (OptionsUtil.DEBUG.getBooleanValue()) {
                    this.mainPlugin.getLogger().info("Player " + player.getUsername() + " is not connected to a server.");
                }
                return;
            }
            currentServer.get().sendPluginMessage(outChannelIdentifier, data);
            if (OptionsUtil.DEBUG.getBooleanValue()) {
                this.mainPlugin.getLogger().info("Sent plugin message with size " + data.length + " bytes to server " + currentServer.get().getServerInfo().getName() + " from player " + player.getUsername());
            }
        } else {
            if (OptionsUtil.DEBUG.getBooleanValue()) {
                this.mainPlugin.getLogger().info("Player " + playerUUID + " is not online.");
            }
        }
    }

    @Override
    public void subscribeSkinProperty(@NotNull BiConsumer<UUID, Skin> handler) {
        throw new UnsupportedOperationException("Velocity side does not need to subscribe to incoming skin changes.");
    }

    @Override
    public void publishPlayerJoin(@NotNull UUID playerUUID) {
        throw new UnsupportedOperationException("Velocity side does not need to publish player joins.");
    }

    @Override
    public void subscribePlayerJoin(Consumer<UUID> handler) {
        this.playerJoinHandler = handler;
    }

    @Override
    public void close() {
        proxyServer.getChannelRegistrar().unregister(inChannelIdentifier);
    }


    @Subscribe
    public void onPluginMessageFromPlayer(PluginMessageEvent event) {
        if (!inChannelIdentifier.equals(event.getIdentifier())) {
            this.mainPlugin.getLogger().log(Level.SEVERE, "Error parsing channel from plugin message");
            return;
        }

        //event.setResult(PluginMessageEvent.ForwardResult.handled());


        if (!(event.getSource() instanceof ServerConnection serverConnection)) {
            return;
        }
        Player player = serverConnection.getPlayer();

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String channel = in.readUTF();
        if (channel.isEmpty()) {
            this.mainPlugin.getLogger().log(Level.SEVERE, "Error parsing channel from plugin message");
            return;
        }
        if (!channel.equalsIgnoreCase("playerjoin")) {
            this.mainPlugin.getLogger().log(Level.SEVERE, "Error parsing channel from plugin message");
            return;
        }
        String message = in.readUTF();
        UUID uuid;
        try {
            uuid = UUID.fromString(message);
        } catch (Exception e) {
            this.mainPlugin.getLogger().log(Level.SEVERE, "Error parsing UUID from plugin message: " + message, e);
            return;
        }
        playerJoinHandler.accept(uuid);
    }
}
