package com.georgev22.skinoverlay.listeners.velocity;

import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.SkinOverlayVelocity;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectVelocity;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class PlayerListeners {

    @Subscribe
    public void onLogin(PostLoginEvent loginEvent) {
        if (!loginEvent.getPlayer().isActive())
            return;
        new PlayerObjectVelocity(loginEvent.getPlayer()).playerJoin();
    }

    @Subscribe
    public void onQuit(DisconnectEvent playerDisconnectEvent) {
        new PlayerObjectVelocity(playerDisconnectEvent.getPlayer()).playerQuit();
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent pluginMessageEvent) {
        if (!(pluginMessageEvent.getSource() instanceof ServerConnection)) {
            return;
        }
        if (pluginMessageEvent.getIdentifier().getId().equalsIgnoreCase("skinoverlay:messagechannel")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(pluginMessageEvent.dataAsInputStream());
            String subChannel = in.readUTF();
            if (subChannel.equalsIgnoreCase("playerJoin")) {
                UUID playerUUID = UUID.fromString(Objects.requireNonNull(Utilities.decrypt(in.readUTF())));
                SchedulerManager.getScheduler().runTaskAsynchronously(SkinOverlay.getInstance().getClass(), () -> {
                    while (!SkinOverlay.getInstance().getUserManager().getLoadedUsers().containsKey(playerUUID)) {
                        //IGNORE
                    }
                    SkinOverlay.getInstance().getUserManager().getUser(playerUUID).thenAccept(user -> {
                        Optional<Player> player = SkinOverlayVelocity.getInstance().getProxy().getPlayer(playerUUID);
                        if (player.isPresent()) {
                            PlayerObject playerObject = new PlayerObjectVelocity(player.get());
                            playerObject.updateSkin();
                        }
                    });
                });
            }
        }
    }
}

