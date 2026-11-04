package com.georgev22.skinoverlay.fabric.player;

import com.georgev22.skinoverlay.fabric.command.FabricCommandIssuer;
import com.georgev22.skinoverlay.player.SPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public final class FabricSPlayer extends FabricCommandIssuer implements SPlayer {

    private final ServerPlayer player;
    private final MinecraftServer server;

    public FabricSPlayer(@NotNull ServerPlayer player, @NotNull MinecraftServer server) {
        super(player.createCommandSourceStack());
        this.player = player;
        this.server = server;
    }

    @Override
    public boolean isOnline() {
        return !player.isRemoved()
                && server.getPlayerList().getPlayer(player.getUUID()) == player;
    }

    @Override
    public <T> T getPlayer() {
        //noinspection unchecked
        return (T) player;
    }
}
