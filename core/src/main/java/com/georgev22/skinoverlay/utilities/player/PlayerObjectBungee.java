package com.georgev22.skinoverlay.utilities.player;

import java.util.UUID;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerObjectBungee implements PlayerObject {
    private final ProxiedPlayer proxiedPlayer;

    public PlayerObjectBungee(final ProxiedPlayer proxiedPlayer) {
        this.proxiedPlayer = proxiedPlayer;
    }

    @Override
    public ProxiedPlayer getPlayer() {
        return this.proxiedPlayer;
    }

    @Override
    public UUID playerUUID() {
        return this.proxiedPlayer.getUniqueId();
    }

    @Override
    public String playerName() {
        return this.proxiedPlayer.getName();
    }
}
