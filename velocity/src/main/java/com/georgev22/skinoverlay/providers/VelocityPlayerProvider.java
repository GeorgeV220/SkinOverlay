package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.player.VelocitySPlayer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class VelocityPlayerProvider extends PlayerProvider {

    private final ProxyServer server;

    public VelocityPlayerProvider(ProxyServer server) {
        this.server = server;
    }

    private SPlayer getOrCreate(@NonNull Player player) {
        return playerCache.computeIfAbsent(
                player.getUniqueId(),
                uuid -> new VelocitySPlayer(player)
        );
    }

    @Override
    public SPlayer getSPlayer(@NonNull Object player) {
        if (player instanceof Player velocityPlayer) {
            return getOrCreate(velocityPlayer);
        }

        if (player instanceof String name) {
            return getSPlayer(name);
        }

        if (player instanceof UUID uuid) {
            return getSPlayer(uuid);
        }

        if (player instanceof CommandIssuer commandIssuer) {
            return getSPlayer(commandIssuer);
        }

        return null;
    }

    @Override
    public SPlayer getSPlayer(@NonNull CommandIssuer commandIssuer) {
        if (!commandIssuer.isPlayer()) {
            throw new IllegalArgumentException("CommandIssuer is not a player");
        }

        return getOrCreate(commandIssuer.getIssuer());
    }

    @Override
    public SPlayer getSPlayer(@NonNull String name) {
        Optional<Player> player = server.getPlayer(name);
        if (player.isPresent()) {
            return getOrCreate(player.get());
        }

        throw new IllegalArgumentException("Player " + name + " is not online");
    }

    @Override
    public SPlayer getSPlayer(@NonNull UUID uuid) {
        Optional<Player> player = server.getPlayer(uuid);
        if (player.isPresent()) {
            return getOrCreate(player.get());
        }

        throw new IllegalArgumentException("Player " + uuid + " is not online");
    }

    @Override
    public @NonNull List<SPlayer> getOnlinePlayers() {
        return new ArrayList<>(
                server.getAllPlayers().stream()
                        .map(this::getOrCreate)
                        .toList()
        );
    }
}