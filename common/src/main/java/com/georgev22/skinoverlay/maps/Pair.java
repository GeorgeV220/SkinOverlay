package com.georgev22.skinoverlay.maps;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * A simple generic class representing a pair of elements.
 *
 * @param <K> the type of the first element (key)
 * @param <V> the type of the second element (value)
 */
public final class Pair<K, V> implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    private final K key;
    private V value;

    /**
     * Constructs a new pair with the specified key and value.
     *
     * @param key   the first element (key) of the pair
     * @param value the second element (value) of the pair
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Indicates whether some other object is "equal to" this pair.
     *
     * @param o the reference object with which to compare
     * @return {@code true} if this pair is the same as the o argument; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair<?, ?>)) {
            return false;
        }

        Pair<K, V> p = (Pair<K, V>) o;

        return Objects.equals(p.key, key) && Objects.equals(p.value, value);
    }

    /**
     * Returns the hash code value for this pair.
     *
     * @return the hash code value for this pair
     */
    @Override
    public int hashCode() {
        return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }

    /**
     * Returns a string representation of this pair.
     *
     * @return a string representation of this pair
     */
    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "Pair{" +
                "key=" + key + ", " +
                "value=" + value + "}";
    }

    /**
     * Creates a new pair with the specified key and value.
     *
     * @param key   the first element (key) of the pair
     * @param value the second element (value) of the pair
     * @param <K>   the type of the first element (key)
     * @param <V>   the type of the second element (value)
     * @return a new pair with the specified key and value
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static <K, V> @NotNull Pair<K, V> create(K key, V value) {
        return new Pair<>(key, value);
    }

    /**
     * Returns the key of this pair.
     *
     * @return the key of this pair
     */
    public K key() {
        return key;
    }

    /**
     * Returns the value of this pair.
     *
     * @return the value of this pair
     */
    public V value() {
        return value;
    }

    /**
     * Sets the value of this pair.
     *
     * @param value the new value to set
     */
    public void setValue(V value) {
        this.value = value;
    }
}
