package com.georgev22.skinoverlay.command.resolvers;

import com.georgev22.skinoverlay.command.CommandIssuer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@FunctionalInterface
public interface ArgumentResolver {

    /**
     * Returns a list of possible completions for tab-completion.
     */
    Collection<String> resolve(@NotNull CommandIssuer commandIssuer, @NotNull String... args);

    /**
     * Resolves the actual argument value to pass to the method.
     * By default, returns the raw string.
     */
    default Object resolveValue(@NotNull CommandIssuer commandIssuer, @NotNull String arg) {
        return arg;
    }
}

