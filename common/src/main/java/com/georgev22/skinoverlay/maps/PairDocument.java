package com.georgev22.skinoverlay.maps;

import com.georgev22.skinoverlay.exceptions.PairDocumentException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

import static java.lang.String.format;

/**
 * A record representing a document composed of key-value pairs.
 */
public final class PairDocument<K, V> implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    private final List<Pair<K, V>> objectPairs;

    /**
     * Constructs a PairDocument with the specified key-value pairs.
     *
     * @param objectPairs the key-value pairs
     * @throws PairDocumentException if the given array of pairs is empty
     */
    public PairDocument(Pair<K, V>[] objectPairs) {
        this(objectPairs != null ? Arrays.asList(objectPairs) : Collections.emptyList());
    }

    /**
     * Constructs a PairDocument with the specified key-value pairs.
     *
     * @param objectPairs the key-value pairs
     * @throws PairDocumentException if the given list of pairs is empty
     */
    public PairDocument(List<Pair<K, V>> objectPairs) {
        if (objectPairs.isEmpty()) {
            throw new PairDocumentException("PairDocument is empty");
        }
        this.objectPairs = objectPairs;
    }

    /**
     * Gets the value associated with the specified key as an Integer.
     *
     * @param key the key whose associated value is to be returned
     * @return the value associated with the specified key as an Integer, or null if no mapping exists for the key
     * @throws ClassCastException if the value is not an Integer
     */
    public Integer getInteger(final Object key) {
        return getInteger(key, 0);
    }

    /**
     * Gets the value associated with the specified key as an Integer, or a default value if the key is not present or the value is null.
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue the default value to return if the key is not present or the value is null
     * @return the value associated with the specified key as an Integer, or the specified default value if no mapping exists for the key
     * @throws ClassCastException if the value is not an Integer
     */
    public int getInteger(final Object key, final int defaultValue) {
        return get(key, defaultValue);
    }

    /**
     * Gets the value associated with the specified key as a Long.
     *
     * @param key the key whose associated value is to be returned
     * @return the value associated with the specified key as a Long, or null if no mapping exists for the key
     * @throws ClassCastException if the value is not a Long
     */
    public Long getLong(final Object key) {
        return getLong(key, 0L);
    }

    /**
     * Gets the value associated with the specified key as a Long, or a default value if the key is not present or the value is null.
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue the default value to return if the key is not present or the value is null
     * @return the value associated with the specified key as a Long, or the specified default value if no mapping exists for the key
     * @throws ClassCastException if the value is not a Long
     */
    public Long getLong(final Object key, final long defaultValue) {
        return get(key, defaultValue);
    }

    /**
     * Gets the value associated with the specified key as a Double.
     *
     * @param key the key whose associated value is to be returned
     * @return the value associated with the specified key as a Double, or null if no mapping exists for the key
     * @throws ClassCastException if the value is not a Double
     */
    public Double getDouble(final Object key) {
        return getDouble(key, 0D);
    }

    /**
     * Gets the value associated with the specified key as a Double, or a default value if the key is not present or the value is null.
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue the default value to return if the key is not present or the value is null
     * @return the value associated with the specified key as a Double, or the specified default value if no mapping exists for the key
     * @throws ClassCastException if the value is not a Double
     */
    public Double getDouble(final Object key, final double defaultValue) {
        return get(key, defaultValue);
    }

    /**
     * Gets the value associated with the specified key as a String.
     *
     * @param key the key whose associated value is to be returned
     * @return the value associated with the specified key as a String, or null if no mapping exists for the key
     * @throws ClassCastException if the value is not a String
     */
    public String getString(final Object key) {
        return getString(key, "");
    }

    /**
     * Gets the value associated with the specified key as a String, or a default value if the key is not present or the value is null.
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue the default value to return if the key is not present or the value is null
     * @return the value associated with the specified key as a String, or the specified default value if no mapping exists for the key
     * @throws ClassCastException if the value is not a String
     */
    public String getString(final Object key, final String defaultValue) {
        return get(key, defaultValue);
    }

    /**
     * Gets the value associated with the specified key as a Boolean.
     *
     * @param key the key whose associated value is to be returned
     * @return the value associated with the specified key as a Boolean, or null if no mapping exists for the key
     * @throws ClassCastException if the value is not a Boolean
     */
    public Boolean getBoolean(final Object key) {
        return getBoolean(key, false);
    }

    /**
     * Gets the value associated with the specified key as a Boolean, or a default value if the key is not present or the value is null.
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue the default value to return if the key is not present or the value is null
     * @return the value associated with the specified key as a Boolean, or the specified default value if no mapping exists for the key
     * @throws ClassCastException if the value is not a Boolean
     */
    public boolean getBoolean(final Object key, final boolean defaultValue) {
        return get(key, defaultValue);
    }

    /**
     * Gets the value associated with the specified key as a Date.
     *
     * @param key the key whose associated value is to be returned
     * @return the value associated with the specified key as a Date, or null if no mapping exists for the key
     * @throws ClassCastException if the value is not a Date
     */
    public Date getDate(final Object key) {
        return getDate(key, new Date());
    }

    /**
     * Gets the value associated with the specified key as a Date, or a default value if the key is not present or the value is null.
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue the default value to return if the key is not present or the value is null
     * @return the value associated with the specified key as a Date, or the specified default value if no mapping exists for the key
     * @throws ClassCastException if the value is not a Date
     */
    public Date getDate(final Object key, final Date defaultValue) {
        return get(key, defaultValue);
    }

    /**
     * Gets the list value associated with the specified key, casting the list elements to the given class.
     *
     * @param key   the key whose associated list value is to be returned
     * @param clazz the non-null class to cast the list value to
     * @param <T>   the type of the class
     * @return the list value associated with the specified key, or null if no mapping exists for the key
     * @throws ClassCastException if the elements in the list value associated with the specified key are not of type T, or the value is not a list
     */
    public <T> List<T> getList(Object key, Class<T> clazz) {
        return getList(key, clazz, null);
    }

    /**
     * Gets the list value associated with the specified key, casting the list elements to the given class, or a default list value if null.
     *
     * @param key          the key whose associated list value is to be returned
     * @param clazz        the non-null class to cast the list value to
     * @param defaultValue the default list value to return if the key is not present or the value is null
     * @param <T>          the type of the class
     * @return the list value associated with the specified key, or the default list value if no mapping exists for the key
     * @throws ClassCastException if the elements in the list value associated with the specified key are not of type T, or the value is not a list
     */
    public <T> List<T> getList(final Object key, final Class<T> clazz, final List<T> defaultValue) {
        List<T> value = get(key, List.class);
        if (value == null) {
            return defaultValue;
        }

        for (Object item : value) {
            if (!clazz.isAssignableFrom(item.getClass())) {
                throw new ClassCastException(format("List element cannot be cast to %s", clazz.getName()));
            }
        }
        return value;
    }

    /**
     * Gets the value associated with the specified key, casting it to the given class.
     *
     * @param key   the key whose associated value is to be returned
     * @param clazz the non-null class to cast the value to
     * @param <T>   the type of the class
     * @return the value associated with the specified key, cast to the specified class, or null if no mapping exists for the key
     */
    public <T> T get(final Object key, final Class<T> clazz) {
        return clazz.cast(get(key));
    }

    /**
     * Gets the value associated with the specified key, or a default value if the key is not present or the value is null.
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue the default value to return if the key is not present or the value is null
     * @param <T>          the type of the value
     * @return the value associated with the specified key, or the specified default value if no mapping exists for the key
     */
    public <T> T get(final Object key, final T defaultValue) {
        Object value = get(key);
        return value == null ? defaultValue : (T) value;
    }

    /**
     * Gets the value associated with the specified key.
     *
     * @param key the key whose associated value is to be returned
     * @param <T> the type of the value
     * @return the value associated with the specified key, or null if no mapping exists for the key
     */
    public <T> @Nullable T get(final Object key) {
        for (Pair<K, V> pair : objectPairs) {
            if (pair.key().equals(key)) {
                return (T) pair.value();
            }
        }
        return null;
    }

    /**
     * Returns a string representation of this PairDocument.
     *
     * @return a string representation of this PairDocument
     */
    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "PairDocument{"
                + "pairs=" + objectPairs
                + "}";
    }

    public List<Pair<K, V>> objectPairs() {
        return objectPairs;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PairDocument) obj;
        return Objects.equals(this.objectPairs, that.objectPairs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectPairs);
    }

}
