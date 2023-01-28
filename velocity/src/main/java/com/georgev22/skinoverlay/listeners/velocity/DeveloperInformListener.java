package com.georgev22.skinoverlay.listeners.velocity;

import com.georgev22.skinoverlay.utilities.player.PlayerObjectVelocity;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;

public class DeveloperInformListener {

    @Subscribe
    public void onJoin(PostLoginEvent e) {
        new PlayerObjectVelocity(e.getPlayer()).developerInform();
    }
}
