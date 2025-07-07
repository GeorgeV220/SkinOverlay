package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.player.VelocitySPlayer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class VelocityPlayerProvider extends PlayerProvider {

    private final ProxyServer server;

    public VelocityPlayerProvider(ProxyServer server) {
        this.server = server;
    }

    @Override
    public SPlayer getSPlayer(Object player) {
        if (player instanceof Player velocityPlayer) {
            return new VelocitySPlayer(velocityPlayer);
        }
        if (player instanceof String name) {
            Optional<Player> onlinePlayer = this.server.getPlayer(name);
            if (onlinePlayer.isPresent()) {
                return new VelocitySPlayer(onlinePlayer.get());
            }
            throw new IllegalArgumentException("Player " + name + " is not online");
        }
        if (player instanceof UUID uuid) {
            Optional<Player> onlinePlayer = this.server.getPlayer(uuid);
            if (onlinePlayer.isPresent()) {
                return new VelocitySPlayer(onlinePlayer.get());
            }
            throw new IllegalArgumentException("Player " + uuid + " is not online");
        }
        if (player instanceof CommandIssuer commandIssuer) {
            if (!commandIssuer.isPlayer()) {
                throw new IllegalArgumentException("CommandIssuer is not a player");
            }
            return new VelocitySPlayer(commandIssuer.getIssuer());
        }
        return null;
    }

    @Override
    public SPlayer getSPlayer(CommandIssuer commandIssuer) {
        if (!commandIssuer.isPlayer()) {
            throw new IllegalArgumentException("CommandIssuer is not a player");
        }
        return new VelocitySPlayer(commandIssuer.getIssuer());
    }

    @Override
    public SPlayer getSPlayer(String name) {
        Optional<Player> onlinePlayer = server.getPlayer(name);
        if (onlinePlayer.isPresent()) {
            return new VelocitySPlayer(onlinePlayer.get());
        }
        throw new IllegalArgumentException("Player " + name + " is not online");
    }

    @Override
    public SPlayer getSPlayer(UUID uuid) {
        Optional<Player> onlinePlayer = server.getPlayer(uuid);
        if (onlinePlayer.isPresent()) {
            return new VelocitySPlayer(onlinePlayer.get());
        }
        throw new IllegalArgumentException("Player " + uuid + " is not online");
    }

    @Override
    public List<SPlayer> getOnlinePlayers() {
        return new ArrayList<>(
                server.getAllPlayers().stream()
                        .map(VelocitySPlayer::new)
                        .toList()
        );
    }
}
