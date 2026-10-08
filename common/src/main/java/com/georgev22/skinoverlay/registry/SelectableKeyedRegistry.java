package com.georgev22.skinoverlay.registry;

import com.georgev22.skinoverlay.utilities.Keyed;
import com.georgev22.skinoverlay.utilities.NamespacedKey;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * A {@link KeyedRegistry} that supports resolving a selected entry.
 * <p>
 * Implementations of this registry can define their own selection strategy,
 * allowing a single registered value to be resolved based on configuration,
 * runtime state, or other external conditions.
 *
 * @param <T> the type of keyed values stored in this registry
 * @see KeyedRegistry
 */
public class SelectableKeyedRegistry<T extends Keyed>
        extends KeyedRegistry<T>
        implements SelectableRegistry<NamespacedKey, T> {

    private T selected;

    /**
     * Returns the currently selected value in this registry.
     *
     * @return an {@link Optional} containing the selected value, or empty if none is selected
     */
    @Override
    public @NonNull Optional<T> getSelected() {
        return Optional.ofNullable(selected);
    }

    /**
     * Sets the selected value in this registry.
     *
     * @param selected the value to select, or {@code null} to clear the selection
     */
    public void setSelected(@Nullable T selected) {
        this.selected = selected;
    }

    /**
     * Sets the selected value in this registry by its key.
     *
     * @param key the key of the value to select, or {@code null} to clear the selection
     */
    public void setSelected(@Nullable NamespacedKey key) {
        this.selected = get(key).orElse(null);
    }
}