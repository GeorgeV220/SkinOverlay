package com.georgev22.skinoverlay.storage.manager.gson;

import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.storage.gson.SkinTypeAdapter;
import com.georgev22.skinoverlay.storage.manager.SkinEntityManager;

import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

@SuppressWarnings("DuplicatedCode")
public class SkinFileManager extends SkinEntityManager {
    private final File dataFolder;

    public SkinFileManager(@NotNull File dataFolder) {
        super();
        this.dataFolder = dataFolder;
        if (!dataFolder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dataFolder.mkdirs();
        }
    }

    @Override
    public Optional<Skin> load(@NotNull String id) {
        File file = new File(dataFolder, id + ".json");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                Skin chest = SkinTypeAdapter.fromJson(id, sb.toString());
                if (chest == null) {
                    return Optional.empty();
                }
                loadedEntities.append(chest.getId().toString(), chest);
                this.postLoad(chest);
                return Optional.of(chest);
            } catch (IOException e) {
                mainPlugin.getLogger().log(Level.SEVERE, "Failed to load void chest " + id, e);
            }
        }
        return Optional.empty();
    }

    @Override
    public void delete(@NotNull Skin entity) {
        File file = new File(dataFolder, entity.getId() + ".json");
        saveQueue.remove(entity.getId());

        if (file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
//
//        @NotNull Optional<EntityManager<PlayerData>> entityManager = EntityManagerRegistry.getManager(PlayerData.class);
//        if (entityManager.isEmpty()) return;
//        Optional<PlayerData> playerData = entityManager.get().getEntity(entity.ownerUUID().toString(), true);
//        if (playerData.isPresent()) {
//            // TODO Delete skin
//            entityManager.get().save(playerData.get());
//        }
//        loadedEntities.remove(entity.getId().toString());
    }

    @Override
    public void loadAll() {
        File[] files = this.dataFolder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            mainPlugin.getLogger().info("No skin data files found to load.");
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
                    Optional<Skin> playerData = this.load(id);
                    if (playerData.isPresent()) {
                        successList.add(id);
                        if (OptionsUtil.DEBUG.getBooleanValue()) {
                            this.mainPlugin.getLogger().info("Loaded skin " + playerData.get().getId());
                        }
                        if (OptionsUtil.DEBUG.getBooleanValue()) {
                            long fileDuration = System.nanoTime() - fileStart;
                            this.mainPlugin.getLogger().info("Loaded " + id + " in " + (fileDuration / 1_000_000.0) + " ms");
                        }
                    } else {
                        failedList.add(id);
                        this.mainPlugin.getLogger().warning("Failed to load skin " + id);
                    }
                } catch (Exception e) {
                    failedList.add(id);
                    this.mainPlugin.getLogger().log(Level.SEVERE, "Exception while loading skin " + id, e);
                }
            })).get();
        } catch (Exception e) {
            this.mainPlugin.getLogger().log(Level.SEVERE, "Fatal error during parallel skin load", e);
        }

        long duration = System.nanoTime() - startTime;

        this.mainPlugin.getLogger().info("Finished loading skin data:");
        this.mainPlugin.getLogger().info("  ✔ Loaded: " + successList.size());
        this.mainPlugin.getLogger().info("  ✘ Failed: " + failedList.size());
        this.mainPlugin.getLogger().info("  ⏱ Duration: " + (duration / 1_000_000.0) + " ms");

        if (!failedList.isEmpty()) {
            if (!OptionsUtil.DEBUG.getBooleanValue())
                this.mainPlugin.getLogger().info("Enable DEBUG mode for more details.");
            else
                this.mainPlugin.getLogger().warning("Failed to load the following skins: " + String.join(", ", failedList));
        }
    }

    @Override
    public boolean exists(@NotNull String id) {
        if (loadedEntities.containsKey(id)) return true;
        return new File(dataFolder, id + ".json").exists();
    }

    protected boolean save0(Skin entity, boolean synchronous) {
        File tempFile;
        try {
            String jsonData = mainPlugin.getGson().toJson(entity);
            if (jsonData == null) {
                mainPlugin.getLogger().log(Level.SEVERE, "Failed to serialize skin data for {0}", entity.getId());
                return false;
            }

            File targetFile = new File(dataFolder, entity.getId() + ".json");
            tempFile = new File(dataFolder, entity.getId() + ".tmp");

            byte[] bytes = jsonData.getBytes(StandardCharsets.UTF_8);

            if (synchronous) {
                Files.write(tempFile.toPath(), bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                String readJson = Files.readString(tempFile.toPath());
                Skin parsed = SkinTypeAdapter.fromJson(entity.getId().toString(), readJson);

                if (parsed == null || !parsed.getId().equals(entity.getId())) {
                    throw new IOException("Parsed data is invalid or ID mismatch");
                }

                Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                this.postSave(entity);
            } else {
                //noinspection resource
                AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(
                        tempFile.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

                ByteBuffer buffer = ByteBuffer.wrap(bytes);

                fileChannel.write(buffer, 0, entity.getId(), new CompletionHandler<Integer, Object>() {
                    @Override
                    public void completed(Integer result, Object attachment) {
                        try {
                            String readJson = Files.readString(tempFile.toPath());
                            Skin parsed = SkinTypeAdapter.fromJson(entity.getId().toString(), readJson);

                            if (parsed == null || !parsed.getId().equals(entity.getId())) {
                                throw new IOException("Parsed data is invalid or ID mismatch");
                            }

                            Files.move(tempFile.toPath(), targetFile.toPath(),
                                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                            SkinFileManager.this.postSave(entity);
                        } catch (IOException e) {
                            mainPlugin.getLogger().log(Level.SEVERE, "Failed to save void chest data for " + entity.getId(), e);
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
            mainPlugin.getLogger().log(Level.SEVERE, "Error while saving void chest data for " + entity.getId(), e);
        }

        return false;
    }
}
