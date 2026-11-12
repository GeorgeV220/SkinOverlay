package com.georgev22.skinoverlay.storage.data;

import com.georgev22.skinoverlay.storage.EntityManager;
import com.georgev22.skinoverlay.utilities.CustomData;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Represents a generic entity with a unique identifier.
 * <p>
 * This interface provides a base for all identifiable entities and includes
 * methods for comparison and equality checks.
 */
public abstract class Entity implements Comparable<Entity> {

    protected final UUID uniqueId;

    protected final CustomData customData = new CustomData();

    public Entity(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * Returns the universally unique identifier (UUID) of this entity.
     *
     * @return the UUID representing the identity of this entity
     */
    public UUID getUniqueId() {
        return uniqueId;
    }

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
    public int compareTo(@NonNull Entity o) {
        return this.uniqueId.compareTo(o.uniqueId);
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
    public boolean equals(Object o) {
        if (!(o instanceof Entity other)) {
            return false;
        }
        return this.uniqueId.equals(other.uniqueId);
    }

    /**
     * Converts this {@link Entity} into its JSON string representation.
     * The formatting of the resulting JSON string can be controlled.
     *
     * @param pretty if true, the JSON output will be formatted with indentation and line breaks for readability.
     *               if false, the JSON output will be compact, without unnecessary whitespace.
     * @return a JSON string representing this {@link Entity}.
     */
    public abstract String toJsonString(boolean pretty);

    /**
     * Converts this {@link Entity} into its JSON representation.
     *
     * @return a JSON representing this {@link Entity}.
     */
    public abstract JsonObject toJson();

    /**
     * Called immediately after this entity has been loaded from storage.
     * <p>
     * This method is invoked exclusively by
     * {@link EntityManager#load(String)} or
     * {@link EntityManager#loadAll()}.
     * It is intended to perform any internal post-loading initialization or processing
     * that the system requires.
     * <p>
     * <b>Note:</b> This method is for internal use only and should not be called
     * directly by plugins or external code.
     */
    @ApiStatus.Internal
    public void postLoad() {
    }

    /**
     * Called immediately after this entity has been saved to storage.
     * <p>
     * This method is invoked exclusively by
     * {@link EntityManager#save(Entity)} or
     * {@link EntityManager#saveAll()}.
     * It is intended to perform any internal post-saving initialization or processing
     * that the system requires.
     * <p>
     * <b>Note:</b> This method is for internal use only and should not be called
     * directly by plugins or external code.
     */
    @ApiStatus.Internal
    public void postSave() {
    }

    /**
     * Removes all storage data associated with this entity.
     * <p>
     * This method is invoked exclusively by
     * {@link EntityManager#delete(Entity)}.
     * It ensures that any residual data left in storage is properly cleaned up.
     * <p>
     * <b>Note:</b> This method is for internal use only and should not be called
     * directly by plugins or external code.
     */
    @ApiStatus.Internal
    public void postDelete() {
    }

    /**
     * Called  immediately after this entity has been created by the {@link EntityManager}
     * <p>
     * This method is invoked exclusively by
     * {@link EntityManager#create(String, Consumer)} or
     * {@link EntityManager#create(UUID, Consumer)}.
     * <p>
     * <b>Note:</b> This method is for internal use only and should not be called
     * directly by plugins or external code.
     */
    @ApiStatus.Internal
    public void postCreate() {
    }

    /**
     * Retrieves the custom data associated with this entity.
     * <p>
     * <strong>Note:</strong> Custom data are not saved, persisted or loaded from storage.
     *
     * @return The custom data associated with this entity.
     */
    public CustomData customData() {
        return customData;
    }
}
