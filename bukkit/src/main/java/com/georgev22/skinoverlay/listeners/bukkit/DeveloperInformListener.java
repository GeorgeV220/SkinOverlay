package com.georgev22.skinoverlay.listeners.bukkit;

import com.georgev22.skinoverlay.SkinOverlay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public class DeveloperInformListener implements Listener {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @EventHandler
    private void onJoin(final PlayerJoinEvent e) {
        skinOverlay.getPlayer(e.getPlayer().getUniqueId()).orElseThrow().developerInform();
    }
}
