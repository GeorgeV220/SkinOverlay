package com.georgev22.skinoverlay.utilities.config;

import org.bspfsystems.yamlconfiguration.file.FileConfiguration;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CFG {

    private final static Set<CFG> cachedFiles = new HashSet<>();
    /* The file's name (without the .yml) */
    private final String fileName;
    private final Logger logger;
    private final File dataFolder;
    private final Class<?> clazz;
    private final boolean saveResource;
    private final boolean replace;
    /* The yml file configuration. */
    private FileConfiguration fileConfiguration;
    /* The file. */
    private File file;

    public CFG(
            final String string,
            final File dataFolder,
            final boolean saveResource,
            final boolean replace,
            final Logger logger,
            final Class<?> clazz
    ) throws Exception {
        this.fileName = string + ".yml";
        this.dataFolder = dataFolder;
        this.saveResource = saveResource;
        this.replace = replace;
        this.logger = logger;
        this.clazz = clazz;
        this.setup();
        cachedFiles.add(this);
    }

    public static void reloadFiles() {
        cachedFiles.forEach(CFG::reloadFile);
    }

    /**
     * Attempts to load the file.
     *
     * @see #reloadFile()
     */
    public void setup() throws Exception {
        if (!dataFolder.exists()) {
            if (dataFolder.mkdir()) {
                logger.info("Folder " + dataFolder.getName() + " has been created!");
            }
        }

        this.file = new File(dataFolder, this.fileName);

        if (!this.file.exists()) {
            try {
                if (this.file.createNewFile()) {
                    logger.info("File " + this.file.getName() + " has been created!");
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            if (saveResource) {
                saveResource(this.fileName, this.dataFolder, this.clazz);
            }
        }

        this.reloadFile();
    }

    private void saveResource(@NotNull String resourcePath, File dataFolder, Class<?> clazz) throws Exception {
        if (resourcePath.equals("")) {
            throw new Exception("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath, clazz);
        if (in == null) {
            this.logger.warning("The embedded resource '" + resourcePath + "' cannot be found in " + new java.io.File(clazz.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath())
                    .getName());
        }

        File outFile = new File(dataFolder, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(dataFolder, resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) {
            if (outDir.mkdirs()) {
                logger.info("Folder " + outDir.getPath() + " has been created!");
            }
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = Files.newOutputStream(outFile.toPath());
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                logger.warning("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            this.logger.log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    @Nullable
    private InputStream getResource(@NotNull String filename, @NotNull Class<?> clazz) {
        try {
            URL url = clazz.getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Saves the file configuration.
     *
     * @see FileConfiguration#save(File)
     * @see #getFileConfiguration()
     */
    public void saveFile() {
        try {
            this.getFileConfiguration().save(this.file);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reloads the file.
     *
     * @see YamlConfiguration#loadConfiguration(File)
     * @see #file
     */
    public void reloadFile() {
        this.fileConfiguration = YamlConfiguration.loadConfiguration(this.file);
    }

    /**
     * @return the file - The {@link FileConfiguration}.
     */
    public FileConfiguration getFileConfiguration() {
        return this.fileConfiguration;
    }

    /**
     * Get the file
     *
     * @return the file
     */
    public File getFile() {
        return file;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, fileConfiguration, file, saveResource);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CFG cfg = (CFG) o;
        return saveResource == cfg.saveResource && Objects.equals(fileName, cfg.fileName) && Objects.equals(fileConfiguration, cfg.fileConfiguration) && Objects.equals(file, cfg.file);
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "CFG{" +
                "fileName='" + fileName + '\'' +
                ", fileConfiguration=" + fileConfiguration +
                ", file=" + file +
                ", saveResource=" + saveResource +
                '}';
    }
}