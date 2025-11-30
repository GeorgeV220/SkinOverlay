package com.georgev22.skinoverlay.command;

import net.kyori.adventure.text.Component;
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
     * Sends a plain text message to the issuer.
     *
     * @param message the message to send
     */
    void sendMessage(@NotNull String message);

    /**
     * Sends multiple plain text messages to the issuer.
     *
     * @param messages the messages to send
     */
    default void sendMessage(String @NotNull ... messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    /**
     * Sends a rich text component to the issuer.
     *
     * @param component the component to send
     */
    void sendMessage(@NotNull Component component);

    /**
     * Sends multiple rich text components to the issuer.
     *
     * @param components the components to send
     */
    default void sendMessage(Component @NotNull ... components) {
        for (Component component : components) {
            sendMessage(component);
        }
    }

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
