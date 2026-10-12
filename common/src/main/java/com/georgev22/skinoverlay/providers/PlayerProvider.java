package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.player.SkinOverlayPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Abstract provider responsible for resolving and managing {@link SkinOverlayPlayer} instances
 * across different platforms (e.g., Bukkit, Bungee, Velocity, etc.).
 */
public abstract class PlayerProvider {

    protected final ConcurrentMap<UUID, SkinOverlayPlayer> playerCache = new ConcurrentHashMap<>();

    /**
     * Resolves an {@link SkinOverlayPlayer} instance from a platform-specific player object.
     *
     * <p>The provided object may represent a player depending on the platform
     * (e.g., {@code org.bukkit.entity.Player}, {@code com.velocitypowered.api.proxy.Player}, etc.).</p>
     *
     * @param player the platform-specific player object
     * @return the corresponding {@link SkinOverlayPlayer}, or {@code null} if not found or invalid
     */
    public abstract @Nullable SkinOverlayPlayer getSkinOverlayPlayer(@NonNull Object player);

    /**
     * Resolves an {@link SkinOverlayPlayer} instance from a {@link CommandIssuer}.
     *
     * <p>This is typically used when handling commands where the sender may
     * or may not be a player.</p>
     *
     * @param commandIssuer the command issuer
     * @return the corresponding {@link SkinOverlayPlayer}, or {@code null} if the issuer is not a player
     */
    public abstract @Nullable SkinOverlayPlayer getSkinOverlayPlayer(@NotNull CommandIssuer commandIssuer);

    /**
     * Resolves an {@link SkinOverlayPlayer} instance by player name.
     *
     * <p>Implementations should consider case sensitivity and offline player handling
     * depending on platform capabilities.</p>
     *
     * @param name the player's name
     * @return the corresponding {@link SkinOverlayPlayer}, or {@code null} if not found
     */
    public abstract @Nullable SkinOverlayPlayer getSkinOverlayPlayer(@NotNull String name);

    /**
     * Resolves an {@link SkinOverlayPlayer} instance by UUID.
     *
     * @param uuid the player's unique identifier
     * @return the corresponding {@link SkinOverlayPlayer}, or {@code null} if not found
     */
    public abstract @Nullable SkinOverlayPlayer getSkinOverlayPlayer(@NotNull UUID uuid);

    /**
     * Retrieves a list of all currently online players.
     *
     * <p>The returned list should only include players that are fully connected
     * and accessible via {@link SkinOverlayPlayer}.</p>
     *
     * @return a list of online {@link SkinOverlayPlayer} instances
     */
    public abstract @NotNull List<SkinOverlayPlayer> getOnlinePlayers();

    /**
     * Checks whether the given {@link SkinOverlayPlayer} is currently online.
     *
     * <p>This method safely handles potential exceptions and null values.</p>
     *
     * @param player the player to check
     * @return {@code true} if the player is online and has a valid underlying player object,
     * otherwise {@code false}
     */
    public boolean isOnline(@NotNull SkinOverlayPlayer player) {
        try {
            return player.isOnline() && player.getPlayer() != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks whether a player with the given name is currently online.
     *
     * <p>This method attempts to resolve the player using {@link #getSkinOverlayPlayer(String)}
     * and then delegates to {@link #isOnline(SkinOverlayPlayer)}.</p>
     *
     * @param name the player's name
     * @return {@code true} if the player is online, otherwise {@code false}
     */
    public boolean isOnline(@NotNull String name) {
        //noinspection ConstantValue
        if (name == null) return false;
        try {
            SkinOverlayPlayer player = getSkinOverlayPlayer(name);
            if (player == null) {
                return false;
            }
            return isOnline(player);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks whether a player with the given UUID is currently online.
     *
     * <p>This method attempts to resolve the player using {@link #getSkinOverlayPlayer(UUID)}
     * and then delegates to {@link #isOnline(SkinOverlayPlayer)}.</p>
     *
     * @param uuid the player's unique identifier
     * @return {@code true} if the player is online, otherwise {@code false}
     */
    public boolean isOnline(@NotNull UUID uuid) {
        //noinspection ConstantValue
        if (uuid == null) return false;
        try {
            SkinOverlayPlayer player = getSkinOverlayPlayer(uuid);
            if (player == null) {
                return false;
            }
            return isOnline(player);
        } catch (Exception e) {
            return false;
        }
    }

    public void remove(UUID uuid) {
        playerCache.remove(uuid);
    }

    public void clear() {
        playerCache.clear();
    }
}