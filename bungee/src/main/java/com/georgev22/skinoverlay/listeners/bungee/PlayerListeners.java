package com.georgev22.skinoverlay.listeners.bungee;

import com.georgev22.skinoverlay.utilities.player.PlayerObjectBungee;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
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
    public void onConnect(ServerConnectedEvent serverConnectedEvent) {
        if (!serverConnectedEvent.getPlayer().isConnected())
            return;
        new PlayerObjectBungee(serverConnectedEvent.getPlayer()).updateSkin();
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent playerDisconnectEvent) {
        new PlayerObjectBungee(playerDisconnectEvent.getPlayer()).playerQuit();
    }
}

