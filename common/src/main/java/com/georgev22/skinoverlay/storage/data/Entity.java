package com.georgev22.skinoverlay.storage.data;

import com.georgev22.skinoverlay.utilities.CustomData;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a generic entity with a unique identifier.
 * <p>
 * This interface provides a base for all identifiable entities and includes
 * methods for comparison and equality checks.
 */
public interface Entity extends Comparable<Entity> {

    /**
     * Returns the universally unique identifier (UUID) of this entity.
     *
     * @return the UUID representing the identity of this entity
     */
    UUID getId();


    /**
     * Retrieves the custom data associated with this entity.
     * <p>
     * <strong>Note:</strong> Custom data are not saved, persisted or loaded from storage.
     *
     * @return The custom data associated with this entity.
     */
    CustomData customData();

    /**
     * Compares this entity with the specified entity for order.
     * <p>
     * The comparison is based on the natural ordering of their UUIDs.
     *
     * @param o the entity to be compared
     * @return a negative integer, zero, or a positive integer as this entity's UUID
     * is less than, equal to, or greater than the specified entity's UUID
     */
    @Override
    default int compareTo(@NotNull Entity o) {
        return getId().compareTo(o.getId());
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * Two entities are considered equal if their UUIDs are equal.
     * Implementations should override this method to enforce identity-based equality.
     *
     * @param o the reference object with which to compare
     * @return {@code true} if this entity is the same as the {@code o} argument;
     * {@code false} otherwise
     */
    @Override
    boolean equals(Object o);

    /**
     * Performs a deep equality check between this entity and another object.
     * <p>
     * This method compares all relevant fields of the entities, not just the UUID.
     *
     * @param o the object to compare with
     * @return {@code true} if all fields of this entity are equal to those of the given object;
     * {@code false} otherwise
     */
    boolean equalsExact(Object o);
}
