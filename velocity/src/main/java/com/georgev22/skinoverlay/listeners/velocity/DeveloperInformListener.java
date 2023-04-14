package com.georgev22.skinoverlay.listeners.velocity;

import com.georgev22.skinoverlay.SkinOverlay;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;

public class DeveloperInformListener {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @Subscribe
    public void onJoin(PostLoginEvent e) {
        skinOverlay.getPlayer(e.getPlayer().getUniqueId()).orElseThrow().developerInform();
    }
}
