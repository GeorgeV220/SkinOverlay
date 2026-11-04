package com.georgev22.skinoverlay.fabric.providers;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.fabric.player.FabricSPlayer;
import com.georgev22.skinoverlay.player.SPlayer;
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

    private SPlayer getOrCreate(@NotNull ServerPlayer player) {
        return playerCache.computeIfAbsent(player.getUUID(), ignored -> new FabricSPlayer(player, server));
    }

    @Override
    public SPlayer getSPlayer(@NonNull Object player) {
        return switch (player) {
            case ServerPlayer serverPlayer -> getOrCreate(serverPlayer);
            case String name -> getSPlayer(name);
            case UUID uuid -> getSPlayer(uuid);
            case CommandIssuer issuer -> getSPlayer(issuer);
            default -> null;
        };
    }

    @Override
    public SPlayer getSPlayer(@NotNull CommandIssuer issuer) {
        if (!issuer.isPlayer()) {
            return null;
        }
        return getOrCreate(issuer.getIssuer());
    }

    @Override
    public SPlayer getSPlayer(@NotNull String name) {
        ServerPlayer player = server.getPlayerList().getPlayerByName(name);
        return player == null ? null : getOrCreate(player);
    }

    @Override
    public SPlayer getSPlayer(@NotNull UUID uuid) {
        ServerPlayer player = server.getPlayerList().getPlayer(uuid);
        return player == null ? null : getOrCreate(player);
    }

    @Override
    public @NotNull List<SPlayer> getOnlinePlayers() {
        return new ArrayList<>(server.getPlayerList().getPlayers().stream().map(this::getOrCreate).toList());
    }
}
