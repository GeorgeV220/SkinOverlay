package com.georgev22.skinoverlay.listeners;

import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.event.EventHandler;
import com.georgev22.skinoverlay.event.EventListener;
import com.georgev22.skinoverlay.event.EventPriority;
import com.georgev22.skinoverlay.event.events.player.PlayerObjectConnectionEvent;
import com.georgev22.skinoverlay.event.events.player.PlayerSkinPartOptionsChangedEvent;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
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
                if (OptionsUtil.PROXY.getBooleanValue())
                    SchedulerManager.getScheduler().runTaskAsynchronously(skinOverlay.getClass(), () -> {
                        skinOverlay.getPluginMessageUtils().setChannel("skinoverlay:message");
                        if (playerObject.isOnline())
                            skinOverlay.getPluginMessageUtils().sendDataToPlayer("playerJoin", playerObject, playerObject.playerUUID().toString());
                        else
                            skinOverlay.getLogger().warning("Player " + playerObject.playerName() + " is not online");
                    });
            }
            case DISCONNECT -> {
                event.getPlayerObject().playerQuit();
            }
        }
    }

    public void onSettingsChange(PlayerSkinPartOptionsChangedEvent event) {
        event.getPlayerObject().updateSkin();
    }


}
