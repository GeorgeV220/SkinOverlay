package com.georgev22.skinoverlay.listeners.sponge;

import com.georgev22.skinoverlay.SkinOverlay;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class DeveloperInformListener {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @Listener
    public void onLogin(ClientConnectionEvent.Join joinEvent) {
        skinOverlay.getPlayer(joinEvent.getTargetEntity().getUniqueId()).orElseThrow().developerInform();
    }
}
