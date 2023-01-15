package com.georgev22.skinoverlay.listeners.velocity;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.SkinOverlayVelocity;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectWrapper;
import com.google.common.collect.Lists;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;

import java.util.List;
import java.util.UUID;

public class DeveloperInformListener {
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();
    private final List<ObjectMap.Pair<String, UUID>> inform = Lists.newArrayList(
            ObjectMap.Pair.create("Shin1gamiX", UUID.fromString("7cc1d444-fe6f-4063-a426-b62fdfea7dab")),
            ObjectMap.Pair.create("GeorgeV22", UUID.fromString("a4f5cd7f-362f-4044-931e-7128b4e6bad9"))
    );

    @Subscribe
    public void onJoin(PostLoginEvent e) {
        PlayerObject playerObject = new PlayerObjectWrapper(e.getPlayer().getUsername(), e.getPlayer().getUniqueId(), SkinOverlay.getInstance().type());
        UUID uuid = playerObject.playerUUID();
        String name = playerObject.playerName();

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

        SchedulerManager.getScheduler().runTaskLater(this.skinOverlay.getClass(), () -> {
            if (!playerObject.isOnline()) {
                return;
            }
            playerObject.sendMessage(joinMessage, new HashObjectMap<String, String>()
                    .append("%player%", name)
                    .append("%version%", skinOverlay.getDescription().version())
                    .append("%package%", skinOverlay.getClass().getPackage().getName())
                    .append("%name%", skinOverlay.getDescription().name())
                    .append("%author%", String.join(", ", skinOverlay.getDescription().authors()))
                    .append("%main%", skinOverlay.getDescription().main())
                    .append("%javaversion%", System.getProperty("java.version"))
                    .append("%serverversion%", SkinOverlayVelocity.getInstance().getProxy().getVersion().getName() + "-" + SkinOverlayVelocity.getInstance().getProxy().getVersion().getVersion()), false);
        }, 200L);
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
