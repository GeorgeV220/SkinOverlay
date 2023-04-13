package com.georgev22.skinoverlay.listeners.velocity;

import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;

import java.util.Objects;
import java.util.UUID;

public class PlayerListeners {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @Subscribe(order = PostOrder.FIRST)
    public void onPostLogin(PostLoginEvent loginEvent) {
        if (!loginEvent.getPlayer().isActive())
            return;
        skinOverlay.getPlayer(loginEvent.getPlayer().getUniqueId()).ifPresent(PlayerObject::playerJoin);
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onQuit(DisconnectEvent playerDisconnectEvent) {
        skinOverlay.getPlayer(playerDisconnectEvent.getPlayer().getUniqueId()).ifPresent(PlayerObject::playerQuit);
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent pluginMessageEvent) {
        if (pluginMessageEvent.getIdentifier().getId().equalsIgnoreCase("skinoverlay:message")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(pluginMessageEvent.dataAsInputStream());
            String subChannel = in.readUTF();
            if (subChannel.equalsIgnoreCase("playerJoin")) {
                UUID playerUUID = UUID.fromString(Objects.requireNonNull(Utilities.decrypt(in.readUTF())));
                SchedulerManager.getScheduler().runTaskAsynchronously(skinOverlay.getClass(), () -> {
                    while (!skinOverlay.getUserManager().getLoadedUsers().containsKey(playerUUID)) {
                        //IGNORE
                    }
                    SchedulerManager.getScheduler().runTask(skinOverlay.getClass(), () -> skinOverlay.getPlayer(playerUUID).ifPresent(PlayerObject::updateSkin));
                });
            }
        }
    }
}

