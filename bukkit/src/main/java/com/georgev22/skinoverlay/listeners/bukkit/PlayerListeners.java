package com.georgev22.skinoverlay.listeners.bukkit;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.event.events.player.SkinOverlayPlayerJoinEvent;
import com.georgev22.skinoverlay.event.events.player.SkinOverlayPlayerLeaveEvent;
import com.georgev22.skinoverlay.player.SkinOverlayPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener {

    private final SkinOverlay mainPlugin = SkinOverlay.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SkinOverlayPlayer skinOverlayPlayer = mainPlugin.getPlayerProvider().getSkinOverlayPlayer(player);
        if (skinOverlayPlayer != null) {
            mainPlugin.getEventBus().post(new SkinOverlayPlayerJoinEvent(skinOverlayPlayer));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        SkinOverlayPlayer skinOverlayPlayer = mainPlugin.getPlayerProvider().getSkinOverlayPlayer(player);
        if (skinOverlayPlayer != null) {
            mainPlugin.getEventBus().post(new SkinOverlayPlayerLeaveEvent(skinOverlayPlayer));
        }
    }

}
