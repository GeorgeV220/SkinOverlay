package com.georgev22.skinoverlay.listeners.bukkit;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.event.events.player.SPlayerJoinEvent;
import com.georgev22.skinoverlay.event.events.player.SPlayerLeaveEvent;
import com.georgev22.skinoverlay.player.SPlayer;
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
        SPlayer sPlayer = mainPlugin.getPlayerProvider().getSPlayer(player);
        if (sPlayer != null) {
            mainPlugin.getEventBus().post(new SPlayerJoinEvent(sPlayer));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        SPlayer sPlayer = mainPlugin.getPlayerProvider().getSPlayer(player);
        if (sPlayer != null) {
            mainPlugin.getEventBus().post(new SPlayerLeaveEvent(sPlayer));
        }
    }

}
