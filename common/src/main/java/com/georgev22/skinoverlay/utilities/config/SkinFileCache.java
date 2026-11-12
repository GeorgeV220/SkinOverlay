package com.georgev22.skinoverlay.utilities.config;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.datastructures.maps.HashObjectMap;
import com.georgev22.skinoverlay.datastructures.maps.ObjectMap;
import com.georgev22.skinoverlay.registry.OverlayRegistry;
import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import com.georgev22.skinoverlay.utilities.Utils;
import com.georgev22.skinoverlay.utilities.skin.Model;
import com.georgev22.skinoverlay.utilities.skin.Overlay;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

/**
 * Manages the discovery, extraction, loading, and caching of skin overlays.
 *
 * <p>Overlay images are stored under the following directory structure:</p>
 *
 * <pre>
 * skins/
 * ├── classic/
 * │   └── mustache.png
 * └── slim/
 *     └── mustache.png
 * </pre>
 *
 * <p>Each configured overlay has a corresponding YAML configuration under
 * {@code skins-cfg/}, named after the overlay:</p>
 *
 * <pre>
 * skins-cfg/
 * └── mustache.yml
 * </pre>
 *
 * <p>An overlay may have a classic image, a slim image, or both. Configurations
 * without at least one corresponding image are ignored.</p>
 *
 * <p>Cached overlays are indexed by model and overlay key. For example,
 * {@code classic/mustache} and {@code slim/mustache} are separate entries.</p>
 *
 * <p>The shared {@link OverlayRegistry} also contains saved runtime overlays
 * from {@code generated-overlays/}. These files include their own image data
 * and do not require matching PNG files under {@code skins/}.</p>
 */
public class SkinFileCache {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    private final OverlayRegistry overlayRegistry = skinOverlay.getOverlayRegistry();

    /**
     * Extracts bundled default resources when necessary and loads the available
     * overlay configurations and images into the shared registry. Saved runtime
     * overlays are loaded after configured overlays.
     *
     * <p>Existing user resources are preserved by passing {@code false} to
     * {@link Utils#saveResource(String, boolean, File, Class)}.</p>
     *
     * <p>The registry is cleared before loading begins, discarding registrations
     * that were not saved to disk. If loading fails partway through, the registry
     * may contain only the overlays loaded successfully.</p>
     */
    public void cache() {
        File dataFolder = skinOverlay.getDataFolder();
        File skinsFolder = new File(dataFolder, "skins");
        File classicFolder = new File(skinsFolder, "classic");
        File slimFolder = new File(skinsFolder, "slim");
        File configFolder = new File(dataFolder, "skins-cfg");

        if (!createDirectory(classicFolder)
                || !createDirectory(slimFolder)
                || !createDirectory(configFolder)) {
            return;
        }

        saveDefaultOverlays("skins/classic", ".png");
        saveDefaultOverlays("skins/slim", ".png");
        saveDefaultOverlays("skins-cfg", ".yml");

        overlayRegistry.clear();

        File[] configs = configFolder.listFiles(file ->
                file.isFile()
                        && file.getName().toLowerCase(Locale.ROOT).endsWith(".yml")
        );

        if (configs == null) {
            skinOverlay.getLogger().warning(
                    "Failed to list skin configurations in " + configFolder
            );
            return;
        }

        for (File configFile : configs) {
            String filename = configFile.getName();
            String key = filename.substring(0, filename.length() - ".yml".length());

            File classicFile = new File(classicFolder, key + ".png");
            File slimFile = new File(slimFolder, key + ".png");

            boolean hasClassic = classicFile.isFile();
            boolean hasSlim = slimFile.isFile();

            if (!hasClassic && !hasSlim) {
                skinOverlay.getLogger().fine(
                        "Skipping overlay '" + key + "': no PNG files found"
                );
                continue;
            }

            try {
                SkinConfigurationFile configurationFile =
                        new SkinConfigurationFile(configFile);
                configurationFile.setupFile();

                if (hasClassic) {
                    try {
                        loadOverlay(
                                key,
                                Model.CLASSIC,
                                configurationFile,
                                classicFile
                        );
                    } catch (Exception e) {
                        logOverlayFailure(key, Model.CLASSIC, e);
                    }
                }

                if (hasSlim) {
                    try {
                        loadOverlay(
                                key,
                                Model.SLIM,
                                configurationFile,
                                slimFile
                        );
                    } catch (Exception e) {
                        logOverlayFailure(key, Model.SLIM, e);
                    }
                }
            } catch (Exception e) {
                skinOverlay.getLogger().log(
                        Level.SEVERE,
                        "Failed to load overlay configuration: " + filename,
                        e
                );
            }
        }

        try {
            overlayRegistry.loadSaved((file, exception) -> skinOverlay.getLogger().log(
                    Level.SEVERE, "Failed to load generated overlay: " + file, exception));
        } catch (IOException exception) {
            skinOverlay.getLogger().log(Level.SEVERE, "Failed to load generated overlays", exception);
        }

        skinOverlay.getLogger().info(
                "Loaded " + overlayRegistry.entries().size() + " overlay model(s)"
        );
    }

