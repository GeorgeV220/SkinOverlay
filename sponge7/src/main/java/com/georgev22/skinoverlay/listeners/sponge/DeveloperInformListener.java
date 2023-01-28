package com.georgev22.skinoverlay.listeners.sponge;

import com.georgev22.skinoverlay.utilities.player.PlayerObjectSponge7;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class DeveloperInformListener {

    @Listener
    public void onLogin(ClientConnectionEvent.Login loginEvent) {
        new PlayerObjectSponge7(loginEvent.getTargetUser()).developerInform();
    }
}
