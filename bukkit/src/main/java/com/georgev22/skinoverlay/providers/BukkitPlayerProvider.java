package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.player.BukkitSPlayer;
import com.georgev22.skinoverlay.player.SPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BukkitPlayerProvider extends PlayerProvider {

    @Override
    public SPlayer getSPlayer(Object player) {
        if (player instanceof Player bukkitPlayer) {
            return new BukkitSPlayer(bukkitPlayer);
        }
        if (player instanceof OfflinePlayer offlinePlayer) {
            if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                return new BukkitSPlayer(offlinePlayer.getPlayer());
            }
            throw new IllegalArgumentException("Player " + offlinePlayer.getName() + " is offline");
        }
        if (player instanceof String name) {
            Player onlinePlayer = Bukkit.getPlayer(name);
            if (onlinePlayer != null) {
                return new BukkitSPlayer(onlinePlayer);
            }
            throw new IllegalArgumentException("Player " + name + " is null");
        }
        if (player instanceof UUID uuid) {
            Player onlinePlayer = Bukkit.getPlayer(uuid);
            if (onlinePlayer != null) {
                return new BukkitSPlayer(onlinePlayer);
            }
            throw new IllegalArgumentException("Player " + uuid + " is null");
        }
        if (player instanceof CommandIssuer commandIssuer) {
            if (!commandIssuer.isPlayer()) {
                throw new IllegalArgumentException("CommandIssuer is not a player");
            }
            return new BukkitSPlayer(commandIssuer.getIssuer());
        }
        return null;
    }

    @Override
    public SPlayer getSPlayer(@NotNull CommandIssuer commandIssuer) {
        if (!commandIssuer.isPlayer()) {
            throw new IllegalArgumentException("CommandIssuer is not a player");
        }
        return new BukkitSPlayer(commandIssuer.getIssuer());
    }

    @Override
    public SPlayer getSPlayer(String name) {
        Player onlinePlayer = Bukkit.getPlayer(name);
        if (onlinePlayer != null) {
            return new BukkitSPlayer(onlinePlayer);
        }
        throw new IllegalArgumentException("Player " + name + " is null");
    }

    @Override
    public SPlayer getSPlayer(UUID uuid) {
        Player onlinePlayer = Bukkit.getPlayer(uuid);
        if (onlinePlayer != null) {
            return new BukkitSPlayer(onlinePlayer);
        }
        throw new IllegalArgumentException("Player " + uuid + " is null");
    }

    @Override
    public List<SPlayer> getOnlinePlayers() {
        return new ArrayList<>(
                Bukkit.getOnlinePlayers().stream()
                        .map(BukkitSPlayer::new)
                        .toList()
        );
    }
}
