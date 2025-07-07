package com.georgev22.skinoverlay.command.resolvers;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.command.CommandManager;
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
        CommandManager manager = SkinOverlay.getInstance().getCommandManager();

        for (String token : tokens) {
            if (token.startsWith("@")) {
                String stripped = token.substring(1);
                String[] parts = stripped.split(":", 2);
                String resolverKey = parts[0];
                String[] resolverArgs = parts.length > 1 ? new String[]{parts[1]} : new String[0];

                ArgumentResolver dynamicResolver = manager.getResolver(resolverKey);
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
}
