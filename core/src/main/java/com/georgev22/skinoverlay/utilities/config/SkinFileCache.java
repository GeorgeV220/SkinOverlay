package com.georgev22.skinoverlay.utilities.config;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Level;

public class SkinFileCache {
    private final ObjectMap<String, SkinConfigurationFile> skinConfigurationFiles = new HashObjectMap<>();

    private final ObjectMap<String, SerializableBufferedImage> skinImages = new HashObjectMap<>();

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @NotNull
    public ObjectMap<String, SkinConfigurationFile> getSkinConfigurationFiles() {
        return skinConfigurationFiles;
    }

    public ObjectMap<String, SerializableBufferedImage> getSkinImages() {
        return skinImages;
    }

    public void cache() {
        this.skinOverlay.getLogger().info("Attempting to load skins and its configuration files in memory.");
        this.skinConfigurationFiles.clear();
        this.skinImages.clear();

        final File folder = new File(skinOverlay.getDataFolder(), "skins-cfg");
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                this.skinOverlay.getLogger().log(Level.INFO, "skins-cfg folder was successfully created.");
            }
            this.copyFilesToFolder(false);
            this.skinOverlay.getLogger().info("skins-cfg folder didn't exist, creating one and copy the default skins configuration files for it.");
        }

        if (!this.skinOverlay.getSkinsDataFolder().exists()) {
            if (this.skinOverlay.getSkinsDataFolder().mkdirs()) {
                this.skinOverlay.getLogger().log(Level.INFO, "skins data folder was successfully created!!");
            }
            this.copyFilesToFolder(true);
            this.skinOverlay.getLogger().info("skins folder didn't exist, creating one and copy the default skins for it.");
        }

        for (File file : Objects.requireNonNull(this.skinOverlay.getSkinsDataFolder().listFiles((dir, name) -> name.endsWith(".png")))) {
            final String skinName = file.getName().substring(0, file.getName().length() - 4);

            if (skinName.isEmpty()) {
                this.skinOverlay.getLogger().info("Invalid skin file name: " + skinName);
                continue;
            }

            try {
                SerializableBufferedImage serializableBufferedImage = new SerializableBufferedImage(ImageIO.read(file));
                this.skinImages.put(skinName, serializableBufferedImage);
                this.skinOverlay.getLogger().info("Skin image file: " + skinName + " successfully loaded in memory.");
            } catch (IOException e) {
                this.skinOverlay.getLogger().log(Level.WARNING, "Cannot load skin image file: " + skinName, e);
            }
        }

        for (File file : Objects.requireNonNull(folder.listFiles((dir, name) -> name.endsWith(".yml")))) {
            final String skinName = file.getName().substring(0, file.getName().length() - 4);

            if (skinName.isEmpty()) {
                this.skinOverlay.getLogger().info("Invalid skin configuration file name: " + skinName);
                continue;
            }

            final SkinConfigurationFile skinFile = new SkinConfigurationFile(skinName);
            skinFile.setupFile();
            this.skinConfigurationFiles.put(skinName, skinFile);
            this.skinOverlay.getLogger().info("Skin configuration file: " + skinName + " successfully loaded in memory.");
        }

    }

    private void copyFilesToFolder(boolean b) {
        String[] resources = new String[]{"alley", "frog_pijama", "bubbo_transparent", "fire_demon", "flame", "glare", "hoodie", "migrator", "pirate", "smoking", "policeman", "mustache"};
        if (b) {
            for (String resource : resources) {
                if (new File(this.skinOverlay.getSkinsDataFolder(), resource + ".png").exists()) continue;
                try {
                    Utils.saveResource("skins/" + resource + ".png", false, this.skinOverlay.getDataFolder(), this.getClass());
                } catch (Exception e) {
                    this.skinOverlay.getLogger().log(Level.WARNING, "Cannot save default skins: ", e.getCause());
                }
            }
        } else {
            for (String resource : resources) {
                if (new File(this.skinOverlay.getDataFolder(), "skins-cfg/" + resource + ".yml").exists()) continue;
                try {
                    Utils.saveResource("skins-cfg/" + resource + ".yml", false, this.skinOverlay.getDataFolder(), this.skinOverlay.getClass());
                } catch (Exception e) {
                    this.skinOverlay.getLogger().log(Level.WARNING, "Cannot save default skin configuration files: ", e.getCause());
                }
            }
        }
    }

    /*
     * Returns a cached or attempts to cache a possible SkinFile object from
     * a specific key.
     */
    @Nullable
    public SkinConfigurationFile getCacheSkinConfig(final String name) {
        SkinConfigurationFile cache = this.skinConfigurationFiles.get(name);
        if (cache != null) {
            return cache;
        }
        for (Entry<String, SkinConfigurationFile> entry : this.skinConfigurationFiles.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Nullable
    public SkinConfigurationFile getCacheSkinConfig(final @NotNull Skin skin) {
        return this.getCacheSkinConfig(skin.skinName());
    }

    @Nullable
    public SerializableBufferedImage getSkinImage(final String skinName) {
        SerializableBufferedImage cache = this.skinImages.get(skinName);
        if (cache != null) {
            return cache;
        }
        for (Entry<String, SerializableBufferedImage> entry : this.skinImages.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(skinName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Nullable
    public SerializableBufferedImage getSkinImage(final @NotNull Skin skin) {
        return this.getSkinImage(skin.skinName());
    }
}
