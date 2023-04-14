package com.georgev22.skinoverlay.listeners.sponge;

import com.georgev22.skinoverlay.SkinOverlay;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class DeveloperInformListener {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @Listener
    public void onLogin(ServerSideConnectionEvent.Join loginEvent) {
        skinOverlay.getPlayer(loginEvent.player().uniqueId()).orElseThrow().developerInform();
    }
}
