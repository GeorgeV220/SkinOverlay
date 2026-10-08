package com.georgev22.skinoverlay.registry;

import org.jspecify.annotations.NonNull;

import java.util.Optional;

/**
 * A registry that supports selecting a single value from its registered entries.
 * <p>
 * Unlike a regular {@link Registry}, a selectable registry can resolve one
 * entry as the current selection. The selection logic is implementation-specific
 * and may depend on configuration, defaults, runtime state, or other factors.
 *
 * @param <K> the type of keys used to identify registered values
 * @param <V> the type of values stored in this registry
 */
public interface SelectableRegistry<K, V> extends Registry<K, V> {

    /**
     * Retrieves the currently selected value.
     * <p>
     * The selection is resolved by the implementation and may change depending
     * on external conditions or internal state.
     *
     * @return an {@link Optional} containing the selected value if one exists,
     *         or {@link Optional#empty()} if no value can be selected
     */
    @NonNull
    Optional<V> getSelected();
}