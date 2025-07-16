package com.georgev22.skinoverlay.listeners.velocity;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.event.events.player.SPlayerJoinEvent;
import com.georgev22.skinoverlay.event.events.player.SPlayerLeaveEvent;
import com.georgev22.skinoverlay.player.SPlayer;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;

public class PlayerListeners {

    private final SkinOverlay mainPlugin = SkinOverlay.getInstance();

    @Subscribe
    public void onPlayerJoin(LoginEvent event) {
        SPlayer player = mainPlugin.getPlayerProvider().getSPlayer(event.getPlayer());
        if (player != null) {
            mainPlugin.getEventBus().post(new SPlayerJoinEvent(player));
        }
    }

    @Subscribe
    public void onPlayerQuit(DisconnectEvent event) {
        SPlayer player = mainPlugin.getPlayerProvider().getSPlayer(event.getPlayer());
        if (player != null) {
            mainPlugin.getEventBus().post(new SPlayerLeaveEvent(player));
        }
    }

}
