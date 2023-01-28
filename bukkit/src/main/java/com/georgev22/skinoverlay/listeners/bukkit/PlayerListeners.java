package com.georgev22.skinoverlay.listeners.bukkit;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectBukkit;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.File;
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

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!channel.equalsIgnoreCase("skinoverlay:bungee")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        UUID uuid = UUID.fromString(in.readUTF());
        String skinName = in.readUTF();
        PlayerObject playerObject = skinOverlay.getPlayer(uuid).orElseThrow();
        if (subChannel.equalsIgnoreCase("change")) {
            Utilities.setSkin(() -> ImageIO.read(new File(skinOverlay.getSkinsDataFolder(), skinName + ".png")), skinName, playerObject, null);
        } else if (subChannel.equalsIgnoreCase("reset")) {
            Utilities.setSkin(() -> null, skinName, playerObject, null);
        } else if (subChannel.equalsIgnoreCase("changeWithProperties")) {
            String name = in.readUTF();
            String value = in.readUTF();
            String signature = in.readUTF();
            Utilities.setSkin(skinName, playerObject, new String[]{name, value, signature});
        } else if (subChannel.equalsIgnoreCase("resetWithProperties")) {
            String name = in.readUTF();
            String value = in.readUTF();
            String signature = in.readUTF();
            Utilities.setSkin(skinName, playerObject, new String[]{name, value, signature});
        }
    }
}

