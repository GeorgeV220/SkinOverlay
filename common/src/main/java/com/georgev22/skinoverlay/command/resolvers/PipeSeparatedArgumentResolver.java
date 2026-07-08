package com.georgev22.skinoverlay.command.resolvers;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.command.CompletionEngine;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PipeSeparatedArgumentResolver implements ArgumentResolver {

    private record ResolverWrapper(ArgumentResolver resolver, String[] args) {
    }

    private final List<ResolverWrapper> resolvers = new ArrayList<>();

    public PipeSeparatedArgumentResolver(@NotNull String pipeSeparated) {
        String[] tokens = pipeSeparated.split("\\|");

        for (String token : tokens) {
            if (token.startsWith("@")) {
                String stripped = token.substring(1);
                String[] parts = stripped.split(":", 2);
                String resolverKey = parts[0];
                String[] resolverArgs = parts.length > 1 ? new String[]{parts[1]} : new String[0];

                ArgumentResolver dynamicResolver = CompletionEngine.resolve(resolverKey);
                if (dynamicResolver != null) {
                    resolvers.add(new ResolverWrapper(dynamicResolver, resolverArgs));
                }
            } else {
                resolvers.add(new ResolverWrapper((sender, args) -> Collections.singletonList(token), new String[0]));
            }
        }
    }

    @Override
    public Collection<String> resolve(@NotNull CommandIssuer commandIssuer, String... ignored) {
        List<String> results = new ArrayList<>();
        for (ResolverWrapper wrapper : resolvers) {
            results.addAll(wrapper.resolver.resolve(commandIssuer, wrapper.args));
        }
        return results;
    }

    @Override
    public Object resolveValue(@NotNull CommandIssuer commandIssuer, @NotNull String arg) {
        for (ResolverWrapper wrapper : resolvers) {
            if (wrapper.resolver == null) continue;

            Collection<String> completions = wrapper.resolver.resolve(commandIssuer, wrapper.args);
            if (completions.contains(arg)) {
                return arg;
            }

            Object value = wrapper.resolver.resolveValue(commandIssuer, arg);
            if (value != null) return value;
        }

        return arg;
    }
}
