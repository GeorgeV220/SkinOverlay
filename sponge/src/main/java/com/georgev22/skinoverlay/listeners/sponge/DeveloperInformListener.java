package com.georgev22.skinoverlay.listeners.sponge;

import com.georgev22.skinoverlay.utilities.player.PlayerObjectSponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class DeveloperInformListener {
    @Listener
    public void onLogin(ServerSideConnectionEvent.Join loginEvent) {
        new PlayerObjectSponge(loginEvent.player().user()).developerInform();
    }
}
