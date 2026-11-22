package com.georgev22.skinoverlay.listeners.velocity;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.event.events.player.SkinOverlayPlayerJoinEvent;
import com.georgev22.skinoverlay.event.events.player.SkinOverlayPlayerLeaveEvent;
import com.georgev22.skinoverlay.player.SkinOverlayPlayer;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;

public class PlayerListeners {

    private final SkinOverlay mainPlugin = SkinOverlay.getInstance();

    @Subscribe
    public void onPlayerJoin(LoginEvent event) {
        SkinOverlayPlayer player = mainPlugin.getPlayerProvider().getSkinOverlayPlayer(event.getPlayer());
        if (player != null) {
            mainPlugin.getEventBus().post(new SkinOverlayPlayerJoinEvent(player));
        }
    }

    @Subscribe
    public void onPlayerQuit(DisconnectEvent event) {
        SkinOverlayPlayer player = mainPlugin.getPlayerProvider().getSkinOverlayPlayer(event.getPlayer());
        if (player != null) {
            mainPlugin.getEventBus().post(new SkinOverlayPlayerLeaveEvent(player));
        }
    }

}
