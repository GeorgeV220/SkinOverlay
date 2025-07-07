package com.georgev22.skinoverlay.listeners.bukkit;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.message.SkinPropertyHandler;
import com.georgev22.skinoverlay.storage.data.Skin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PluginMessageListenerImpl implements PluginMessageListener {
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();
    private final SkinPropertyHandler handler;

    public PluginMessageListenerImpl(SkinPropertyHandler handler) {
        this.handler = handler;
        Bukkit.getMessenger().registerIncomingPluginChannel(skinOverlay.getPlugin(), "skinoverlay:skinupdate", this);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!channel.equals("skinoverlay:skinupdate")) return;

        String data = new String(message, StandardCharsets.UTF_8);
        String[] parts = data.split("\\|", 2);
        if (parts.length != 2) {
            skinOverlay.getLogger().severe("Invalid skin property message: " + data);
            return;
        }

        try {
            UUID uuid = UUID.fromString(parts[0]);
            String base64Skin = parts[1];
            Skin skin = Skin.fromBase64(base64Skin);

            Bukkit.getScheduler().runTask(skinOverlay.getPlugin(), () -> handler.handle(uuid, skin));
        } catch (Exception e) {
            skinOverlay.getLogger().log(java.util.logging.Level.SEVERE, "Error handling skin property message: " + data, e);
        }
    }
}
