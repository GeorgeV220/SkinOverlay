package com.georgev22.skinoverlay.listeners.bungee;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.event.events.player.PlayerObjectConnectionEvent;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.UUID;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public class PlayerListeners implements Listener {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @EventHandler(priority = 1)
    public void onLogin(PostLoginEvent postLoginEvent) {
        if (!postLoginEvent.getPlayer().isConnected())
            return;
        skinOverlay.getEventManager().callEvent(
                new PlayerObjectConnectionEvent(
                        skinOverlay.getPlayer(postLoginEvent.getPlayer().getUniqueId()).orElseThrow(),
                        PlayerObjectConnectionEvent.ConnectionType.CONNECT,
                        true
                )
        );
    }

    @EventHandler(priority = 1)
    public void onQuit(PlayerDisconnectEvent playerDisconnectEvent) {
        skinOverlay.getEventManager().callEvent(
                new PlayerObjectConnectionEvent(
                        skinOverlay.getPlayer(playerDisconnectEvent.getPlayer().getUniqueId()).orElseThrow(),
                        PlayerObjectConnectionEvent.ConnectionType.DISCONNECT,
                        true
                )
        );
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent pluginMessageEvent) {
        if (!(pluginMessageEvent.getSender() instanceof Server)) {
            return;
        }
        if (pluginMessageEvent.getTag().equalsIgnoreCase("skinoverlay:message")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(pluginMessageEvent.getData());
            String subChannel = in.readUTF();
            if (subChannel.equalsIgnoreCase("playerJoin")) {
                UUID playerUUID = UUID.fromString(Objects.requireNonNull(Utilities.decrypt(in.readUTF())));
                this.skinOverlay.getMinecraftScheduler().runTask(skinOverlay.getPlugin(), () -> skinOverlay.getPlayer(playerUUID).ifPresent(PlayerObject::updateSkin));
            }
        }
    }
}

