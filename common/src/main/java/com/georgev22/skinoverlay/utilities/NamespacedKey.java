package com.georgev22.skinoverlay.utilities;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class NamespacedKey implements Key {
    private final String namespace;
    private final String key;

    /**
     * Constructs a new {@code NamespacedKey} using the given namespace and key.
     *
     * @param namespace the namespace
     * @param key       the key name (identifier)
     */
    public NamespacedKey(String namespace, String key) {
        this.namespace = namespace;
        this.key = key;
    }

    /**
     * Returns a string representation in the format {@code namespace:key}.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return namespace + ":" + key;
    }

    @Contract("_ -> new")
    public static @NotNull NamespacedKey fromString(@NotNull String string) {
        String[] parts = string.split(":");
        return new NamespacedKey(parts[0], parts[1]);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NamespacedKey that = (NamespacedKey) obj;
        return Objects.equals(namespace, that.namespace) && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, key);
    }

    @KeyPattern.Namespace
    @Override
    public @NotNull String namespace() {
        return this.namespace;
    }

    @KeyPattern.Value
    @Override
    public @NotNull String value() {
        return this.key;
    }

    @Override
    public @NotNull String asString() {
        return this.toString();
    }
}
