package com.georgev22.skinoverlay.listeners.bukkit;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.minecraft.BukkitMinecraftUtils;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import com.google.common.collect.Lists;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class DeveloperInformListener implements Listener {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    private final List<ObjectMap.Pair<String, UUID>> inform = Lists.newArrayList(
            ObjectMap.Pair.create("GeorgeV22", UUID.fromString("a4f5cd7f-362f-4044-931e-7128b4e6bad9"))
    );

    @EventHandler
    private void onJoin(final PlayerJoinEvent e) {
        final OfflinePlayer player = e.getPlayer();
        final UUID uuid = player.getUniqueId();
        final String name = player.getName();

        final ObjectMap.Pair<String, UUID> pair = ObjectMap.Pair.create(name, uuid);

        boolean found = false;

        for (ObjectMap.Pair<String, UUID> loop : this.inform) {
            if (loop.key().equals(pair.key())) {
                found = true;
                break;
            }
            if (loop.value().equals(pair.value())) {
                found = true;
                break;
            }
        }

        if (!found) {
            return;
        }

        SchedulerManager.getScheduler().runTaskLater(skinOverlay.getClass(), () -> {
            if (!player.isOnline() && player.getPlayer() == null) {
                return;
            }

            BukkitMinecraftUtils.msg(Objects.requireNonNull(player.getPlayer()), joinMessage, new HashObjectMap<String, String>()
                    .append("%player%", player.getName())
                    .append("%version%", skinOverlay.getDescription().version())
                    .append("%package%", skinOverlay.getClass().getPackage().getName())
                    .append("%name%", skinOverlay.getDescription().name())
                    .append("%author%", String.join(", ", skinOverlay.getDescription().authors()))
                    .append("%main%", skinOverlay.getDescription().main())
                    .append("%javaversion%", System.getProperty("java.version"))
                    .append("%serverversion%", BukkitMinecraftUtils.MinecraftVersion.getCurrentVersion().name()), false);
        }, 20L * 10L);

    }

    private final static List<String> joinMessage = Lists.newArrayList(

            "",

            "",

            "&7Hey &f%player%&7, details are listed below.",

            "&7Version: &c%version%",

            "&7Java Version: &c%javaversion%",

            "&7Server Version: &c%serverversion%",

            "&7Name: &c%name%",

            "&7Author: &c%author%",

            "&7Main package: &c%package%",

            "&7Main path: &c%main%",

            "&7Experimental Features: &c" + OptionsUtil.EXPERIMENTAL_FEATURES.getBooleanValue(),

            ""

    );
}
