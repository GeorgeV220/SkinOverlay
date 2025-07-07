package com.georgev22.skinoverlay.storage.manager.gson;

import com.georgev22.skinoverlay.event.events.player.PlayerDataLoadEvent;
import com.georgev22.skinoverlay.event.events.player.PlayerDataSaveEvent;
import com.georgev22.skinoverlay.registry.EntityManagerRegistry;
import com.georgev22.skinoverlay.storage.EntityManager;
import com.georgev22.skinoverlay.storage.data.PlayerData;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.storage.gson.PlayerDataTypeAdapter;
import com.georgev22.skinoverlay.storage.manager.PlayerEntityManager;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import org.jetbrains.annotations.NotNull;

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
import java.util.function.Consumer;
import java.util.logging.Level;

@SuppressWarnings("DuplicatedCode")
public class PlayerFileManager extends PlayerEntityManager {

    private final File entitiesDirectory;

    public PlayerFileManager(File folder) {
        super();
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
    @Override
    public void delete(@NotNull PlayerData entity) {
        if (entitiesDirectory != null) {
            File file = new File(entitiesDirectory, entity.getId() + ".json");
            saveQueue.remove(entity.getId());

            if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
            this.loadedEntities.remove(entity.getId().toString());
            // Delete skins

            @NotNull Optional<EntityManager<Skin>> entityManager = EntityManagerRegistry.getManager(Skin.class);
            if (entityManager.isPresent()) {
                Optional<Skin> currentSkin = entityManager.get().getEntity(entity.getCurrentSkin().getId(), true);
                currentSkin.ifPresent(entityManager.get()::delete);
                Optional<Skin> defaultSkin = entityManager.get().getEntity(entity.getDefaultSkin().getId(), true);
                defaultSkin.ifPresent(entityManager.get()::delete);
            }

        }
    }

    /**
     * Loads an entity by its unique identifier.
     *
     * @param id the unique identifier
     * @return the entity, or {@code null} if not found
     */
    @Override
    public Optional<PlayerData> load(@NotNull String id) {
        File jsonFile = new File(entitiesDirectory, id + ".json");

        if (jsonFile.exists()) {
            try {
                String jsonData = Files.readString(jsonFile.toPath(), StandardCharsets.UTF_8);
                PlayerData playerData = PlayerDataTypeAdapter.fromJson(id, jsonData);

                if (playerData == null) {
                    this.mainPlugin.getLogger().log(Level.SEVERE, "Failed to load player " + id);
                    return Optional.empty();
                }
                this.loadedEntities.append(playerData.getId().toString(), playerData);
                this.mainPlugin.getEventBus().post(new PlayerDataLoadEvent(playerData));
                return Optional.of(playerData);
            } catch (IOException e) {
                this.mainPlugin.getLogger().log(Level.SEVERE, "Error while trying to load " + id + " player", e);
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
            mainPlugin.getLogger().info("No player data files found to load.");
            return;
        }

        int availableThreads = Math.max(4, Runtime.getRuntime().availableProcessors() / 2);
        ForkJoinPool customPool = new ForkJoinPool(availableThreads);

        List<String> successList = Collections.synchronizedList(new ArrayList<>());
        List<String> failedList = Collections.synchronizedList(new ArrayList<>());

        long startTime = System.nanoTime();

        try {
            customPool.submit(() -> Arrays.stream(files).parallel().forEach(file -> {
                String id = file.getName().replace(".json", "");

                try {
                    long fileStart = System.nanoTime();
                    Optional<PlayerData> playerData = this.load(id);
                    if (playerData.isPresent()) {
                        successList.add(id);
                        if (OptionsUtil.DEBUG.getBooleanValue()) {
                            this.mainPlugin.getLogger().info("Loaded player " + playerData.get().getId());
                        }
                        if (OptionsUtil.DEBUG.getBooleanValue()) {
                            long fileDuration = System.nanoTime() - fileStart;
                            this.mainPlugin.getLogger().info("Loaded " + id + " in " + (fileDuration / 1_000_000.0) + " ms");
                        }
                    } else {
                        failedList.add(id);
                        this.mainPlugin.getLogger().warning("Failed to load player " + id);
                    }
                } catch (Exception e) {
                    failedList.add(id);
                    this.mainPlugin.getLogger().log(Level.SEVERE, "Exception while loading player " + id, e);
                }
            })).get();
        } catch (Exception e) {
            this.mainPlugin.getLogger().log(Level.SEVERE, "Fatal error during parallel player load", e);
        }

        long duration = System.nanoTime() - startTime;

        this.mainPlugin.getLogger().info("Finished loading player data:");
        this.mainPlugin.getLogger().info("  ✔ Loaded: " + successList.size());
        this.mainPlugin.getLogger().info("  ✘ Failed: " + failedList.size());
        this.mainPlugin.getLogger().info("  ⏱ Duration: " + (duration / 1_000_000.0) + " ms");

        if (!failedList.isEmpty()) {
            if (!OptionsUtil.DEBUG.getBooleanValue())
                this.mainPlugin.getLogger().info("Enable DEBUG mode for more details.");
            else
                this.mainPlugin.getLogger().warning("Failed to load the following players: " + String.join(", ", failedList));
        }
    }

    @Override
    public void saveAll(Consumer<PlayerData> consumer) {
        this.mainPlugin.getLogger().info("Saving all player data...");
        batchSave();
        List<PlayerData> entities = new ArrayList<>(loadedEntities.values());
        for (PlayerData entity : entities) {
            consumer.accept(entity);
            saveEntityWithRetry(entity);
        }
    }

    /**
     * Returns all entities managed by this manager.
     *
     * @return a list of all entities
     */
    @Override
    public List<PlayerData> getAll() {
        return new ArrayList<>(this.loadedEntities.values());
    }

    /**
     * Checks if an entity with the specified identifier exists.
     *
     * @param id the unique identifier
     * @return {@code true} if the entity exists, {@code false} otherwise
     */
    @Override
    public boolean exists(@NotNull String id) {
        if (this.loadedEntities.containsKey(id)) {
            return true;
        }
        if (this.entitiesDirectory != null) {
            return new File(this.entitiesDirectory, id + ".json").exists();
        } else {
            return false;
        }
    }

    /**
     * Saves all entities in the save queue, executing the save operation in batches.
     */
    protected void batchSave() {
        if (isSaving || saveQueue.isEmpty()) {
            return;
        }

        isSaving = true;

        Set<UUID> toSave = new HashSet<>(saveQueue);
        saveQueue.clear();

        for (UUID entityId : toSave) {
            if (this.loadedEntities.get(entityId.toString()) == null) {
                continue;
            }
            saveEntityWithRetry(this.loadedEntities.get(entityId.toString()));
        }

        isSaving = false;
    }

    /**
     * Saves the entity to a temporary file before moving it to its final location.
     *
     * @param entity the entity to be saved
     * @return {@code true} if the save was successful, {@code false} otherwise
     */
    @SuppressWarnings("DuplicatedCode")
    protected boolean save0(PlayerData entity, boolean synchronous) {
        File tempFile;
        try {
            String jsonData = mainPlugin.getGson().toJson(entity);
            if (jsonData == null) {
                mainPlugin.getLogger().log(Level.SEVERE, "Failed to serialize player data for {0}", entity.getId());
                return false;
            }

            File targetFile = new File(entitiesDirectory, entity.getId() + ".json");
            tempFile = new File(entitiesDirectory, entity.getId() + ".tmp");

            byte[] bytes = jsonData.getBytes(StandardCharsets.UTF_8);

            if (synchronous) {
                Files.write(tempFile.toPath(), bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                String readJson = Files.readString(tempFile.toPath());
                PlayerData parsed = mainPlugin.getGson().fromJson(readJson, PlayerData.class);

                if (parsed == null || !parsed.getId().equals(entity.getId())) {
                    throw new IOException("Parsed data is invalid or ID mismatch");
                }

                Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                this.mainPlugin.getEventBus().post(new PlayerDataSaveEvent(entity));
            } else {
                //noinspection resource
                AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(
                        tempFile.toPath(),
                        StandardOpenOption.WRITE, StandardOpenOption.CREATE
                );

                ByteBuffer buffer = ByteBuffer.wrap(bytes);

                fileChannel.write(buffer, 0, entity.getId(), new CompletionHandler<Integer, Object>() {
                    @Override
                    public void completed(Integer result, Object attachment) {
                        try {
                            String readJson = Files.readString(tempFile.toPath());
                            PlayerData parsed = mainPlugin.getGson().fromJson(readJson, PlayerData.class);

                            if (parsed == null || !parsed.getId().equals(entity.getId())) {
                                throw new IOException("Parsed data is invalid or ID mismatch");
                            }

                            Files.move(tempFile.toPath(), targetFile.toPath(),
                                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                            PlayerFileManager.this.mainPlugin.getEventBus().post(new PlayerDataSaveEvent(entity));
                        } catch (IOException e) {
                            mainPlugin.getLogger().log(Level.SEVERE, "Failed to save player data for " + entity.getId(), e);
                        } finally {
                            try {
                                fileChannel.close();
                            } catch (IOException e) {
                                mainPlugin.getLogger().log(Level.WARNING, "Failed to close file channel for " + entity.getId(), e);
                            }
                        }
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        mainPlugin.getLogger().log(Level.SEVERE, "Asynchronous file write failed for " + entity.getId(), exc);
                        try {
                            fileChannel.close();
                        } catch (IOException e) {
                            mainPlugin.getLogger().log(Level.WARNING, "Failed to close file channel after failure for " + entity.getId(), e);
                        }
                    }
                });

            }
            return true;

        } catch (Exception e) {
            mainPlugin.getLogger().log(Level.SEVERE, "Error while saving player data for " + entity.getId(), e);
        }
        return false;
    }
}
