package com.georgev22.skinoverlay.command;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.annotation.CommandCompletion;
import com.georgev22.skinoverlay.command.resolvers.ArgumentResolver;

import java.util.ArrayList;
import java.util.Collection;

public class CompletionEngine {

    public static Collection<String> resolveCompletions(Class<?> clazz, CommandIssuer commandIssuer, String[] args) {
        if (!clazz.isAnnotationPresent(CommandCompletion.class)) {
            return new ArrayList<>();
        }

        String pattern = clazz.getAnnotation(CommandCompletion.class).value();
        String[] parts = pattern.split(" ");

        if (args.length > parts.length) {
            return new ArrayList<>();
        }

        String segment = parts[args.length - 1];
        Collection<String> completions = new ArrayList<>();

        for (String option : segment.split("\\|")) {
            if (option.startsWith("@")) {
                String resolverKey = option.substring(1);
                String[] resolverParts = resolverKey.split(":", 2);
                String resolverName = resolverParts[0];
                String[] resolverArgs = resolverParts.length > 1 ? new String[]{resolverParts[1]} : new String[0];

                ArgumentResolver resolver = SkinOverlay.getInstance().getCommandManager().getResolver(resolverName);
                if (resolver != null) {
                    completions.addAll(resolver.resolve(commandIssuer, resolverArgs));
                }
            } else {
                completions.add(option);
            }
        }

        return completions;
    }
}
