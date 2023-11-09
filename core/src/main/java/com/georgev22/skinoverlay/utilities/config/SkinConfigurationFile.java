package com.georgev22.skinoverlay.utilities.config;


import com.georgev22.library.yaml.file.FileConfiguration;
import com.georgev22.library.yaml.file.YamlConfiguration;
import com.georgev22.skinoverlay.SkinOverlay;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class SkinConfigurationFile {

    private final String name;
    private FileConfiguration fileConfiguration;
    private File file;
    private boolean setup = false;

    public SkinConfigurationFile(final String name) {
        this.name = name;

    }

    public File getFile() {
        return file;
    }

    public void setupFile() {

        if (setup) {
            throw new RuntimeException(
                    "The void file with the name: " + this.name + " can't be setup twice!\n" +
                            "If you believe this is an issue, contact GeorgeV22.");
        }

        final SkinOverlay plugin = SkinOverlay.getInstance();

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        this.file = new File(plugin.getDataFolder(), "skins-cfg" + File.separator + this.name + ".yml");

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (final IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while creating a new file:", e);
            }
        }
        this.reloadFile();
        setup = true;
    }

    public void reloadFile() {
        this.fileConfiguration = YamlConfiguration.loadConfiguration(this.file);
    }

    @NotNull
    public FileConfiguration getFileConfiguration() {
        return this.fileConfiguration;
    }
}
