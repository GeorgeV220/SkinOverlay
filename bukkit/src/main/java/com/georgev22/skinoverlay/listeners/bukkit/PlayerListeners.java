package com.georgev22.skinoverlay.listeners.bukkit;

import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectBukkit;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

import static com.georgev22.skinoverlay.utilities.Utilities.decrypt;

public class PlayerListeners implements Listener, PluginMessageListener {
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        PlayerObject playerObject = new PlayerObjectBukkit(playerJoinEvent.getPlayer());
        playerObject.playerJoin();
        if (OptionsUtil.PROXY.getBooleanValue())
            SchedulerManager.getScheduler().runTaskLater(skinOverlay.getClass(), () -> {
                skinOverlay.getPluginMessageUtils().setChannel("skinoverlay:messagechannel");
                if (playerJoinEvent.getPlayer().isOnline())
                    skinOverlay.getPluginMessageUtils().sendDataToPlayer("playerJoin", playerObject, playerObject.playerUUID().toString());
                else
                    skinOverlay.getLogger().warning("Player " + playerObject.playerName() + " is not online");
            }, 1);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
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
        UUID uuid = UUID.fromString(Objects.requireNonNull(decrypt(in.readUTF())));
        SkinOptions skinOptions = Utilities.getSkinOptions(Objects.requireNonNull(decrypt(in.readUTF())));
        PlayerObject playerObject = skinOverlay.getPlayer(uuid).orElseThrow();
        if (subChannel.equalsIgnoreCase("change")) {
            if (!skinOptions.getSkinName().contains("custom")) {
                skinOverlay.getSkinHandler().setSkin(() -> ImageIO.read(new File(skinOverlay.getSkinsDataFolder(), skinOptions.getSkinName() + ".png")), skinOptions, playerObject, null);
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
                skinOverlay.getSkinHandler().setSkin(() -> ImageIO.read(new ByteArrayInputStream(output.toByteArray())), skinOptions, playerObject, null);
            }
        } else if (subChannel.equalsIgnoreCase("reset")) {
            skinOverlay.getSkinHandler().setSkin(() -> null, skinOptions, playerObject, null);
        } else if (subChannel.equalsIgnoreCase("changeWithProperties")) {
            String name = decrypt(in.readUTF());
            String value = decrypt(in.readUTF());
            String signature = decrypt(in.readUTF());
            skinOverlay.getSkinHandler().setSkin(skinOptions, playerObject, new String[]{name, value, signature});
        } else if (subChannel.equalsIgnoreCase("resetWithProperties")) {
            String name = decrypt(in.readUTF());
            String value = decrypt(in.readUTF());
            String signature = decrypt(in.readUTF());
            skinOverlay.getSkinHandler().setSkin(skinOptions, playerObject, new String[]{name, value, signature});
        }
    }

}

