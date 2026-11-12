package com.georgev22.skinoverlay.utilities.config;

import org.bspfsystems.yamlconfiguration.file.FileConfiguration;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

/**
 * Holds an overlay's YAML configuration and its associated file location.
 * <p>
 * File-backed instances load their configuration through {@link #setupFile()} or
 * {@link #reloadFile()}. Runtime instances accept an already initialized configuration
 * and perform no file I/O during construction. This class does not save configurations.
 * </p>
 */
public class SkinConfigurationFile {

    private final File file;
    private boolean setup = false;
    private FileConfiguration fileConfiguration;

    /**
     * Creates an uninitialized configuration backed by the given file.
     *
     * @param file YAML file to load when this instance is initialized
     * @throws NullPointerException if {@code file} is {@code null}
     */
    public SkinConfigurationFile(final File file) {
        this.file = Objects.requireNonNull(file, "Configuration file cannot be null");
    }

    /**
     * Creates an initialized configuration for a runtime overlay without reading or writing a file.
     * <p>
     * The supplied configuration is retained directly, so subsequent changes to it are visible
     * through this instance. The file is an associated location; it does not need to exist.
     * Calling {@link #reloadFile()} replaces the supplied configuration with the file's contents.
     * </p>
     *
     * @param file          associated YAML file location
     * @param configuration initialized configuration to retain
     * @throws NullPointerException if either argument is {@code null}
     */
    public SkinConfigurationFile(final File file, final FileConfiguration configuration) {
        this.file = Objects.requireNonNull(file, "Configuration file cannot be null");
        this.fileConfiguration = Objects.requireNonNull(configuration, "Configuration cannot be null");
        this.setup = true;
    }

    /**
     * Returns the file associated with this configuration.
     *
     * @return the file location, which may not exist for a runtime configuration
     */
    public File getFile() {
        return file;
    }

    /**
     * Loads the associated YAML file and marks this instance as initialized.
     *
     * @throws IllegalStateException if setup has already completed, including through
     *                               the runtime-configuration constructor
     */
    public void setupFile() {
        if (setup) {
            throw new IllegalStateException("Configuration has already been initialized: " + file);
        }

        this.reloadFile();
        setup = true;
    }

    /**
     * Replaces the current configuration with the associated YAML file's contents.
     * <p>
     * Unsaved changes are discarded. The YAML loader supplies an empty configuration
     * when the file is absent; this method does not create the file.
     * </p>
     */
    public void reloadFile() {
        this.fileConfiguration = YamlConfiguration.loadConfiguration(this.file);
    }

    /**
     * Returns the current mutable configuration.
     *
     * @return the configuration supplied at construction or most recently loaded from disk
     * @throws IllegalStateException if no configuration has been supplied or loaded
     */
    @NotNull
    public FileConfiguration getFileConfiguration() {
        if (fileConfiguration == null) {
            throw new IllegalStateException("Configuration has not been initialized: " + file);
        }
        return this.fileConfiguration;
    }
}
