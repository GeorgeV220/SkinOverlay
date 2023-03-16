package com.georgev22.skinoverlay.listeners.bukkit;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectBukkit;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.util.UUID;

public class PlayerListeners implements Listener, PluginMessageListener {
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        new PlayerObjectBukkit(playerJoinEvent.getPlayer()).playerJoin();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent playerQuitEvent) {
        new PlayerObjectBukkit(playerQuitEvent.getPlayer()).playerQuit();
    }

    @SneakyThrows
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!channel.equalsIgnoreCase("skinoverlay:bungee")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        UUID uuid = UUID.fromString(in.readUTF());
        byte[] skinOptionsBytes = in.readUTF().getBytes();
        ByteArrayInputStream bi = new ByteArrayInputStream(skinOptionsBytes);
        ObjectInputStream si = new ObjectInputStream(bi);
        SkinOptions skinOptions = (SkinOptions) si.readObject();
        PlayerObject playerObject = skinOverlay.getPlayer(uuid).orElseThrow();
        if (subChannel.equalsIgnoreCase("change")) {
            if (skinOptions.getSkinName().contains("custom")) {
                Utilities.setSkin(() -> ImageIO.read(new File(skinOverlay.getSkinsDataFolder(), skinOptions.getSkinName() + ".png")), skinOptions, playerObject, null);
            } else {
                URL url = new URL(skinOptions.getUrl());
                ByteArrayOutputStream output = new ByteArrayOutputStream();

                try (InputStream stream = url.openStream()) {
                    byte[] buffer = new byte[4096];

                    while (true) {
                        int bytesRead = stream.read(buffer);
                        if (bytesRead < 0) {
                            break;
                        }
                        output.write(buffer, 0, bytesRead);
                    }
                }
                Utilities.setSkin(() -> ImageIO.read(new ByteArrayInputStream(output.toByteArray())), skinOptions, playerObject, null);
            }
        } else if (subChannel.equalsIgnoreCase("reset")) {
            Utilities.setSkin(() -> null, skinOptions, playerObject, null);
        } else if (subChannel.equalsIgnoreCase("changeWithProperties")) {
            String name = in.readUTF();
            String value = in.readUTF();
            String signature = in.readUTF();
            Utilities.setSkin(skinOptions, playerObject, new String[]{name, value, signature});
        } else if (subChannel.equalsIgnoreCase("resetWithProperties")) {
            String name = in.readUTF();
            String value = in.readUTF();
            String signature = in.readUTF();
            Utilities.setSkin(skinOptions, playerObject, new String[]{name, value, signature});
        }
    }
}

