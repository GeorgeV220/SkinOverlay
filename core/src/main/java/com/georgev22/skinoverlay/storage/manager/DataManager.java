package com.georgev22.skinoverlay.storage.manager;

import com.georgev22.library.database.DatabaseWrapper;
import com.georgev22.library.database.DatabaseWrapper.DatabaseObject;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.maps.ObjectMap.Pair;
import com.georgev22.library.maps.ObservableObjectMap;
import com.georgev22.library.utilities.EntityManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.storage.data.Data;
import com.mongodb.annotations.Beta;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The {@link DataManager} class is responsible for managing {@link Data} objects in a persistence storage.
 * It supports multiple storage types including MySQL, SQLite, PostgreSQL, MongoDB, and FILE.
 * The class provides methods for checking if a {@link Data} exists,
 * loading a {@link Data}, and creating a {@link Data}.
 *
 * @author <a href="https://github.com/GeorgeV220">GeorgeV220</a>
 */
public class DataManager implements EntityManager<Data> {
    private final File entitiesDirectory;
    private final DatabaseWrapper database;
    private final String collection;
    private final ObservableObjectMap<UUID, Data> loadedEntities = new ObservableObjectMap<>();

    /**
     * Constructor for the EntityManager class
     *
     * @param obj            the object to be used for storage (DatabaseWrapper or File)
     * @param collectionName the name of the collection to be used for MONGODB and SQL, null for other types
     */
    public DataManager(Object obj, @Nullable String collectionName) {
        this.collection = collectionName;
        if (obj instanceof File folder) {
            this.entitiesDirectory = folder;
            this.database = null;
            if (!this.entitiesDirectory.exists()) {
                this.entitiesDirectory.mkdirs();
            }
        } else if (obj instanceof DatabaseWrapper databaseWrapper) {
            this.entitiesDirectory = null;
            this.database = databaseWrapper;
        } else {
            this.entitiesDirectory = null;
            this.database = null;
        }
    }

