package com.georgev22.skinoverlay.message;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.hooks.placeholder.PlaceholderHook;
import com.georgev22.skinoverlay.utilities.Utils;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for handling placeholder replacements and
 * inline conditional text switches.
 * <p>
 * Supported features:
 * <ul>
 *     <li>Basic placeholder replacement using a Map</li>
 *     <li>PlaceholderAPI support (if installed)</li>
 *     <li>Inline switch syntax: {@code {enabled|disabled}}</li>
 *     <li>Keyed inline switch syntax: {@code {autosell:enabled|disabled}}</li>
 * </ul>
 */
public final class PlaceholderUtils {

    private PlaceholderUtils() {
        // Utility class, prevent instantiation
    }

    /**
     * Replaces placeholders in a single string.
     * <p>
     * Supports:
     * <ul>
     *     <li>Simple placeholder replacement from the provided map</li>
     *     <li>Integration with PlaceholderAPI if target is a player</li>
     * </ul>
     *
     * @param target       Optional target player/operator for PlaceholderAPI integration
     * @param input        The input string to replace placeholders in
     * @param placeholders Map of key-value placeholders to replace, can be null
     * @param ignoreCase   Whether to ignore case when replacing placeholders
     * @return The string with all placeholders replaced
     */
    public static @NotNull String replace(
            @Nullable Audience target,
            @NotNull String input,
            @Nullable Map<String, String> placeholders,
            boolean ignoreCase
    ) {
        String result = input;

        if (placeholders != null && !placeholders.isEmpty()) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                result = ignoreCase
                        ? Utils.replaceIgnoreCase(result, entry.getKey(), entry.getValue())
                        : result.replace(entry.getKey(), entry.getValue());
            }
        }

        result = resolve(target, result);

        return result;
    }

    /**
     * Replaces placeholders in a list of strings.
     *
     * @param target       Optional target player/operator for PlaceholderAPI integration
     * @param input        The input list of strings
     * @param placeholders Map of key-value placeholders to replace, can be null
     * @param ignoreCase   Whether to ignore case when replacing placeholders
     * @return A new list of strings with placeholders replaced
     */
    public static @NotNull List<String> replace(
            @Nullable Audience target,
            @NotNull List<String> input,
            @Nullable Map<String, String> placeholders,
            boolean ignoreCase
    ) {
        return input.stream()
                .map(str -> replace(target, str, placeholders, ignoreCase))
                .collect(Collectors.toList());
    }

    /**
     * Replaces placeholders in an array of strings.
     *
     * @param target       Optional target player/operator for PlaceholderAPI integration
     * @param input        The input array of strings
     * @param placeholders Map of key-value placeholders to replace, can be null
     * @param ignoreCase   Whether to ignore case when replacing placeholders
     * @return A new array of strings with placeholders replaced
     */
    public static @NotNull String @NonNull [] replace(
            @Nullable Audience target,
            @NotNull String[] input,
            @Nullable Map<String, String> placeholders,
            boolean ignoreCase
    ) {
        String[] copy = Arrays.copyOf(input, input.length);

        for (int i = 0; i < copy.length; i++) {
            copy[i] = replace(target, copy[i], placeholders, ignoreCase);
        }

        return copy;
    }

    private static final Pattern SIMPLE_SWITCH_PATTERN =
            Pattern.compile("\\{([^:{}|]+)\\|([^{}|]+)}");

    private static final Pattern KEYED_SWITCH_PATTERN =
            Pattern.compile("\\{(\\w+):([^{}|]+)\\|([^{}|]+)}");

    /**
     * Resolves simple inline switches in a string.
     * <p>
     * Example:
     * <pre>{@code
     * resolveInlineSwitches("Feature is {enabled|disabled}", true);
     * // Returns "Feature is enabled"
     * }</pre>
     *
     * @param input Input string containing simple inline switches
     * @param state Boolean state to determine which option to select (true -> first, false -> second)
     * @return String with inline switches resolved
     */
    public static @NotNull String resolveInlineSwitches(
            @NotNull String input,
            boolean state
    ) {
        Matcher matcher = SIMPLE_SWITCH_PATTERN.matcher(input);
        StringBuilder builder = new StringBuilder();

        while (matcher.find()) {
            String first = matcher.group(1);
            String second = matcher.group(2);

            String replacement = state ? first : second;
            matcher.appendReplacement(builder, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(builder);
        return builder.toString();
    }

    /**
     * Resolves keyed inline switches in a string.
     * <p>
     * Example:
     * <pre>{@code
     * Map<String, Boolean> states = Map.of("autosell", true);
     * resolveInlineSwitches("AutoSell is {autosell:enabled|disabled}", states);
     * // Returns "AutoSell is enabled"
     * }</pre>
     *
     * @param input  Input string containing keyed inline switches
     * @param states Map of key -> boolean state for each switch
     * @return String with keyed inline switches resolved
     */
    public static @NotNull String resolveInlineSwitches(
            @NotNull String input,
            @Nullable Map<String, Boolean> states
    ) {
        if (states == null || states.isEmpty()) {
            return input;
        }

        Matcher matcher = KEYED_SWITCH_PATTERN.matcher(input);
        StringBuilder builder = new StringBuilder();

        while (matcher.find()) {
            String key = matcher.group(1);
            String first = matcher.group(2);
            String second = matcher.group(3);

            boolean state = states.getOrDefault(key, false);
            String replacement = state ? first : second;

            matcher.appendReplacement(builder, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(builder);
        return builder.toString();
    }

    /**
     * Applies {@link PlaceholderHook} replacements if the target is an {@link Audience}.
     *
     * @param target Target player/operator for PlaceholderAPI, can be null
     * @param input  Input string to apply PlaceholderAPI placeholders to
     * @return String with {@link PlaceholderHook} placeholders applied, or original string if unavailable
     */
    private static String resolve(
            @Nullable Audience target,
            @NotNull String input
    ) {
        Optional<PlaceholderHook> optionalPlaceholderHook = SkinOverlay.getInstance().getPlaceholderHook();
        if (optionalPlaceholderHook.isEmpty())
            return input;

        PlaceholderHook placeholderHook = optionalPlaceholderHook.get();
        if (!placeholderHook.isRegistered())
            return input;

        return placeholderHook.resolve(target, input);
    }
}