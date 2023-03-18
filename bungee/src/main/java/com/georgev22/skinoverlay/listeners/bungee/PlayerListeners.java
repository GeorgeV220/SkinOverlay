package com.georgev22.skinoverlay.listeners.bungee;

import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectBungee;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;


public class PlayerListeners implements Listener {

    @EventHandler
    public void onLogin(PostLoginEvent postLoginEvent) {
        if (!postLoginEvent.getPlayer().isConnected())
            return;
        new PlayerObjectBungee(postLoginEvent.getPlayer()).playerJoin();
    }

    @EventHandler
    public void onConnect(ServerSwitchEvent serverSwitchEvent) {
        if (serverSwitchEvent.getFrom() == null) {
            return;
        }
        if (!serverSwitchEvent.getPlayer().isConnected())
            return;
        //TODO THINK ABOUT A BETTER FIX FOR THIS ISSUE
        SchedulerManager.getScheduler().runTaskLater(SkinOverlay.getInstance().getClass(), () -> {
            new PlayerObjectBungee(serverSwitchEvent.getPlayer()).updateSkin();
        }, 20L);
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent playerDisconnectEvent) {
        new PlayerObjectBungee(playerDisconnectEvent.getPlayer()).playerQuit();
    }
}

