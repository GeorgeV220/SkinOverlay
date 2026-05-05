package com.georgev22.skinoverlay.datastructures;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * A lightweight immutable pair of two related values.
 *
 * <p>This class represents an ordered container holding two elements:
 * a mandatory {@code first} value and an optional {@code second} value.
 * It is designed for simple structured return values and small data groupings.</p>
 *
 * <p>Equality, hashing and string representation are defined based on both contained values.</p>
 *
 * @param <F> the type of the first element (non-null)
 * @param <S> the type of the second element (nullable)
 */
public final class Pair<F, S> implements Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    private final F first;
    private S second;

    /**
     * Constructs a new {@code Pair} with the specified values.
     *
     * @param first  the first (mandatory) element
     * @param second the second (optional) element, may be {@code null}
     */
    public Pair(@NonNull F first, @Nullable S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Indicates whether some other object is equal to this pair.
     *
     * <p>Two pairs are considered equal if both their {@code first} and {@code second}
     * elements are equal.</p>
     *
     * @param o the object to compare with
     * @return {@code true} if both pairs contain equal values, otherwise {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair<?, ?> p)) return false;
        return Objects.equals(first, p.first) && Objects.equals(second, p.second);
    }

    /**
     * Returns the hash code value for this pair.
     *
     * <p>The hash is derived from both elements.</p>
     *
     * @return the hash code of this pair
     */
    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    /**
     * Returns a human-readable string representation of this pair.
     *
     * @return a string representation of this pair
     */
    @Contract(pure = true)
    @Override
    public @NonNull String toString() {
        return "Pair[first=" + first + ", second=" + second + "]";
    }

    /**
     * Creates a new {@code Pair} instance.
     *
     * @param first  the first (mandatory) value
     * @param second the second (optional) value
     * @param <F>    the first element type
     * @param <S>    the second element type
     * @return a new pair containing the supplied values
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static <F, S> @NonNull Pair<F, S> create(@NonNull F first, @Nullable S second) {
        return new Pair<>(first, second);
    }

    /**
     * Returns the first element of this pair.
     *
     * @return the first element (never {@code null})
     */
    public @NonNull F first() {
        return first;
    }

    /**
     * Returns the second element of this pair.
     *
     * @return the second element, or {@code null} if not present
     */
    public @Nullable S second() {
        return second;
    }

    /**
     * Returns the second element wrapped in an {@link Optional}.
     *
     * @return an {@code Optional} describing the second element, or empty if absent
     */
    @Contract(pure = true)
    public @NonNull Optional<S> secondOptional() {
        return Optional.ofNullable(second);
    }

    /**
     * Replaces the second element of this pair.
     *
     * @param second the new second value (maybe {@code null})
     */
    public void setSecond(@Nullable S second) {
        this.second = second;
    }
}
