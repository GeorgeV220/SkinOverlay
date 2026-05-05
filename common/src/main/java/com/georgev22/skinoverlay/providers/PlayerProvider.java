package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.player.SPlayer;

import java.util.List;
import java.util.UUID;

/**
 * Abstract provider responsible for resolving and managing {@link SPlayer} instances
 * across different platforms (e.g., Bukkit, Bungee, Velocity, etc.).
 */
public abstract class PlayerProvider {

    /**
     * Resolves an {@link SPlayer} instance from a platform-specific player object.
     *
     * <p>The provided object may represent a player depending on the platform
     * (e.g., {@code org.bukkit.entity.Player}, {@code com.velocitypowered.api.proxy.Player}, etc.).</p>
     *
     * @param player the platform-specific player object
     * @return the corresponding {@link SPlayer}, or {@code null} if not found or invalid
     */
    public abstract SPlayer getSPlayer(Object player);

    /**
     * Resolves an {@link SPlayer} instance from a {@link CommandIssuer}.
     *
     * <p>This is typically used when handling commands where the sender may
     * or may not be a player.</p>
     *
     * @param commandIssuer the command issuer
     * @return the corresponding {@link SPlayer}, or {@code null} if the issuer is not a player
     */
    public abstract SPlayer getSPlayer(CommandIssuer commandIssuer);

    /**
     * Resolves an {@link SPlayer} instance by player name.
     *
     * <p>Implementations should consider case sensitivity and offline player handling
     * depending on platform capabilities.</p>
     *
     * @param name the player's name
     * @return the corresponding {@link SPlayer}, or {@code null} if not found
     */
    public abstract SPlayer getSPlayer(String name);

    /**
     * Resolves an {@link SPlayer} instance by UUID.
     *
     * @param uuid the player's unique identifier
     * @return the corresponding {@link SPlayer}, or {@code null} if not found
     */
    public abstract SPlayer getSPlayer(UUID uuid);

    /**
     * Retrieves a list of all currently online players.
     *
     * <p>The returned list should only include players that are fully connected
     * and accessible via {@link SPlayer}.</p>
     *
     * @return a list of online {@link SPlayer} instances
     */
    public abstract List<SPlayer> getOnlinePlayers();

    /**
     * Checks whether the given {@link SPlayer} is currently online.
     *
     * <p>This method safely handles potential exceptions and null values.</p>
     *
     * @param player the player to check
     * @return {@code true} if the player is online and has a valid underlying player object,
     * otherwise {@code false}
     */
    public boolean isOnline(SPlayer player) {
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
    public boolean isOnline(String name) {
        try {
            SPlayer player = getSPlayer(name);
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
    public boolean isOnline(UUID uuid) {
        try {
            SPlayer player = getSPlayer(uuid);
            return isOnline(player);
        } catch (Exception e) {
            return false;
        }
    }
}