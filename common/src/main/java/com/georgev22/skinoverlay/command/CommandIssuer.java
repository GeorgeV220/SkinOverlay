package com.georgev22.skinoverlay.command;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface CommandIssuer {

    boolean isPlayer();

    void sendMessage(@NotNull String message);

    default void sendMessage(String @NotNull ... messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    void sendMessage(@NotNull Component component);

    default void sendMessage(Component @NotNull ... components) {
        for (Component component : components) {
            sendMessage(component);
        }
    }

    @NotNull <T> T getIssuer();

    boolean hasPermission(String permission);

    boolean isOp();

    UUID getUniqueId();

    String getName();

}
