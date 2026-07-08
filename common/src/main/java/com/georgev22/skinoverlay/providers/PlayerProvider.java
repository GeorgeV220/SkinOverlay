package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.player.SPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Abstract provider responsible for resolving and managing {@link SPlayer} instances
 * across different platforms (e.g., Bukkit, Bungee, Velocity, etc.).
 */
public abstract class PlayerProvider {

    protected final ConcurrentMap<UUID, SPlayer> playerCache = new ConcurrentHashMap<>();

    /**
     * Resolves an {@link SPlayer} instance from a platform-specific player object.
     *
     * <p>The provided object may represent a player depending on the platform
     * (e.g., {@code org.bukkit.entity.Player}, {@code com.velocitypowered.api.proxy.Player}, etc.).</p>
     *
     * @param player the platform-specific player object
     * @return the corresponding {@link SPlayer}, or {@code null} if not found or invalid
     */
    public abstract @Nullable SPlayer getSPlayer(@NonNull Object player);

    /**
     * Resolves an {@link SPlayer} instance from a {@link CommandIssuer}.
     *
     * <p>This is typically used when handling commands where the sender may
     * or may not be a player.</p>
     *
     * @param commandIssuer the command issuer
     * @return the corresponding {@link SPlayer}, or {@code null} if the issuer is not a player
     */
    public abstract @Nullable SPlayer getSPlayer(@NotNull CommandIssuer commandIssuer);

    /**
     * Resolves an {@link SPlayer} instance by player name.
     *
     * <p>Implementations should consider case sensitivity and offline player handling
     * depending on platform capabilities.</p>
     *
     * @param name the player's name
     * @return the corresponding {@link SPlayer}, or {@code null} if not found
     */
    public abstract @Nullable SPlayer getSPlayer(@NotNull String name);

    /**
     * Resolves an {@link SPlayer} instance by UUID.
     *
     * @param uuid the player's unique identifier
     * @return the corresponding {@link SPlayer}, or {@code null} if not found
     */
    public abstract @Nullable SPlayer getSPlayer(@NotNull UUID uuid);

    /**
     * Retrieves a list of all currently online players.
     *
     * <p>The returned list should only include players that are fully connected
     * and accessible via {@link SPlayer}.</p>
     *
     * @return a list of online {@link SPlayer} instances
     */
    public abstract @NotNull List<SPlayer> getOnlinePlayers();

    /**
     * Checks whether the given {@link SPlayer} is currently online.
     *
     * <p>This method safely handles potential exceptions and null values.</p>
     *
     * @param player the player to check
     * @return {@code true} if the player is online and has a valid underlying player object,
     * otherwise {@code false}
     */
    public boolean isOnline(@NotNull SPlayer player) {
        try {
            return player.isOnline() && player.getPlayer() != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks whether a player with the given name is currently online.
     *
     * <p>This method attempts to resolve the player using {@link #getSPlayer(String)}
     * and then delegates to {@link #isOnline(SPlayer)}.</p>
     *
     * @param name the player's name
     * @return {@code true} if the player is online, otherwise {@code false}
     */
    public boolean isOnline(@NotNull String name) {
        //noinspection ConstantValue
        if (name == null) return false;
        try {
            SPlayer player = getSPlayer(name);
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
     * <p>This method attempts to resolve the player using {@link #getSPlayer(UUID)}
     * and then delegates to {@link #isOnline(SPlayer)}.</p>
     *
     * @param uuid the player's unique identifier
     * @return {@code true} if the player is online, otherwise {@code false}
     */
    public boolean isOnline(@NotNull UUID uuid) {
        //noinspection ConstantValue
        if (uuid == null) return false;
        try {
            SPlayer player = getSPlayer(uuid);
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