package com.georgev22.skinoverlay.listeners.sponge;

import com.georgev22.skinoverlay.utilities.player.PlayerObjectSponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class PlayerListeners {

    @Listener
    public void onLogin(ServerSideConnectionEvent.Join joinEvent) {
        if (!joinEvent.player().isOnline())
            return;
        new PlayerObjectSponge(joinEvent.player().user()).playerJoin();
    }

    @Listener
    public void onDisconnect(ServerSideConnectionEvent.Disconnect disconnectEvent) {
        new PlayerObjectSponge(disconnectEvent.player().user()).playerQuit();
    }
}

