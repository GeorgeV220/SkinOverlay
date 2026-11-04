package com.georgev22.skinoverlay.fabric.player;

import com.georgev22.skinoverlay.fabric.command.FabricCommandIssuer;
import com.georgev22.skinoverlay.player.SkinOverlayPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public final class FabricSkinOverlayPlayer extends FabricCommandIssuer implements SkinOverlayPlayer {

    private final ServerPlayer player;
    private final MinecraftServer server;

    public FabricSkinOverlayPlayer(@NotNull ServerPlayer player, @NotNull MinecraftServer server) {
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
    public <T> T getPlatformPlayer() {
        //noinspection unchecked
        return (T) player;
    }
}
