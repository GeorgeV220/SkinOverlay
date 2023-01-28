package com.georgev22.skinoverlay.listeners.bungee;

import com.georgev22.skinoverlay.utilities.player.PlayerObjectBungee;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class DeveloperInformListener implements Listener {

    @EventHandler
    public void onJoin(PostLoginEvent e) {
        new PlayerObjectBungee(e.getPlayer()).developerInform();
    }
}
