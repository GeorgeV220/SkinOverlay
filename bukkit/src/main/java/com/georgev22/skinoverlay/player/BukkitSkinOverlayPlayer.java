package com.georgev22.skinoverlay.player;

import com.georgev22.skinoverlay.command.BukkitCommandIssuer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BukkitSkinOverlayPlayer extends BukkitCommandIssuer implements SkinOverlayPlayer {

    private final Player player;

    public BukkitSkinOverlayPlayer(@NotNull Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public boolean isOnline() {
        return this.player.isOnline();
    }

    @Override
    public <T> T getPlatformPlayer() {
        //noinspection unchecked
        return (T) this.player;
    }
}
