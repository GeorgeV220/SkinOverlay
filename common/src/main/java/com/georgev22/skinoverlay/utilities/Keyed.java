package com.georgev22.skinoverlay.utilities;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an object which has a {@link NamespacedKey} attached to it.
 */
public interface Keyed extends net.kyori.adventure.key.Keyed {

    /**
     * Return the namespaced identifier for this object.
     *
     * @return this object's key
     */
    @NotNull
    NamespacedKey getKey();

    /**
     * Returns the unique identifier for this object.
     *
     * @return this object's key
     */
    @Override
    default @NotNull Key key() {
        return this.getKey();
    }
}
