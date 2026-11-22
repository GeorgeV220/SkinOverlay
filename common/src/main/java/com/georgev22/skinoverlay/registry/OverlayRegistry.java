package com.georgev22.skinoverlay.registry;

import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import com.georgev22.skinoverlay.utilities.config.SkinConfigurationFile;
import com.georgev22.skinoverlay.utilities.skin.Model;
import com.georgev22.skinoverlay.utilities.skin.Overlay;
import com.georgev22.skinoverlay.utilities.skin.OverlayRenderer;
import org.bspfsystems.yamlconfiguration.configuration.InvalidConfigurationException;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Stores configured and runtime overlays under model-qualified identifiers such as {@code classic/hat}.
 * <p>
 * Registration and replacement affect memory only. {@link #registerAndSave(Overlay)},
 * {@link #combine(String, Collection)} and {@link #generate(Collection)} also persist new overlays
 * as YAML files containing configuration and embedded PNG data. Saving and composition perform
 * blocking I/O and should run on a worker thread.
 * </p>
 * <p>
 * Combinations retain their source identifiers. Their source definitions must remain registered
 * to expand or recompose them; persist runtime sources separately when they must survive a restart.
 * Inherited removal and clearing operations affect memory only and do not delete saved files.
 * </p>
 */
public final class OverlayRegistry extends AbstractRegistry<String, Overlay> {
    private static final List<String> PARTS = List.of(
            "hat", "jacket", "left_sleeve", "right_sleeve", "left_pants", "right_pants");
    private final Path directory;

    /**
     * Creates a registry using the given directory for runtime overlay files.
     *
     * @param directory storage root containing model directories such as {@code classic/}
     */
    public OverlayRegistry(Path directory) {
        this.directory = Objects.requireNonNull(directory, "Overlay directory cannot be null");
    }

    /**
     * Registers an overlay in memory using {@link Overlay#identifier()}.
     *
     * @param overlay overlay to register without copying or saving it
     * @throws IllegalArgumentException if the key or image dimensions are invalid, or the identifier exists
     */
    @Override
    public synchronized void register(@NotNull Overlay overlay) {
        register(overlay.identifier(), overlay);
    }

    /**
     * Registers an overlay in memory under its model-qualified identifier.
     *
     * @param identifier identifier matching {@link Overlay#identifier()}
     * @param overlay    overlay to register without copying or saving it
     * @throws IllegalArgumentException if the identifier does not match, the key or image is invalid,
     *                                  or an overlay is already registered under the identifier
     */
    @Override
    public synchronized void register(@NotNull String identifier, @NotNull Overlay overlay) {
        validateIdentifier(identifier, overlay);
        super.register(identifier, overlay);
    }

    /**
     * Registers or replaces an overlay in memory using {@link Overlay#identifier()}.
     *
     * @param overlay overlay to register without copying or saving it
     * @return {@code true} if an existing registration was replaced
     * @throws IllegalArgumentException if the key or image dimensions are invalid
     */
    @Override
    public synchronized boolean replaceOrRegister(@NotNull Overlay overlay) {
        return replaceOrRegister(overlay.identifier(), overlay);
    }

    /**
     * Registers or replaces an overlay in memory under its model-qualified identifier.
     *
     * @param identifier identifier matching {@link Overlay#identifier()}
     * @param overlay    overlay to register without copying or saving it
     * @return {@code true} if an existing registration was replaced
     * @throws IllegalArgumentException if the identifier does not match or the key or image is invalid
     */
    @Override
    public synchronized boolean replaceOrRegister(@NotNull String identifier, @NotNull Overlay overlay) {
        validateIdentifier(identifier, overlay);
        return super.replaceOrRegister(identifier, overlay);
    }

    private void validateIdentifier(String identifier, Overlay overlay) {
        if (!overlay.key().matches("[A-Za-z0-9_-]+") || !identifier.equals(overlay.identifier())) {
            throw new IllegalArgumentException("Expected model/key with a key containing only letters, numbers, _ or -: " + identifier);
        }
        Objects.requireNonNull(overlay.configurationFile(), "Overlay configuration is required");
        Objects.requireNonNull(overlay.getBufferedImage(), "Overlay image is required");
        BufferedImage image = overlay.getBufferedImage().getBufferedImage();
        if (image.getWidth() != 64 || (image.getHeight() != 64 && image.getHeight() != 32)) {
            throw new IllegalArgumentException("Overlay images must be 64x64 or legacy 64x32 pixels");
        }
    }

    /**
     * Resolves a model-qualified identifier or an unqualified key with exactly one registered model.
     *
     * @param identifier overlay key or model-qualified identifier
     * @return the registered overlay instance
     * @throws IllegalArgumentException if no overlay matches or the key matches multiple models
     */
    public Overlay require(String identifier) {
        Optional<Overlay> exact = get(identifier);
        if (exact.isPresent()) return exact.get();
        List<Overlay> matches = registry.values().stream().filter(overlay -> overlay.key().equals(identifier)).toList();
        if (matches.size() == 1) return matches.getFirst();
        if (matches.size() > 1)
            throw new IllegalArgumentException("Specify classic/ or slim/ for overlay: " + identifier);
        throw new IllegalArgumentException("Unknown overlay: " + identifier);
    }

    /**
     * Expands combinations into unique leaf overlays and validates their compatibility.
     * <p>
     * Leaves are ordered by ascending layer, then by model-qualified identifier. Duplicates
     * are identified by their identifiers. Two different leaves cannot occupy the same
     * case-sensitive type and layer, and all leaves must use the same player model.
     * </p>
     *
     * @param overlays non-empty collection of overlays or combinations to expand
     * @return an immutable list of leaf overlays in rendering order
     * @throws IllegalArgumentException if the collection is empty, a source cannot be resolved,
     *                                  a component cycle exists, slots conflict, or models differ
     */
    public List<Overlay> flatten(Collection<Overlay> overlays) {
        Map<String, Overlay> leaves = new LinkedHashMap<>();
        for (Overlay overlay : overlays) expand(overlay, leaves, new HashSet<>());
        if (leaves.isEmpty()) throw new IllegalArgumentException("At least one overlay is required");
        List<Overlay> result = leaves.values().stream()
                .sorted(Comparator.comparingInt(Overlay::layer).thenComparing(Overlay::identifier))
                .toList();
        Model model = result.getFirst().model();
        Map<List<Object>, Overlay> slots = new HashMap<>();
        for (Overlay overlay : result) {
            if (overlay.model() != model)
                throw new IllegalArgumentException("All overlays must use the same classic/slim model");
            Overlay previous = slots.putIfAbsent(List.of(overlay.type(), overlay.layer()), overlay);
            if (previous != null) {
                throw new IllegalArgumentException("Conflicting overlays " + previous.identifier() + " and " + overlay.identifier()
                        + " (type " + overlay.type() + ", layer " + overlay.layer() + ")");
            }
        }
        return result;
    }

    private void expand(Overlay overlay, Map<String, Overlay> leaves, Set<String> visiting) {
        if (!visiting.add(overlay.identifier()))
            throw new IllegalArgumentException("Cyclic overlay combination: " + overlay.identifier());
        if (overlay.components().isEmpty()) leaves.putIfAbsent(overlay.identifier(), overlay);
        else for (String identifier : overlay.components()) expand(require(identifier), leaves, visiting);
        visiting.remove(overlay.identifier());
    }

    /**
     * Renders, saves and registers a combination under a caller-supplied key.
     * <p>
     * The model comes from the source overlays. Existing registrations and files are never
     * overwritten. Source overlays are expanded through {@link #flatten(Collection)}.
     * </p>
     *
     * @param key      unqualified key containing only letters, numbers, underscores or hyphens
     * @param overlays non-empty collection of source overlays or combinations
     * @return the saved combination instance registered under its model-qualified identifier
     * @throws IllegalArgumentException if the sources are incompatible, the key is invalid,
     *                                  or the destination identifier or file already exists
     * @throws IOException              if the combination cannot be encoded or saved atomically
     */
    public synchronized Overlay combine(String key, Collection<Overlay> overlays) throws IOException {
        return registerAndSave(compose(key, flatten(overlays)));
    }

    /**
     * Gets or creates a persistent combination using a hash of the rendered pixels, masks and component IDs.
     * <p>
     * Input order does not affect the identifier. An existing generated combination is reused.
     * A single leaf overlay is returned directly without copying, registering or saving it.
     * Source overlays are not saved by this method.
     * </p>
     *
     * @param overlays non-empty collection of source overlays or combinations
     * @return the sole source overlay or a registered, saved combination
     * @throws IllegalArgumentException if expansion fails or the sources are incompatible
     * @throws IOException              if the rendered combination cannot be encoded or saved atomically
     */
    public synchronized Overlay generate(Collection<Overlay> overlays) throws IOException {
        List<Overlay> leaves = flatten(overlays);
        if (leaves.size() == 1) return leaves.getFirst();
        Overlay composed = compose("generated", leaves);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(composed.getBufferedImage().getBufferedImage(), "png", bytes);
        bytes.write(composed.configurationFile().getFileConfiguration().saveToString().getBytes(StandardCharsets.UTF_8));
        String key = "generated_" + digest(bytes.toByteArray());
        Optional<Overlay> existing = get(composed.model().name().toLowerCase(Locale.ROOT) + "/" + key);
        if (existing.isPresent()) return existing.get();
        return registerAndSave(new Overlay(key, composed.model(), composed.configurationFile(), composed.getBufferedImage()));
    }

    private Overlay compose(String key, List<Overlay> leaves) {
        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            for (Overlay overlay : leaves) graphics.drawImage(OverlayRenderer.render(overlay), 0, 0, null);
        } finally {
            graphics.dispose();
        }
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("Options.components", leaves.stream().map(Overlay::identifier).toList());
        configuration.set("Options.placeholder", String.join(" + ", leaves.stream().map(Overlay::key).toList()));
        for (String part : PARTS) {
            configuration.set("Options.overlay." + part, true);
            configuration.set("Options.player." + part, leaves.stream().allMatch(overlay ->
                    overlay.configurationFile().getFileConfiguration().getBoolean("Options.player." + part, false)));
        }
        return new Overlay(key, leaves.getFirst().model(),
                new SkinConfigurationFile(directory.resolve("runtime.yml").toFile(), configuration),
                new SerializableBufferedImage(image));
    }

    /**
     * Saves a snapshot of a runtime overlay and publishes the snapshot in this registry.
     * <p>
     * Metadata and PNG pixels are written together to {@code <model>/<key>.yml} using an
     * atomic move. Existing identifiers and files are rejected. The supplied instance is
     * not modified, and later changes to it do not affect the registered snapshot.
     * Component references are retained; their source overlays are not saved implicitly.
     * </p>
     *
     * @param overlay overlay with initialized configuration and a 64x64 or legacy 64x32 image
     * @return the copied instance that was saved and registered
     * @throws IllegalArgumentException if the key or image is invalid, or the destination exists
     * @throws IOException              if the configuration or PNG cannot be encoded or the file cannot be saved atomically
     */
    public synchronized Overlay registerAndSave(Overlay overlay) throws IOException {
        validateIdentifier(overlay.identifier(), overlay);
        Path destination = directory.resolve(overlay.identifier() + ".yml");
        if (contains(overlay.identifier()) || Files.exists(destination)) {
            throw new IllegalArgumentException("Overlay already exists: " + overlay.identifier());
        }
        YamlConfiguration configuration = new YamlConfiguration();
        try {
            configuration.loadFromString(overlay.configurationFile().getFileConfiguration().saveToString());
        } catch (InvalidConfigurationException e) {
            throw new IOException("Invalid overlay configuration", e);
        }
        ByteArrayOutputStream png = new ByteArrayOutputStream();
        if (!ImageIO.write(overlay.getBufferedImage().getBufferedImage(), "png", png))
            throw new IOException("Cannot encode overlay PNG");
        configuration.set("Image", Base64.getEncoder().encodeToString(png.toByteArray()));
        Files.createDirectories(destination.getParent());
        Path temporary = Files.createTempFile(destination.getParent(), ".overlay-", ".tmp");
        try {
            Files.writeString(temporary, configuration.saveToString(), StandardCharsets.UTF_8);
            Files.move(temporary, destination, StandardCopyOption.ATOMIC_MOVE);
        } finally {
            Files.deleteIfExists(temporary);
        }
        configuration.set("Image", null);
        Overlay saved = new Overlay(overlay.key(), overlay.model(),
                new SkinConfigurationFile(destination.toFile(), configuration),
                new SerializableBufferedImage(ImageIO.read(new ByteArrayInputStream(png.toByteArray()))));
        register(saved);
        return saved;
    }

    /**
     * Loads saved runtime overlays into the current registry without clearing existing registrations.
     * <p>
     * A missing storage directory is ignored. Invalid files and duplicate identifiers are
     * reported through the callback, then loading continues with the remaining files.
     * Component references are resolved when combinations are expanded, after all files load.
     * </p>
     *
     * @param onFailure callback receiving each file that could not be loaded and its exception
     * @throws IOException if a model directory cannot be enumerated
     */
    public synchronized void loadSaved(BiConsumer<Path, Exception> onFailure) throws IOException {
        if (!Files.isDirectory(directory)) return;
        for (Model model : Model.values()) {
            Path folder = directory.resolve(model.name().toLowerCase(Locale.ROOT));
            if (!Files.isDirectory(folder)) continue;
            try (Stream<Path> files = Files.list(folder)) {
                for (Path file : files.filter(path -> path.getFileName().toString().endsWith(".yml")).sorted().toList()) {
                    try {
                        YamlConfiguration configuration = new YamlConfiguration();
                        configuration.load(file.toFile());
                        String name = file.getFileName().toString();
                        byte[] png = Base64.getDecoder().decode(Objects.requireNonNull(configuration.getString("Image")));
                        BufferedImage image = ImageIO.read(new ByteArrayInputStream(png));
                        if (image == null) throw new IOException("Invalid overlay PNG");
                        configuration.set("Image", null);
                        register(new Overlay(name.substring(0, name.length() - 4), model,
                                new SkinConfigurationFile(file.toFile(), configuration), new SerializableBufferedImage(image)));
                    } catch (Exception exception) {
                        onFailure.accept(file, exception);
                    }
                }
            }
        }
    }

    private static String digest(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
