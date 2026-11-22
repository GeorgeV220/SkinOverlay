package com.georgev22.skinoverlay.command.resolvers;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.registry.OverlayRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Suggests registered overlay identifiers for comma-separated command arguments.
 * <p>
 * Input-aware suggestions retain everything before the last comma so the completion engine
 * can match the entire argument while completing the next model-qualified identifier.
 * </p>
 */
public final class OverlayListResolver implements ArgumentResolver {
    private final OverlayRegistry registry;

    /**
     * Creates a resolver that reads the current registry on each completion request.
     *
     * @param registry registry supplying configured and generated overlay identifiers
     */
    public OverlayListResolver(OverlayRegistry registry) {
        this.registry = registry;
    }

    /**
     * Returns all registered identifiers without a comma prefix.
     *
     * @param issuer sender requesting suggestions
     * @param args   configured resolver arguments, unused by this resolver
     * @return the registry's current model-qualified identifiers
     */
    @Override
    public Collection<String> resolve(@NotNull CommandIssuer issuer, @NotNull String... args) {
        return registry.entries().keySet();
    }

    /**
     * Appends each registered identifier to the current argument's completed comma prefix.
     *
     * @param issuer sender requesting suggestions
     * @param input  current command arguments, including the argument being completed
     * @param args   configured resolver arguments, unused by this resolver
     * @return candidates containing the unchanged prefix and a model-qualified identifier
     */
    @Override
    public Collection<String> suggest(@NotNull CommandIssuer issuer, String @NotNull [] input,
                                      @NotNull String... args) {
        String current = input.length == 0 ? "" : input[input.length - 1];
        String prefix = current.substring(0, current.lastIndexOf(',') + 1);
        return registry.entries().keySet().stream().map(key -> prefix + key).toList();
    }
}
