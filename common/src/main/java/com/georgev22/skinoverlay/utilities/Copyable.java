package com.georgev22.skinoverlay.utilities;

import org.jetbrains.annotations.NotNull;

/**
 * A generic interface that defines a contract for objects that can be copied.
 * <p>
 * Implementing classes must provide their own implementations of
 * {@link #shallowCopy()} and {@link #deepCopy()} to create new instances
 * of the object with different copy depths.
 *
 * @param <T> the type of the object that can be copied
 */
public interface Copyable<T> {

    /**
     * Creates a shallow copy of the current object.
     * <p>
     * Only the top-level fields are copied; nested objects are referenced directly.
     *
     * @return a shallow copy of type {@code T}
     */
    @NotNull T shallowCopy();

    /**
     * Creates a deep copy of the current object.
     * <p>
     * Both top-level fields and nested objects are fully copied.
     *
     * @return a deep copy of type {@code T}
     */
    @NotNull T deepCopy();

    /**
     * Creates a copy of the current object according to the given {@link CopyType}.
     *
     * @param type the copy type (shallow or deep)
     * @return a copy of type {@code T}, based on the specified {@link CopyType}
     */
    @NotNull
    default T copy(@NotNull CopyType type) {
        return type.isDeep() ? deepCopy() : shallowCopy();
    }

    /**
     * Represents the strategy for copying an object: shallow or deep.
     */
    enum CopyType {
        SHALLOW,
        DEEP;

        public boolean isDeep() {
            return this == DEEP;
        }

        public boolean isShallow() {
            return this == SHALLOW;
        }

        public static CopyType fromBoolean(boolean deep) {
            return deep ? DEEP : SHALLOW;
        }
    }
}
