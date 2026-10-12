package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.player.BukkitSkinOverlayPlayer;
import com.georgev22.skinoverlay.player.SkinOverlayPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BukkitPlayerProvider extends PlayerProvider {

    private SkinOverlayPlayer getOrCreate(@NonNull Player player) {
        return playerCache.computeIfAbsent(
                player.getUniqueId(),
                uuid -> new BukkitSkinOverlayPlayer(player)
        );
    }

    @Override
    public SkinOverlayPlayer getSkinOverlayPlayer(@NonNull Object player) {
        if (player instanceof Player bukkitPlayer) {
            return getOrCreate(bukkitPlayer);
        }

        if (player instanceof OfflinePlayer offlinePlayer) {
            if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                return getOrCreate(offlinePlayer.getPlayer());
            }
            return null;
        }

        if (player instanceof String name) {
            return getSkinOverlayPlayer(name);
        }

        if (player instanceof UUID uuid) {
            return getSkinOverlayPlayer(uuid);
        }

        if (player instanceof CommandIssuer commandIssuer) {
            return getSkinOverlayPlayer(commandIssuer);
        }

        return null;
    }

    @Override
    public SkinOverlayPlayer getSkinOverlayPlayer(@NotNull CommandIssuer commandIssuer) {
        if (!commandIssuer.isPlayer()) {
            return null;
        }

        return getOrCreate(commandIssuer.getIssuer());
    }

    @Override
    public SkinOverlayPlayer getSkinOverlayPlayer(@NonNull String name) {
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            return getOrCreate(player);
        }

        return null;
    }

    @Override
    public SkinOverlayPlayer getSkinOverlayPlayer(@NonNull UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return getOrCreate(player);
        }

        return null;
    }

    @Override
    public @NonNull List<SkinOverlayPlayer> getOnlinePlayers() {
        return new ArrayList<>(
                Bukkit.getOnlinePlayers().stream()
                        .map(this::getOrCreate)
                        .toList()
        );
    }
}