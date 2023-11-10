package com.georgev22.skinoverlay.listeners;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.event.EventPriority;
import com.georgev22.skinoverlay.event.annotations.EventHandler;
import com.georgev22.skinoverlay.event.events.player.PlayerObjectConnectionEvent;
import com.georgev22.skinoverlay.event.events.player.PlayerSkinPartOptionsChangedEvent;
import com.georgev22.skinoverlay.event.interfaces.EventListener;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public class PlayerListeners implements EventListener {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onConnection(@NotNull PlayerObjectConnectionEvent event) {
        switch (event.getConnectionType()) {
            case CONNECT -> event.getPlayerObject().playerJoin();
            case DISCONNECT -> event.getPlayerObject().playerQuit();
        }
    }

    public void onSettingsChange(PlayerSkinPartOptionsChangedEvent event) {
        event.getPlayerObject().updateSkin();
    }


}