    /**
     * Creates a directory if it does not already exist.
     *
     * @param directory directory to create
     * @return {@code true} if the directory exists or was created successfully;
     * {@code false} otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean createDirectory(@NonNull File directory) {
        if (directory.isDirectory()) {
            return true;
        }

        if (directory.exists() || directory.mkdirs()) {
            if (directory.isDirectory()) {
                return true;
            }
        }

        skinOverlay.getLogger().severe(
                "Failed to create directory: " + directory
        );
        return false;
    }

    /**
     * Loads an overlay image and registers it in the cache.
     *
     * @param key               logical overlay identifier, derived from the
     *                          configuration filename
     * @param model             player skin model represented by the image
     * @param configurationFile configuration shared by this overlay's models
     * @param imageFile         PNG image to load
     * @throws IOException if the image cannot be read
     */
    private void loadOverlay(
            @NonNull String key,
            @NonNull Model model,
            @NonNull SkinConfigurationFile configurationFile,
            @NonNull File imageFile
    ) throws IOException {
        BufferedImage image = ImageIO.read(imageFile);

        if (image == null) {
            throw new IOException("Unsupported or invalid image: " + imageFile);
        }

        SerializableBufferedImage bufferedImage =
                new SerializableBufferedImage(image);

        Overlay overlay = new Overlay(
                key,
                model,
                configurationFile,
                bufferedImage
        );

        overlayRegistry.register(overlay);
    }

    /**
     * Logs a model-specific overlay loading failure.
     *
     * @param key   overlay identifier
     * @param model skin model that failed to load
     * @param cause underlying failure
     */
    private void logOverlayFailure(
            @NonNull String key,
            @NonNull Model model,
            @NonNull Exception cause
    ) {
        skinOverlay.getLogger().log(
                Level.SEVERE,
                "Failed to load " + model + " overlay: " + key,
                cause
        );
    }

    /**
     * Lists resources directly inside a folder in the plugin's packaged JAR.
     *
     * <p>Subdirectories are not traversed. Matching is case-insensitive for
     * the file extension.</p>
     *
     * <p>This method enumerates JAR entries directly and does not depend on
     * directory entries being present in the archive.</p>
     *
     * @param folder    resource folder relative to the JAR root, such as
     *                  {@code skins/classic}
     * @param extension file extension to match, such as {@code .png}
     * @return resource paths relative to the JAR root; an empty list if the
     * resources cannot be enumerated
     */
    public @NonNull List<String> listResources(
            @NonNull String folder,
            @NonNull String extension
    ) {
        String prefix = folder.endsWith("/") ? folder : folder + "/";
        String suffix = extension.toLowerCase(Locale.ROOT);

        List<String> files = new ArrayList<>();

        try {
            URL location = SkinOverlay.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation();

            Path jarPath = Path.of(location.toURI());

            if (!Files.isRegularFile(jarPath)
                    || !jarPath.getFileName().toString()
                    .toLowerCase(Locale.ROOT).endsWith(".jar")) {
                skinOverlay.getLogger().warning(
                        "Cannot enumerate JAR resources outside a packaged JAR: "
                                + folder
                );
                return files;
            }

            try (JarFile jar = new JarFile(jarPath.toFile())) {
                Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();

                    if (!entry.isDirectory()
                            && name.startsWith(prefix)
                            && name.toLowerCase(Locale.ROOT).endsWith(suffix)
                            && name.indexOf('/', prefix.length()) == -1) {
                        files.add(name);
                    }
                }
            }
        } catch (Exception e) {
            skinOverlay.getLogger().log(
                    Level.SEVERE,
                    "Failed to list resources in: " + folder,
                    e
            );
        }

        return files;
    }

    /**
     * Copies bundled resources of the specified extension into the plugin's
     * data folder without overwriting existing files.
     *
     * @param path      resource folder relative to the JAR root
     * @param extension resource extension, such as {@code .png} or {@code .yml}
     */
    private void saveDefaultOverlays(
            @NonNull String path,
            @NonNull String extension
    ) {
        listResources(path, extension).forEach(resource -> {
            try {
                Utils.saveResource(
                        resource,
                        false,
                        skinOverlay.getDataFolder(),
                        SkinOverlay.class
                );
            } catch (Exception e) {
                skinOverlay.getLogger().log(
                        Level.SEVERE,
                        "Failed to save default resource: " + resource,
                        e
                );
            }
        });
    }

    /**
     * Returns an unmodifiable snapshot of configured and runtime overlay registrations.
     *
     * <p>Keys use the format {@code model/overlayKey}, for example
     * {@code slim/mustache}. Changes to the internal cache after this method
     * returns are not reflected in the returned map. The overlay instances themselves
     * are shared with the registry and remain mutable.</p>
     *
     * @return unmodifiable map containing the cached overlays
     */
    @Unmodifiable
    public @NonNull ObjectMap<String, Overlay> getOverlayMap() {
        return new HashObjectMap<>(overlayRegistry.entries()).toUnmodifiableObjectMap();
    }

    /**
     * Resolves an overlay identifier to its cached configuration.
     * <p>
     * Unqualified keys prefer the classic model when both models exist, preserving
     * legacy configuration lookup behavior. Use a model-qualified identifier for an
     * exact lookup; command resolution through the registry requires disambiguation.
     * </p>
     *
     * @param identifier overlay name or {@code model/name}, e.g.
     *                   {@code mustache}, {@code classic/mustache}
     * @return the cached configuration, or an empty optional if no matching overlay exists
     */
    public Optional<SkinConfigurationFile> resolveConfiguration(@NonNull String identifier) {
        Overlay overlay = overlayRegistry.get(identifier).orElse(null);

        if (overlay != null) {
            return Optional.of(overlay.configurationFile());
        }

        overlay = overlayRegistry.get(Model.CLASSIC.name().toLowerCase(Locale.ROOT) + "/" + identifier).orElse(null);

        if (overlay == null) {
            overlay = overlayRegistry.get(Model.SLIM.name().toLowerCase(Locale.ROOT) + "/" + identifier).orElse(null);
        }

        return overlay != null ? Optional.of(overlay.configurationFile()) : Optional.empty();
    }
}
