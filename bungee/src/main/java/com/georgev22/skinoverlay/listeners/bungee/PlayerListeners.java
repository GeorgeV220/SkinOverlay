package com.georgev22.skinoverlay.listeners.bungee;

import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectBungee;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Objects;
import java.util.UUID;

public class PlayerListeners implements Listener {

    @EventHandler
    public void onLogin(PostLoginEvent postLoginEvent) {
        if (!postLoginEvent.getPlayer().isConnected())
            return;
        new PlayerObjectBungee(postLoginEvent.getPlayer()).playerJoin();
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent playerDisconnectEvent) {
        new PlayerObjectBungee(playerDisconnectEvent.getPlayer()).playerQuit();
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent pluginMessageEvent) {
        if (!(pluginMessageEvent.getSender() instanceof Server)) {
            return;
        }
        if (pluginMessageEvent.getTag().equalsIgnoreCase("skinoverlay:messagechannel")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(pluginMessageEvent.getData());
            String subChannel = in.readUTF();
            if (subChannel.equalsIgnoreCase("playerJoin")) {
                UUID playerUUID = UUID.fromString(Objects.requireNonNull(Utilities.decrypt(in.readUTF())));
                SchedulerManager.getScheduler().runTaskAsynchronously(SkinOverlay.getInstance().getClass(), () -> {
                    while (!SkinOverlay.getInstance().getUserManager().getLoadedUsers().containsKey(playerUUID)) {
                        //IGNORE
                    }
                    SkinOverlay.getInstance().getUserManager().getUser(playerUUID).thenAccept(user -> {
                        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(playerUUID);
                        if (proxiedPlayer != null && proxiedPlayer.isConnected()) {
                            PlayerObject playerObject = new PlayerObjectBungee(proxiedPlayer);
                            playerObject.updateSkin();
                        }
                    });
                });
            }
        }
    }
}

