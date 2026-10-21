package com.georgev22.skinoverlay.utilities.config;

import org.bspfsystems.yamlconfiguration.file.FileConfiguration;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class SkinConfigurationFile {

    private final File file;
    private boolean setup = false;
    private FileConfiguration fileConfiguration;

    public SkinConfigurationFile(final File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setupFile() {

        if (setup) {
            throw new RuntimeException(
                    "The void file with the name: " + this.file.getName() + " can't be setup twice!\n" +
                            "If you believe this is an issue, contact GeorgeV22.");
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
