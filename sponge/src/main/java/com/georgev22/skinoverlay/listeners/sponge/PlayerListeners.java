package com.georgev22.skinoverlay.listeners.sponge;

import com.georgev22.skinoverlay.SkinOverlay;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class PlayerListeners {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @Listener
    public void onLogin(ServerSideConnectionEvent.Join joinEvent) {
        if (!joinEvent.player().isOnline())
            return;
        skinOverlay.getPlayer(joinEvent.player().uniqueId()).orElseThrow().playerJoin();
    }

    @Listener
    public void onDisconnect(ServerSideConnectionEvent.Disconnect disconnectEvent) {
        skinOverlay.getPlayer(disconnectEvent.player().uniqueId()).orElseThrow().playerQuit();
    }
}

