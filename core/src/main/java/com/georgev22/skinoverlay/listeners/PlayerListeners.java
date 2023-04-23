package com.georgev22.skinoverlay.listeners;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.event.annotations.EventHandler;
import com.georgev22.skinoverlay.event.interfaces.EventListener;
import com.georgev22.skinoverlay.event.EventPriority;
import com.georgev22.skinoverlay.event.events.player.PlayerObjectConnectionEvent;
import com.georgev22.skinoverlay.event.events.player.PlayerSkinPartOptionsChangedEvent;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.jetbrains.annotations.NotNull;

public class PlayerListeners implements EventListener {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onConnection(@NotNull PlayerObjectConnectionEvent event) {
        switch (event.getConnectionType()) {
            case CONNECT -> {
                PlayerObject playerObject = event.getPlayerObject();
                playerObject.playerJoin();
            }
            case DISCONNECT -> event.getPlayerObject().playerQuit();
        }
    }

    public void onSettingsChange(PlayerSkinPartOptionsChangedEvent event) {
        event.getPlayerObject().updateSkin();
    }


}
