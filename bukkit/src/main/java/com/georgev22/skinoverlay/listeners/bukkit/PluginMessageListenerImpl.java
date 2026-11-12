package com.georgev22.skinoverlay.listeners.bukkit;

import com.georgev22.skinoverlay.messaging.MessageData;
import com.georgev22.skinoverlay.messaging.MessageManager;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;

public class PluginMessageListenerImpl extends MessageManager implements PluginMessageListener {
    private BiConsumer<UUID, Skin> handler = (uuid, skin) -> {
    };

    public PluginMessageListenerImpl() {
        Bukkit.getMessenger().registerIncomingPluginChannel(this.mainPlugin.getPlugin(), CHANNEL_TO_BACKEND, this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this.mainPlugin.getPlugin(), CHANNEL_FROM_BACKEND);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!channel.equals(CHANNEL_TO_BACKEND)) return;

        @NotNull MessageData data;
        try {
            data = readByteArray(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!data.subChannel().equalsIgnoreCase("skinupdate")) {
            if (OptionsUtil.DEBUG.getBooleanValue()) {
                this.mainPlugin.getLogger().warning("Unknown subchannel: " + data.subChannel());
            }
            return;
        }
        List<String> parts = List.of(data.dataEntries());
        if (parts.size() != 2) {
            this.mainPlugin.getLogger().severe("Invalid skin property message: " + data);
            return;
        }

        try {
            UUID uuid = UUID.fromString(parts.get(0));
            String base64Skin = parts.get(1);
            Skin skin = Skin.fromBase64(base64Skin);

            this.mainPlugin.getScheduler().runTask(mainPlugin.getPlugin(), () -> handler.accept(uuid, skin));
        } catch (Exception e) {
            this.mainPlugin.getLogger().log(Level.SEVERE, "Error handling skin property message: " + data, e);
        }
    }

    @Override
    public void publishSkinProperty(@NotNull UUID playerUUID, @NotNull Skin skin) {
        // No need to send to this channel
    }

    @Override
    public void publishPlayerJoin(@NotNull UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) {
            this.mainPlugin.getLogger().warning("Player " + playerUUID + " not found");
            return;
        }
        String message = playerUUID.toString();
        byte[] data = this.toByteArray("playerjoin", message);
        player.sendPluginMessage(this.mainPlugin.getPlugin(), CHANNEL_FROM_BACKEND, data);
        if (OptionsUtil.DEBUG.getBooleanValue())
            this.mainPlugin.getLogger().info("Sent player join message to " + CHANNEL_FROM_BACKEND + ": " + message + " (" + data.length + " bytes) to " + player.getName());
    }

    @Override
    public void subscribeSkinProperty(@NotNull BiConsumer<UUID, Skin> handler) {
        this.handler = handler;
    }

    @Override
    public void subscribePlayerJoin(Consumer<UUID> handler) {
        // No need to listen to this channel
    }

    @Override
    public void close() {

    }
}
