package com.georgev22.skinoverlay.listeners.velocity;

import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.skinoverlay.SkinOverlay;
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

        //TODO THINK ABOUT A BETTER FIX FOR THIS ISSUE
        SchedulerManager.getScheduler().runTaskLater(SkinOverlay.getInstance().getClass(), () -> {
            new PlayerObjectVelocity(serverConnectedEvent.getPlayer()).updateSkin();
        }, 20L);
    }

    @Subscribe
    public void onQuit(DisconnectEvent playerDisconnectEvent) {
        new PlayerObjectVelocity(playerDisconnectEvent.getPlayer()).playerQuit();
    }
}

