package com.georgev22.skinoverlay.listeners.bukkit;

import com.georgev22.skinoverlay.utilities.player.PlayerObjectBukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class DeveloperInformListener implements Listener {

    @EventHandler
    private void onJoin(final PlayerJoinEvent e) {
        new PlayerObjectBukkit(e.getPlayer()).developerInform();
    }
}
