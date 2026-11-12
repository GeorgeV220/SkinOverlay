package com.georgev22.skinoverlay.fabric.providers;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.fabric.player.FabricSkinOverlayPlayer;
import com.georgev22.skinoverlay.player.SkinOverlayPlayer;
import com.georgev22.skinoverlay.providers.PlayerProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class FabricPlayerProvider extends PlayerProvider {

    private final MinecraftServer server;

    public FabricPlayerProvider(@NotNull MinecraftServer server) {
        this.server = server;
    }

    private SkinOverlayPlayer getOrCreate(@NotNull ServerPlayer player) {
        return playerCache.computeIfAbsent(player.getUUID(), ignored -> new FabricSkinOverlayPlayer(player, server));
    }

    @Override
    public SkinOverlayPlayer getSkinOverlayPlayer(@NonNull Object player) {
        return switch (player) {
            case ServerPlayer serverPlayer -> getOrCreate(serverPlayer);
            case String name -> getSkinOverlayPlayer(name);
            case UUID uuid -> getSkinOverlayPlayer(uuid);
            case CommandIssuer issuer -> getSkinOverlayPlayer(issuer);
            default -> null;
        };
    }

    @Override
    public SkinOverlayPlayer getSkinOverlayPlayer(@NotNull CommandIssuer issuer) {
        if (!issuer.isPlayer()) {
            return null;
        }
        return getOrCreate(issuer.getIssuer());
    }

    @Override
    public SkinOverlayPlayer getSkinOverlayPlayer(@NotNull String name) {
        ServerPlayer player = server.getPlayerList().getPlayerByName(name);
        return player == null ? null : getOrCreate(player);
    }

    @Override
    public SkinOverlayPlayer getSkinOverlayPlayer(@NotNull UUID uuid) {
        ServerPlayer player = server.getPlayerList().getPlayer(uuid);
        return player == null ? null : getOrCreate(player);
    }

    @Override
    public @NotNull List<SkinOverlayPlayer> getOnlinePlayers() {
        return new ArrayList<>(server.getPlayerList().getPlayers().stream().map(this::getOrCreate).toList());
    }
}
