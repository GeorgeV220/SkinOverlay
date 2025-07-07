package com.georgev22.skinoverlay.command.resolvers;

import com.georgev22.skinoverlay.command.CommandIssuer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@FunctionalInterface
public interface ArgumentResolver {
    Collection<String> resolve(@NotNull CommandIssuer commandIssuer, @NotNull String... args);
}
