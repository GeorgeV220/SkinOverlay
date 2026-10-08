package com.georgev22.skinoverlay.registry;

import com.georgev22.skinoverlay.utilities.Keyed;
import com.georgev22.skinoverlay.utilities.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * A generic registry for {@link Keyed} objects, using their {@link NamespacedKey}
 * as the unique registration key.
 * <p>
 * This registry allows plugin-defined objects (such as custom economies, upgrades,
 * or special items) to be easily registered and retrieved based on their key.
 * By extending this class, API modules can add convenient accessors and domain-specific
 * queries while retaining consistent registry behavior.
 * </p>
 *
 * <h3>Basic Usage:</h3>
 * <pre>{@code
 * KeyedRegistry<MyElement> registry = new KeyedRegistry<>();
 *
 * MyElement element = new MyElement(plugin, "example");
 * registry.register(element);
 *
 * Optional<MyElement> result = registry.get(element.getKey());
 * }</pre>
 *
 * @param <T> the type of value being registered, which must implement {@link Keyed}
 * @see Keyed
 * @see NamespacedKey
 * @see AbstractRegistry
 * @see SelectableKeyedRegistry
 */
public class KeyedRegistry<T extends Keyed> extends AbstractRegistry<NamespacedKey, T> {

    /**
     * Registers the given keyed value using its {@link NamespacedKey}.
     *
     * @param value the value to register
     * @throws IllegalArgumentException if a value is already registered under the same key
     */
    @Override
    public void register(@NotNull T value) throws IllegalArgumentException {
        super.register(value.getKey(), value);
    }

    /**
     * Registers the given keyed value using its {@link NamespacedKey},
     * replacing any existing value under the same key.
     *
     * @param value the value to register or replace
     * @return {@code true} if an existing value was replaced,
     * {@code false} if this is a new registration
     */
    @Override
    public boolean replaceOrRegister(@NotNull T value) {
        return super.replaceOrRegister(value.getKey(), value);
    }
}
