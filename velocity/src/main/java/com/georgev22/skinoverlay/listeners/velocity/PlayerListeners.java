package com.georgev22.skinoverlay.listeners.velocity;

import com.georgev22.skinoverlay.utilities.player.PlayerObjectVelocity;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;

public class PlayerListeners {

    @Subscribe
    public void onLogin(PostLoginEvent loginEvent) {
        if (!loginEvent.getPlayer().isActive())
            return;
        new PlayerObjectVelocity(loginEvent.getPlayer()).playerJoin();
    }

    @Subscribe
    public void onChange(ServerConnectedEvent serverConnectedEvent) {
        if (serverConnectedEvent.getPreviousServer().isEmpty()) {
            return;
        }
        if (!serverConnectedEvent.getPlayer().isActive())
            return;
        new PlayerObjectVelocity(serverConnectedEvent.getPlayer()).updateSkin();
    }

    @Subscribe
    public void onQuit(DisconnectEvent playerDisconnectEvent) {
        new PlayerObjectVelocity(playerDisconnectEvent.getPlayer()).playerQuit();
    }
}

