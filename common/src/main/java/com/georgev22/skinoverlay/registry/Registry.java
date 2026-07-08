package com.georgev22.skinoverlay.registry;

import com.georgev22.skinoverlay.datastructures.maps.ObjectMap;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface Registry<K, V> {

    /**
     * Registers a value with the given key.
     *
     * @param key   the key used to identify the value
     * @param value the value to register
     * @throws IllegalArgumentException if a value is already registered with the given key
     */
    void register(@NotNull K key, @NotNull V value) throws IllegalArgumentException;

    /**
     * Registers a value that can provide its own key.
     * <p>
     * The value type {@code V} must have a way of exposing its key (for example,
     * via a {@code getKey()} method). The registry will extract the key from
     * the value and use it for registration.
     * </p>
     *
     * @param value the value to register, which must provide its own key
     * @throws IllegalArgumentException if a value is already registered with the same key
     */
    void register(@NotNull V value) throws IllegalArgumentException;

    /**
     * Registers or replaces an existing value with the given key.
     *
     * @param key   the key used to identify the value
     * @param value the value to register or replace
     * @return {@code true} if an existing value was replaced, {@code false} if this is a new registration
     */
    boolean replaceOrRegister(@NotNull K key, @NotNull V value);

    /**
     * Registers or replaces a value that can provide its own key.
     * <p>
     * The value type {@code V} must have a way of exposing its key (for example,
     * via a {@code getKey()} method). The registry will extract the key from
     * the value and use it for registration.
     * </p>
     *
     * @param value the value to register or replace, which must provide its own key
     * @return {@code true} if an existing value was replaced, {@code false} if this is a new registration
     */
    boolean replaceOrRegister(@NotNull V value);

    /**
     * Unregisters a value by its key.
     *
     * @param key the key used to identify the value
     * @return {@code true} if the value was unregistered, {@code false} if not registered
     */
    boolean unregister(@NotNull K key);

    /**
     * Retrieves a registered value by its key.
     *
     * @param key the key used to look up the value
     * @return an {@link Optional} containing the value if found, or {@link Optional#empty()} if not registered
     */
    @NotNull Optional<V> get(K key);

    /**
     * Checks whether a value is registered under the given key.
     *
     * @param key the key to check
     * @return {@code true} if a value is registered under the key, {@code false} otherwise
     */
    boolean contains(@NotNull K key);

    /**
     * Returns an unmodifiable view of the registry entries.
     *
     * @return an {@link ObjectMap} containing all key–value pairs currently registered
     */
    @NotNull ObjectMap<K, V> entries();
}
