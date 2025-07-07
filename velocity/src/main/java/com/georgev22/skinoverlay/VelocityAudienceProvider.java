package com.georgev22.skinoverlay;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VelocityAudienceProvider implements AudienceProvider {

    private final ProxyServer server;
    private final Set<Player> players = ConcurrentHashMap.newKeySet();

    public VelocityAudienceProvider(@NotNull Object plugin, @NotNull ProxyServer server) {
        this.server = server;
        server.getEventManager().register(plugin, this);

        players.addAll(server.getAllPlayers());
    }

    @Subscribe
    public void onPlayerJoin(@NotNull LoginEvent event) {
        players.add(event.getPlayer());
    }

    @Subscribe
    public void onPlayerLeave(@NotNull DisconnectEvent event) {
        players.remove(event.getPlayer());
    }

    @Override
    public @NotNull Audience all() {
        Collection<Audience> all = new ArrayList<>(players);
        all.add(server.getConsoleCommandSource());
        return Audience.audience(all);
    }

    @Override
    public @NotNull Audience console() {
        return server.getConsoleCommandSource();
    }

    @Override
    public @NotNull Audience players() {
        return Audience.audience(players);
    }

    @Override
    public @NotNull Audience player(@NotNull UUID playerId) {
        return server.getPlayer(playerId).<Audience>map(p -> p).orElse(Audience.empty());
    }

    @Override
    public @NotNull Audience permission(@NotNull String permission) {
        return Audience.audience(
                players.stream()
                        .filter(p -> p.hasPermission(permission))
                        .toList()
        );
    }

    @Override
    public @NotNull Audience world(@NotNull Key world) {
        // Velocity does not support worlds directly. Return empty.
        return Audience.empty();
    }

    @Override
    public @NotNull Audience server(@NotNull String serverName) {
        return Audience.audience(
                players.stream()
                        .filter(player -> player.getCurrentServer()
                                .map(serverConn -> serverConn.getServerInfo().getName().equals(serverName))
                                .orElse(false))
                        .toList()
        );
    }

    @Override
    public @NotNull ComponentFlattener flattener() {
        return ComponentFlattener.basic();
    }

    @Override
    public void close() {
        players.clear();
    }
}
