package com.georgev22.skinoverlay.player;

import com.georgev22.skinoverlay.command.VelocityCommandIssuer;
import com.velocitypowered.api.proxy.Player;

public class VelocitySkinOverlayPlayer extends VelocityCommandIssuer implements SkinOverlayPlayer {

    private final Player player;

    public VelocitySkinOverlayPlayer(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public boolean isOnline() {
        return this.player.isActive();
    }

    @Override
    public <T> T getPlatformPlayer() {
        return (T) this.player;
    }
}
