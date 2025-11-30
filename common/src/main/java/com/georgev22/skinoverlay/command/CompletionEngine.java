package com.georgev22.skinoverlay.command;

import com.georgev22.skinoverlay.command.annotation.Argument;
import com.georgev22.skinoverlay.command.annotation.CommandCompletion;
import com.georgev22.skinoverlay.command.resolvers.ArgumentResolver;
import com.georgev22.skinoverlay.command.resolvers.PlayersResolver;
import com.georgev22.skinoverlay.command.resolvers.RangeResolver;
import com.georgev22.skinoverlay.maps.ConcurrentObjectMap;
import com.georgev22.skinoverlay.maps.ObjectMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * The {@code CompletionEngine} class provides a unified tab-completion system
 * for the command framework.
 * <p>
 * It supports both static completions (literal strings) and dynamic completions
 * via registered {@link ArgumentResolver}s. Command classes can declare their
 * completion pattern using the {@link CommandCompletion} annotation, while
 * custom resolvers can be registered globally.
 * <p>
 * Default resolvers include:
 * <ul>
 *     <li>{@code @players} — provides online player names.</li>
 *     <li>{@code @range:min-max} — provides numeric ranges.</li>
 * </ul>
 *
 * @see ArgumentResolver
 * @see CommandCompletion
 */
public class CompletionEngine {

    /**
     * Holds all registered argument resolvers, mapped by lowercase key.
     */
    private static final ObjectMap<String, ArgumentResolver> resolvers = new ConcurrentObjectMap<>();

    static {
        registerDefaultResolvers();
    }

    /**
     * Resolves possible tab completions for a command class and current user input.
     *
     * @param clazz         the command class annotated with {@link CommandCompletion}
     * @param commandIssuer the command issuer executing the command
     * @param args          the current arguments typed by the user
     * @return a collection of completions filtered and sorted to match the current input;
     * returns an empty collection if no matches are found or the class lacks {@link CommandCompletion}
     */
    public static @NotNull Collection<String> resolveCompletions(
            @NotNull Class<?> clazz,
            CommandIssuer commandIssuer,
            String[] args
    ) {
        if (!clazz.isAnnotationPresent(CommandCompletion.class)) {
            return new ArrayList<>();
        }

        String pattern = clazz.getAnnotation(CommandCompletion.class).value();
        String[] parts = pattern.split(" ");

        if (args.length > parts.length) {
            return new ArrayList<>();
        }

        String segment = parts[args.length - 1];
        return getStrings(commandIssuer, args, segment);
    }

    /**
     * Resolves possible tab completions for a method's arguments.
     *
     * <p>This method checks parameters annotated with {@link Argument} and uses
     * the {@code completion} property to provide tab completions.</p>
     *
     * @param method        the method whose parameters are being completed
     * @param commandIssuer the command issuer executing the command
     * @param args          the current arguments typed by the user
     * @return a collection of completions filtered and sorted to match the current input;
     * returns an empty collection if no matches are found or if no completion is defined
     */
    public static @NotNull Collection<String> resolveCompletions(
            @NotNull Method method,
            CommandIssuer commandIssuer,
            String @NotNull [] args
    ) {
        Parameter[] parameters = Arrays.stream(method.getParameters())
                .filter(p -> p.isAnnotationPresent(Argument.class))
                .toArray(Parameter[]::new);

        if (args.length > parameters.length) {
            return List.of();
        }

        Parameter param = parameters[args.length - 1];
        Argument argAnno = param.getAnnotation(Argument.class);
        if (argAnno == null) {
            return List.of();
        }

        String completion = argAnno.completion();
        if (completion.isEmpty()) {
            return List.of();
        }

        return getStrings(commandIssuer, args, completion);
    }

    /**
     * Resolves an {@link ArgumentResolver} by its registered key.
     *
     * @param key the resolver key to look up — must start with {@code '@'} (case-insensitive)
     * @return the corresponding {@link ArgumentResolver}, or {@code null} if none is found
     */
    public static @Nullable ArgumentResolver resolve(@NotNull String key) {
        if (!key.startsWith("@")) {
            return null;
        }

        String baseKey = key.toLowerCase(Locale.ROOT);
        int colonIndex = baseKey.indexOf(':');
        if (colonIndex != -1) {
            baseKey = baseKey.substring(0, colonIndex);
        }

        return resolvers.get(baseKey);
    }

    /**
     * Registers a new {@link ArgumentResolver} for the given key.
     *
     * @param key      the resolver key to register — must start with {@code '@'}
     * @param resolver the {@link ArgumentResolver} instance to associate with the key
     * @throws IllegalArgumentException if {@code key} does not start with {@code '@'}
     * @throws NullPointerException     if {@code resolver} is {@code null}
     */
    public static void registerResolver(@NotNull String key, @NotNull ArgumentResolver resolver) {
        if (!key.startsWith("@")) {
            throw new IllegalArgumentException("Resolver key must start with '@' to avoid colliding with subcommands: " + key);
        }
        resolvers.put(key.toLowerCase(Locale.ROOT), Objects.requireNonNull(resolver, "resolver"));
    }

    /**
     * Unregisters an existing {@link ArgumentResolver} by key.
     *
     * @param key the resolver key to remove — must start with {@code '@'}
     */
    public static void unregisterResolver(@NotNull String key) {
        resolvers.remove(key.toLowerCase(Locale.ROOT));
    }

    /**
     * Registers the built-in default argument resolvers.
     *
     * <p>Currently includes:</p>
     * <ul>
     *     <li>{@code @players} — resolves online player names.</li>
     *     <li>{@code @range} — resolves integer ranges (e.g., 1–10).</li>
     * </ul>
     */
    public static void registerDefaultResolvers() {
        registerResolver("@players", new PlayersResolver());
        registerResolver("@range", new RangeResolver());
    }

    /**
     * Processes a completion segment and returns matching suggestions.
     *
     * <p>The segment may contain literal options separated by {@code |} and/or
     * dynamic resolver keys prefixed with {@code @}. If a resolver key contains
     * a colon, the part after the colon is passed as an argument to the resolver.</p>
     *
     * <p>Matches are filtered based on the current input (last element of {@code args})
     * and sorted with exact matches first, then by the index of the input in the option.</p>
     *
     * @param commandIssuer the issuer of the command
     * @param args          the current arguments typed by the user
     * @param segment       the completion segment (literal options and/or resolver keys)
     * @return an unmodifiable collection of matching completion strings
     */
    private static @NotNull @Unmodifiable Collection<String> getStrings(CommandIssuer commandIssuer, String[] args, @NotNull String segment) {
        Collection<String> completions = new ArrayList<>();

        for (String option : segment.split("\\|")) {
            if (option.startsWith("@")) {
                String[] resolverParts = option.split(":", 2);
                String resolverName = resolverParts[0];
                String[] resolverArgs = resolverParts.length > 1 ? new String[]{resolverParts[1]} : new String[0];

                ArgumentResolver resolver = resolve(resolverName);
                if (resolver != null) {
                    completions.addAll(resolver.resolve(commandIssuer, resolverArgs));
                }
            } else {
                completions.add(option);
            }
        }

        String currentInput = args[args.length - 1];
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(currentInput.toLowerCase()))
                .sorted(Comparator.comparingInt(s -> s.equalsIgnoreCase(currentInput) ? 0 : s.toLowerCase().indexOf(currentInput.toLowerCase())))
                .toList();
    }
}
