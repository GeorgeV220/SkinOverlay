package com.georgev22.skinoverlay.listeners.sponge;

import com.georgev22.skinoverlay.SkinOverlay;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class PlayerListeners {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @Listener
    public void onJoin(ClientConnectionEvent.Join joinEvent) {
        if (!joinEvent.getTargetEntity().isOnline())
            return;
        skinOverlay.getPlayer(joinEvent.getTargetEntity().getUniqueId()).orElseThrow().playerJoin();
    }

    @Listener
    public void onDisconnect(ClientConnectionEvent.Disconnect disconnectEvent) {
        skinOverlay.getPlayer(disconnectEvent.getTargetEntity().getUniqueId()).orElseThrow().playerQuit();
    }
}

