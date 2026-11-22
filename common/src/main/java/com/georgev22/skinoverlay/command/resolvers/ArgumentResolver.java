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
     * Produces suggestions with access to the command arguments currently being completed.
     * <p>
     * The default implementation delegates to {@link #resolve(CommandIssuer, String...)} using
     * only the resolver's configured arguments, preserving existing resolver behavior.
     * </p>
     *
     * @param commandIssuer sender requesting suggestions
     * @param input         arguments at the current command level, including the unfinished last argument
     * @param args          resolver-specific arguments supplied after a colon in the completion pattern
     * @return candidate suggestions before the completion engine filters them against the current input
     */
    default Collection<String> suggest(@NotNull CommandIssuer commandIssuer, String @NotNull [] input,
                                       @NotNull String... args) {
        return resolve(commandIssuer, args);
    }

    /**
     * Resolves the actual argument value to pass to the method.
     * By default, returns the raw string.
     */
    default Object resolveValue(@NotNull CommandIssuer commandIssuer, @NotNull String arg) {
        return arg;
    }
}

