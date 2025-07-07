package com.georgev22.skinoverlay.player;

import com.georgev22.skinoverlay.command.BukkitCommandIssuer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BukkitSPlayer extends BukkitCommandIssuer implements SPlayer {

    private final Player player;

    public BukkitSPlayer(@NotNull Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public boolean isOnline() {
        return this.player.isOnline();
    }

    @Override
    public <T> T getPlayer() {
        //noinspection unchecked
        return (T) this.player;
    }
}
