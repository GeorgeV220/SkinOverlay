package com.georgev22.skinoverlay.storage;

import com.georgev22.skinoverlay.datastructures.maps.ObjectMap;
import com.georgev22.skinoverlay.datastructures.maps.UnmodifiableObjectMap;
import com.georgev22.skinoverlay.storage.data.Entity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Interface for managing entities with CRUD operations and additional helper methods.
 *
 * @param <E> the type of entity to manage
 */
public interface EntityManager<E extends Entity> {

    /**
     * Returns the class of the entity managed by this manager.
     *
     * @return the entity class
     */
    default Class<E> getEntityClass() {
        return getManagedEntity().type();
    }

    ManagedEntity<E> getManagedEntity();

    /**
     * Saves the specified entity.
     *
     * @param entity the entity to save
     */
    void save(@NonNull E entity);

    /**
     * Finds an entity by its unique identifier.
     *
     * @param id the unique identifier
     * @return an {@link Optional} containing the entity if found, or an empty Optional if not found
     */
    Optional<E> findById(@NonNull String id);

    /**
     * Finds an entity by its unique identifier.
     *
     * @param uuid the unique identifier
     * @return an {@link Optional} containing the entity if found, or an empty Optional if not found
     */
    default Optional<E> findById(@NonNull UUID uuid) {
        return findById(uuid.toString());
    }

    /**
     * Deletes the specified entity.
     *
     * @param entity the entity to delete
     */
    void delete(@NonNull E entity);

    /**
     * Loads an entity by its unique identifier.
     *
     * @param id the unique identifier
     * @return an {@link Optional} containing the entity if found, or an empty Optional if not found
     */
    CompletableFuture<Optional<E>> load(@NonNull String id);

    /**
     * Loads an entity by its unique identifier.
     *
     * @param uuid the unique identifier
     * @return an {@link Optional} containing the entity if found, or an empty Optional if not found
     */
    default CompletableFuture<Optional<E>> load(@NonNull UUID uuid) {
        return load(uuid.toString());
    }

    /**
     * Loads all entities.
     */
    void loadAll();

    /**
     * Saves all entities.
     */
    default void saveAll() {
        saveAll(entity -> {
        });
    }

    /**
     * Saves all entities and executes the provided consumer on each one.
     *
     * @param consumer the consumer to apply to each saved entity
     */
    void saveAll(Consumer<E> consumer);

    /**
     * Returns all entities managed by this manager.
     *
     * @return a list of all entities
     */
    List<E> getAll();

    /**
     * Returns a view of the entities currently loaded in memory.
     *
     * @return an unmodifiable view of the loaded entities
     */
    UnmodifiableObjectMap<String, E> getLoadedEntities();

    /**
     * Checks if an entity with the specified identifier exists.
     *
     * @param id the unique identifier
     * @return {@code true} if the entity exists, {@code false} otherwise
     */
    boolean exists(@NonNull String id);

    /**
     * Checks if an entity with the specified identifier exists.
     *
     * @param uuid the unique identifier
     * @return {@code true} if the entity exists, {@code false} otherwise
     */
    default boolean exists(@NonNull UUID uuid) {
        return exists(uuid.toString());
    }

    /**
     * Creates a new entity with the specified identifier and executes the provided consumer.
     *
     * @param id       the unique identifier
     * @param consumer the consumer to apply to the new entity
     * @return an {@link Optional} containing the new entity if created, or empty if not created
     */
    default Optional<E> create(@NonNull String id, @Nullable Consumer<E> consumer) {
        return create(UUID.fromString(id), consumer);
    }

    /**
     * Creates a new entity with the specified identifier and executes the provided consumer.
     *
     * @param uuid     the unique identifier
     * @param consumer the consumer to apply to the new entity
     * @return an {@link Optional} containing the new entity if created, or empty if not created
     */
    default Optional<E> create(@NonNull UUID uuid, @Nullable Consumer<E> consumer) {
        return create(ObjectMap.ofEntries(ObjectMap.entry("id", uuid)), consumer);
    }

    /**
     * Creates a new entity with the specified identifier and executes the provided consumer.
     *
     * @param data     the data to create the entity with
     * @param consumer the consumer to apply to the new entity
     * @return an {@link Optional} containing the new entity if created, or empty if not created
     */
    Optional<E> create(@NonNull ObjectMap<String, Object> data, @Nullable Consumer<E> consumer);

    /**
     * Gets the name of this entity manager.
     *
     * @return the name
     */
    String getName();

    /**
     * Shuts down this manager.
     */
    default void shutdown() {
        shutdown(entity -> {
        });
    }

    /**
     * Shuts down this manager and applies the given consumer to each entity.
     *
     * @param consumer the consumer to apply
     */
    void shutdown(Consumer<E> consumer);

}
