package com.georgev22.skinoverlay.message;

import com.georgev22.skinoverlay.datastructures.maps.ConcurrentHashObjectMap;
import com.georgev22.skinoverlay.datastructures.maps.ObjectMap;
import com.georgev22.skinoverlay.utilities.Copyable;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Map;

/**
 * A utility class that manages placeholders and dynamic states for message formatting.
 * <p>
 * Supports:
 * <ul>
 *     <li>Simple string placeholders (e.g., %player%)</li>
 *     <li>Boolean states for inline switches (e.g., {autosell:enabled|disabled})</li>
 *     <li>Integration with {@link Audience} for PlaceholderAPI</li>
 * </ul>
 */
@SuppressWarnings("UnusedReturnValue")
public class Placeholder implements Copyable<Placeholder> {

    private final ObjectMap<String, String> placeholders = new ConcurrentHashObjectMap<>();
    private final ObjectMap<String, Boolean> states = new ConcurrentHashObjectMap<>();
    private @Nullable Audience target;

    /**
     * Creates an empty Placeholder instance.
     */
    public Placeholder() {
    }

    /**
     * Creates a Placeholder instance with a target {@link Audience}.
     *
     * @param target The target player or server operator for PlaceholderAPI integration.
     */
    public Placeholder(@Nullable Audience target) {
        this.target = target;
    }

    /**
     * Sets the target {@link Audience} for PlaceholderAPI integration.
     *
     * @param target The target player or server operator.
     * @return The current Placeholder instance for chaining.
     */
    public Placeholder setTarget(@Nullable Audience target) {
        this.target = target;
        return this;
    }

    /**
     * Adds a simple placeholder key-value pair.
     * <p>Example: addPlaceholder("%player%", "George")</p>
     *
     * @param key   The placeholder key.
     * @param value The replacement value.
     * @return The current Placeholder instance for chaining.
     */
    public Placeholder addPlaceholder(@NonNull String key, @NonNull String value) {
        placeholders.put(key, value);
        return this;
    }

    /**
     * Adds multiple placeholders from a map.
     *
     * @param map A map containing key-value pairs for placeholders.
     * @return The current Placeholder instance for chaining.
     */
    public Placeholder addPlaceholders(@NonNull Map<String, String> map) {
        placeholders.putAll(map);
        return this;
    }

    /**
     * Adds a boolean state for inline dynamic switches.
     * <p>Example: addState("autosell", true) will allow "{autosell:enabled|disabled}" to resolve to "enabled".</p>
     *
     * @param key   The state key.
     * @param state The boolean state.
     * @return The current Placeholder instance for chaining.
     */
    public Placeholder addState(@NonNull String key, boolean state) {
        states.put(key, state);
        return this;
    }

    /**
     * Merges placeholders and states from another {@link Placeholder} instance
     * into this one.
     * <p>
     * Any placeholders or states present in the provided instance will be added
     * to this instance. If a key already exists, the value from the provided
     * placeholder will overwrite the existing value.
     *
     * @param placeholder The {@link Placeholder} instance whose placeholders
     *                    and states should be merged into this instance.
     * @return The current {@link Placeholder} instance for chaining.
     */
    public Placeholder merge(@NonNull Placeholder placeholder) {
        placeholders.putAll(placeholder.placeholders);
        states.putAll(placeholder.states);
        return this;
    }

    /**
     * Adds multiple boolean states from a map.
     *
     * @param map A map containing key-boolean pairs for states.
     * @return The current Placeholder instance for chaining.
     */
    public Placeholder addStates(@NonNull Map<String, Boolean> map) {
        states.putAll(map);
        return this;
    }

    /**
     * Resolves all placeholders and states in the given input string.
     * <p>This method will replace:</p>
     * <ul>
     *     <li>Simple placeholders (e.g., %player%)</li>
     *     <li>Inline switches using boolean states (e.g., {autosell:enabled|disabled})</li>
     *     <li>Integrates with PlaceholderAPI if target is set</li>
     * </ul>
     *
     * @param input The input string containing placeholders and inline switches.
     * @return The resolved string with placeholders replaced.
     */
    public @NotNull String resolve(@NonNull String input) {
        //noinspection ConstantValue
        if (input == null) return "";

        input = PlaceholderUtils.resolveInlineSwitches(input, states);

        return PlaceholderUtils.replace(target, input, placeholders, true);
    }

    /**
     * Returns a copy of this {@link Placeholder}.
     * <p>
     * Although this method is named "shallowCopy", the concept of a shallow copy
     * does not really apply here because a new {@link Placeholder} instance is
     * created in the constructor with its own independent maps. All placeholders
     * and states from this instance are copied into these new maps. Therefore,
     * modifications to the returned instance do not affect the original.
     *
     * @return A new {@link Placeholder} instance containing the same placeholders,
     * states, and target.
     */
    @Override
    public @NonNull Placeholder shallowCopy() {
        return new Placeholder(target).merge(this);
    }

