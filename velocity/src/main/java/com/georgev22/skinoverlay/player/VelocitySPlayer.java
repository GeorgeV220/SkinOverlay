package com.georgev22.skinoverlay.player;

import com.georgev22.skinoverlay.command.VelocityCommandIssuer;
import com.velocitypowered.api.proxy.Player;

public class VelocitySPlayer extends VelocityCommandIssuer implements SPlayer {

    private final Player player;

    public VelocitySPlayer(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public boolean isOnline() {
        return this.player.isActive();
    }

    @Override
    public <T> T getPlayer() {
        return (T) this.player;
    }
}
