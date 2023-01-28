package com.georgev22.skinoverlay.listeners.sponge;

import com.georgev22.skinoverlay.utilities.player.PlayerObjectSponge7;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class PlayerListeners {

    @Listener
    public void onLogin(ClientConnectionEvent.Join joinEvent) {
        if (!joinEvent.getTargetEntity().isOnline())
            return;
        new PlayerObjectSponge7(joinEvent.getTargetEntity()).playerJoin();
    }

    @Listener
    public void onLogin(ClientConnectionEvent.Disconnect disconnectEvent) {
        new PlayerObjectSponge7(disconnectEvent.getTargetEntity()).playerQuit();
    }
}

