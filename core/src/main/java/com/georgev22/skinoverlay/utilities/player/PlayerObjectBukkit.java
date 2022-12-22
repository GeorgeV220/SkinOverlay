package com.georgev22.skinoverlay.utilities.player;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

public class PlayerObjectBukkit implements PlayerObject {
    private final OfflinePlayer offlinePlayer;

    public PlayerObjectBukkit(final OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
    }

    @Override
    public OfflinePlayer getPlayer() {
        return this.offlinePlayer;
    }

    @Override
    public UUID playerUUID() {
        return this.offlinePlayer.getUniqueId();
    }

    @Override
    public String playerName() {
        return this.offlinePlayer.getName();
    }
}