    /**
     * Loads the {@link Data} with the specified ID
     *
     * @param entityId the {@link UUID} of the entity to be loaded
     * @return a {@link CompletableFuture} containing the loaded {@link Data} object
     */
    @Override
    public CompletableFuture<Data> load(UUID entityId) {
        return exists(entityId)
                .thenCompose(exists -> {
                    if (exists) {
                        return CompletableFuture.supplyAsync(() -> {
                            if (entitiesDirectory != null) {
                                File file = new File(entitiesDirectory, entityId + ".entity");
                                try {
                                    Data entity = (Data) Utils.deserializeObject(file.getAbsolutePath());
                                    loadedEntities.append(entityId, entity);
                                    return entity;
                                } catch (IOException | ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            } else if (database != null) {
                                Pair<String, List<DatabaseObject>> retrievedData = database.retrieveData(collection, Pair.create("entity_id", entityId.toString()));
                                Data entity = new Data(entityId);
                                retrievedData.value().forEach(databaseObject -> {
                                    ObjectMap<String, Object> databaseObjectData = databaseObject.data();
                                    databaseObjectData.forEach(entity::addCustomData);
                                });
                                return entity;
                            } else {
                                return new Data(entityId);
                            }
                        });
                    } else {
                        return createEntity(entityId);
                    }
                });
    }

    /**
     * Saves the specified {@link Data}.
     *
     * @param entity the {@link Data} to save
     * @return a {@link CompletableFuture} that completes when the {@link Data} is saved
     */
    @Override
    public CompletableFuture<Void> save(Data entity) {
        return CompletableFuture.runAsync(() -> {
            if (entitiesDirectory != null) {
                File file = new File(entitiesDirectory, entity.getId() + ".entity");
                try {
                    Utils.serializeObject(entity, file.getAbsolutePath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (database != null) {
                exists(entity.getId()).thenAccept(result -> {
                    ObjectMap<String, Object> entityData = new HashObjectMap<>(entity.getCustomData());
                    if (result) {
                        database.updateData(collection, Pair.create("entity_id", entity.getId().toString()), Pair.create("$set", entityData.removeEntry("entity_id")), null);
                    } else {
                        database.addData(collection, Pair.create(entity.getId().toString(), entityData));
                    }
                });
            }
            this.loadedEntities.append(entity.getId(), entity);
        });
    }

    /**
     * Deletes the specified entity.
     *
     * @param entity the {@link Data} to delete
     * @return a {@link CompletableFuture} that completes when the {@link Data} is deleted
     */
    @Override
    public CompletableFuture<Void> delete(Data entity) {
        return CompletableFuture.runAsync(() -> {
            if (entitiesDirectory != null) {
                File file = new File(entitiesDirectory, entity.getId() + ".entity");
                if (file.exists()) {
                    file.delete();
                }
            } else if (database != null) {
                exists(entity.getId()).thenAccept(result -> {
                    ObjectMap<String, Object> entityData = new HashObjectMap<>(entity.getCustomData());
                    if (result) {
                        database.removeData(collection, Pair.create("entity_id", entity.getId()), null);
                    }
                });
            }
            this.loadedEntities.remove(entity.getId());
        });
    }

    /**
     * Creates a new {@link Data} with the specified entity ID.
     *
     * @param entityId the {@link UUID} of the entity to create
     * @return a {@link CompletableFuture} that returns the newly created {@link Data}
     */
    @Override
    public CompletableFuture<Data> createEntity(UUID entityId) {
        return CompletableFuture.completedFuture(loadedEntities.append(entityId, new Data(entityId)).get(entityId));
    }

    /**
     * Determines if a {@link Data} with the specified entity ID exists.
     *
     * @param entityId the {@link UUID} of the entity to check
     * @return a {@link CompletableFuture} that returns true if a {@link Data} with the specified ID exists, false otherwise
     */
    @Override
    public CompletableFuture<Boolean> exists(UUID entityId) {
        return CompletableFuture.supplyAsync(() -> {
            if (entitiesDirectory != null) {
                return new File(entitiesDirectory, entityId + ".entity").exists();
            } else if (database != null) {
                return database.exists(collection, Pair.create("entity_id", entityId), null);
            } else {
                return false;
            }
        });
    }

    /**
     * Retrieves the {@link Data} with the given {@link UUID}.
     * <p>
     * If the entity is already loaded, it is returned immediately.
     * If not, it is loaded
     * asynchronously and returned in a {@link CompletableFuture}.
     *
     * @param entityId the {@link UUID} of the entity to retrieve
     * @return a {@link CompletableFuture} that will contain the {@link Data} with the given id
     */
    @Override
    public CompletableFuture<Data> getEntity(UUID entityId) {
        if (loadedEntities.containsKey(entityId)) {
            return CompletableFuture.completedFuture(loadedEntities.get(entityId));
        }

        return load(entityId);
    }

    /**
     * Saves all the loaded {@link Data}s in the {@link #loadedEntities} map.
     * For each {@link Data} in the map,
     * this method calls the {@link #save(Data)} method to persist the {@link Data}.
     */
    @Override
    public void saveAll() {
        ObjectMap<UUID, Data> entities = new ObservableObjectMap<UUID, Data>().append(loadedEntities);
        entities.forEach((uuid, entity) -> save(entity));
    }

    /**
     * Loads all the entities by retrieving their IDs and invoking the {@link #load(UUID)} method.
     * If the entities directory is specified, it scans the directory for entity files and extracts their IDs.
     * If the database is specified, it retrieves entity IDs from the database and loads them.
     */
    @Beta
    @Override
    public void loadAll() {
        List<UUID> entityIDs = new ArrayList<>();
        if (entitiesDirectory != null) {
            File[] files = this.entitiesDirectory.listFiles((dir, name) -> name.endsWith(".entity"));
            if (files != null) {
                Arrays.stream(files).forEach(file -> entityIDs.add(UUID.fromString(file.getName().replace(".entity", ""))));
            }
        } else if (database != null) {
            Pair<String, List<DatabaseObject>> data = database.retrieveData(collection, Pair.create("entity_id", null));
            data.value().forEach(databaseObject -> entityIDs.add(UUID.fromString(String.valueOf(databaseObject.data().get("entity_id")))));
        }
        entityIDs.forEach(this::load);
    }

    /**
     * Retrieves the current map of loaded entities.
     *
     * @return the map of loaded entities with UUID as the key and Data object as the value
     */
    @Override
    public ObservableObjectMap<UUID, Data> getLoadedEntities() {
        return loadedEntities;
    }
}
