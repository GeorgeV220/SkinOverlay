package com.georgev22.skinoverlay.storage.manager.gson;

import com.georgev22.skinoverlay.storage.ManagedEntity;
import com.georgev22.skinoverlay.storage.data.Entity;
import com.georgev22.skinoverlay.storage.manager.AbstractEntityManager;
import com.georgev22.skinoverlay.utilities.GsonUtils;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;

public class FileEntityManager<E extends Entity> extends AbstractEntityManager<E> {

    private final File entitiesDirectory;

    public FileEntityManager(ManagedEntity<E> managedEntity, File folder) {
        super(managedEntity);
        this.entitiesDirectory = folder;
        if (!this.entitiesDirectory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            this.entitiesDirectory.mkdirs();
        }
    }

    /**
     * Deletes the specified entity.
     *
     * @param entity the entity to delete
     */
    protected void delete0(@NonNull E entity) {
        if (entitiesDirectory != null) {
            File file = new File(entitiesDirectory, entity.getUniqueId() + ".json");

            if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
    }

    @Override
    public Optional<E> load0(@NonNull String id) {
        File jsonFile = new File(entitiesDirectory, id + ".json");

        if (jsonFile.exists()) {
            try {
                String jsonData = Files.readString(jsonFile.toPath(), StandardCharsets.UTF_8);
                E entity = GsonUtils.fromJson(jsonData, getEntityClass());

                if (entity == null) {
                    this.main.getLogger().log(Level.SEVERE, "Failed to load entity " + getEntityClass().getSimpleName() + " " + id);
                    return Optional.empty();
                }
                this.loadedEntities.append(entity.getUniqueId().toString(), entity);
                return Optional.of(entity);
            } catch (IOException e) {
                this.main.getLogger().log(Level.SEVERE, "Error while trying to load " + id + " entity " + getEntityClass().getSimpleName(), e);
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    /**
     * Loads all entities from the file system.
     */
    @Override
    public void loadAll() {
        File[] files = this.entitiesDirectory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            main.getLogger().info("No entity " + getEntityClass().getSimpleName() + " data files found to load.");
            return;
        }

        int availableThreads = Math.max(4, Runtime.getRuntime().availableProcessors() / 2);

        List<String> successList = Collections.synchronizedList(new ArrayList<>());
        List<String> failedList = Collections.synchronizedList(new ArrayList<>());

        long startTime = System.nanoTime();
        try (ForkJoinPool customPool = new ForkJoinPool(availableThreads)) {
            try {
                customPool.submit(() -> Arrays.stream(files).parallel().forEach(file -> {
                    String id = file.getName().replace(".json", "");

                    try {
                        long fileStart = System.nanoTime();
                        Optional<E> entity = this.load(id).get();
                        if (entity.isPresent()) {
                            successList.add(id);
                            if (OptionsUtil.DEBUG.getBooleanValue()) {
                                this.main.getLogger().info("Loaded entity " + getEntityClass().getSimpleName() + " " + entity.get().getUniqueId());
                            }
                            if (OptionsUtil.DEBUG.getBooleanValue()) {
                                long fileDuration = System.nanoTime() - fileStart;
                                this.main.getLogger().info("Loaded " + id + " in " + (fileDuration / 1_000_000.0) + " ms");
                            }
                        } else {
                            failedList.add(id);
                            this.main.getLogger().warning("Failed to load entity " + getEntityClass().getSimpleName() + " " + id);
                        }
                    } catch (Exception e) {
                        failedList.add(id);
                        this.main.getLogger().log(Level.SEVERE, "Exception while loading entity " + getEntityClass().getSimpleName() + " " + id, e);
                    }
                })).get();
            } catch (Exception e) {
                this.main.getLogger().log(Level.SEVERE, "Fatal error during parallel entity " + getEntityClass().getSimpleName() + " load", e);
            }
        }

        logLoadAll(successList, failedList, startTime);
    }

    /**
     * Checks if an entity with the specified identifier exists.
     *
     * @param id the unique identifier
     * @return {@code true} if the entity exists, {@code false} otherwise
     */
    @Override
    public boolean exists(@NonNull String id) {
        if (this.loadedEntities.containsKey(id)) {
            return true;
        }
        if (this.entitiesDirectory != null) {
            return new File(this.entitiesDirectory, id + ".json").exists();
        } else {
            return false;
        }
    }

    protected boolean save0(@NonNull E entity) {
        File tempFile;
        try {
            String jsonData;
            try {
                jsonData = GsonUtils.toJson(entity, true);
            } catch (Exception e) {
                main.getLogger().log(Level.SEVERE, "Failed to serialize entity " + getEntityClass().getSimpleName() + " data for " + entity.getUniqueId(), e);
                return false;
            }

            //noinspection ConstantConditions
            if (jsonData == null) {
                main.getLogger().log(Level.SEVERE, "Json data for {0} " + getEntityClass().getSimpleName() + " is null", entity.getUniqueId());
                return false;
            }

            File targetFile = new File(entitiesDirectory, entity.getUniqueId() + ".json");
            tempFile = new File(entitiesDirectory, entity.getUniqueId() + ".tmp");

            byte[] bytes = jsonData.getBytes(StandardCharsets.UTF_8);

            if (isShuttingDown) {
                Files.write(tempFile.toPath(), bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                String readJson = Files.readString(tempFile.toPath());
                E parsed = GsonUtils.fromJson(readJson, getEntityClass());

                if (parsed == null || !parsed.getUniqueId().equals(entity.getUniqueId())) {
                    throw new IOException("Parsed data is invalid or ID mismatch");
                }

                Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } else {
                //noinspection resource
                AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(
                        tempFile.toPath(),
                        StandardOpenOption.WRITE, StandardOpenOption.CREATE
                );

                ByteBuffer buffer = ByteBuffer.wrap(bytes);

                fileChannel.write(buffer, 0, entity.getUniqueId(), new CompletionHandler<Integer, Object>() {
                    @Override
                    public void completed(Integer result, Object attachment) {
                        try {
                            String readJson = Files.readString(tempFile.toPath());
                            E parsed = GsonUtils.fromJson(readJson, getEntityClass());

                            if (parsed == null || !parsed.getUniqueId().equals(entity.getUniqueId())) {
                                throw new IOException("Parsed data is invalid or ID mismatch");
                            }

                            Files.move(tempFile.toPath(), targetFile.toPath(),
                                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                        } catch (IOException e) {
                            main.getLogger().log(Level.SEVERE, "Failed to save entity " + getEntityClass().getSimpleName() + " data for " + entity.getUniqueId(), e);
                        } finally {
                            try {
                                fileChannel.close();
                            } catch (IOException e) {
                                main.getLogger().log(Level.WARNING, "Failed to close file channel for " + entity.getUniqueId(), e);
                            }
                        }
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        main.getLogger().log(Level.SEVERE, "Asynchronous file write failed for " + entity.getUniqueId(), exc);
                        try {
                            fileChannel.close();
                        } catch (IOException e) {
                            main.getLogger().log(Level.WARNING, "Failed to close file channel after failure for " + entity.getUniqueId(), e);
                        }
                    }
                });
            }
            return true;

        } catch (Exception e) {
            main.getLogger().log(Level.SEVERE, "Error while saving entity " + getEntityClass().getSimpleName() + " data for " + entity.getUniqueId(), e);
        }
        return false;
    }
}
