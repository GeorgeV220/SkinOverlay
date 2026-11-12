package com.georgev22.skinoverlay.command;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents an entity that can issue commands.
 * <p>
 * Can be a player, console, or any custom command sender implementation.
 */
public interface CommandIssuer {

    /**
     * Checks if the issuer is a player.
     *
     * @return true if the issuer is a player, false otherwise
     */
    boolean isPlayer();

    /**
     * Returns the Kyori Adventure {@link Audience} associated with this issuer.
     *
     * <p>An {@code Audience} represents any target that can receive messages,
     * such as players, the console, command blocks, or other custom implementations.</p>
     *
     * <p>This can be used to send rich chat components, titles, action bars,
     * and other Adventure-supported message types.</p>
     *
     * @return the audience associated with this issuer
     */
    Audience audience();

    /**
     * Returns the underlying issuer object.
     * <p>
     * For example, a Bukkit {@code CommandSender} instance.
     *
     * @param <T> the type of the underlying issuer
     * @return the underlying issuer object
     */
    @NotNull <T> T getIssuer();

    /**
     * Checks if the issuer has a specific permission.
     *
     * @param permission the permission string
     * @return true if the issuer has the permission, false otherwise
     */
    boolean hasPermission(String permission);

    /**
     * Checks if the issuer is an operator.
     *
     * @return true if the issuer is an op, false otherwise
     */
    boolean isOp();

    /**
     * Gets the unique identifier (UUID) of the issuer.
     *
     * @return the UUID of the issuer
     */
    UUID getUniqueId();

    /**
     * Gets the display name of the issuer.
     *
     * @return the name of the issuer
     */
    String getName();
}