    /**
     * Returns a deep copy of this {@link Placeholder}.
     * <p>
     * For this class, deepCopy is identical to shallowCopy because:
     * <ul>
     *     <li>The keys and values (Strings and Booleans) are immutable.</li>
     *     <li>All internal maps are newly created in the constructor, ensuring
     *         independence from the original instance.</li>
     * </ul>
     *
     * @return A new {@link Placeholder} instance containing the same data.
     */
    @Override
    public @NonNull Placeholder deepCopy() {
        return this.shallowCopy();
    }

    /**
     * Creates a new {@link Builder} for constructing a {@link Placeholder}.
     *
     * @return A new builder instance.
     */
    @Contract(" -> new")
    public static @NonNull Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new {@link Builder} with a predefined {@link Audience}.
     *
     * @param target The target server operator.
     * @return A new builder instance.
     */
    @Contract("_ -> new")
    public static @NonNull Builder builder(@NotNull Audience target) {
        return new Builder(target);
    }

    /**
     * A fluent builder for creating {@link Placeholder} instances.
     * <p>
     * This builder simplifies the construction of {@link Placeholder} objects
     * by allowing chained configuration of:
     * <ul>
     *     <li>Simple placeholders (e.g., %player%)</li>
     *     <li>Boolean states for inline switches (e.g., {autosell:enabled|disabled})</li>
     *     <li>Target {@link Audience} for PlaceholderAPI integration</li>
     * </ul>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * Placeholder placeholder = new Placeholder.Builder(player)
     *         .placeholder("%player%", player.getName())
     *         .placeholder("%amount%", "100")
     *         .state("autosell", true)
     *         .build();
     *
     * String result = placeholder.resolve("Hello %player%! {autosell:Enabled|Disabled}");
     * }</pre>
     */
    public static class Builder {

        private final Placeholder placeholder;

        /**
         * Creates a new builder with an empty {@link Placeholder} instance.
         */
        public Builder() {
            this.placeholder = new Placeholder();
        }

        /**
         * Creates a new builder with a predefined {@link Audience} target.
         * <p>
         * This target will be used when resolving placeholders via PlaceholderAPI.
         *
         * @param target The target {@link Audience}.
         */
        public Builder(@NotNull Audience target) {
            this.placeholder = new Placeholder(target);
        }

        /**
         * Sets or replaces the {@link Audience} target for PlaceholderAPI integration.
         *
         * @param target The target player or server operator.
         * @return The current builder instance for chaining.
         */
        public Builder target(@Nullable Audience target) {
            placeholder.setTarget(target);
            return this;
        }

        /**
         * Adds a single placeholder key-value pair.
         *
         * <p>Example:</p>
         * <pre>{@code
         * builder.placeholder("%player%", "George");
         * }</pre>
         *
         * @param key   The placeholder key (e.g., %player%).
         * @param value The replacement value.
         * @return The current builder instance for chaining.
         */
        public Builder placeholder(@NonNull String key, @NonNull String value) {
            placeholder.addPlaceholder(key, value);
            return this;
        }

        /**
         * Adds multiple placeholders from a map.
         *
         * @param map A map containing placeholder key-value pairs.
         * @return The current builder instance for chaining.
         */
        public Builder placeholders(@NonNull Map<String, String> map) {
            placeholder.addPlaceholders(map);
            return this;
        }

        /**
         * Adds a boolean state used for inline conditional switches.
         *
         * <p>Example:</p>
         * <pre>{@code
         * builder.state("autosell", true);
         * }</pre>
         * <p>
         * This allows constructs like:
         * <pre>{@code
         * {autosell:enabled|disabled}
         * }</pre>
         *
         * @param key   The state key.
         * @param value The boolean state value.
         * @return The current builder instance for chaining.
         */
        public Builder state(@NonNull String key, boolean value) {
            placeholder.addState(key, value);
            return this;
        }

        /**
         * Adds multiple boolean states from a map.
         *
         * @param map A map containing state key-boolean pairs.
         * @return The current builder instance for chaining.
         */
        public Builder states(@NonNull Map<String, Boolean> map) {
            placeholder.addStates(map);
            return this;
        }

        /**
         * Merges placeholders and states from another {@link Placeholder} instance.
         *
         * @param other The placeholder instance to merge from.
         * @return The current builder instance for chaining.
         */
        public Builder merge(@NonNull Placeholder other) {
            placeholder.merge(other);
            return this;
        }

        /**
         * Builds and returns the configured {@link Placeholder} instance.
         *
         * @return The constructed {@link Placeholder}.
         */
        public @NotNull Placeholder build() {
            return placeholder;
        }
    }
}