package com.georgev22.skinoverlay.utilities.skin;

import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import com.georgev22.skinoverlay.utilities.config.OverlayOptionsUtil;
import com.georgev22.skinoverlay.utilities.config.SkinConfigurationFile;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * An overlay's model, image and YAML options, identified by an unqualified key.
 * <p>
 * Layer, type and component metadata are read from the current configuration. Instances are mutable;
 * use the registry's save operation to persist a snapshot of an overlay created at runtime.
 * </p>
 */
public final class Overlay {
    private final String key;
    private SkinConfigurationFile configurationFile;
    private final Model model;
    private SerializableBufferedImage bufferedImage;
    private SkinParts skinParts;

    /**
     * Creates an overlay without rendering its image, registering it or writing files.
     *
     * @param key               unqualified overlay key, shared by its classic and slim variants
     * @param model             player model represented by the image
     * @param configurationFile initialized configuration containing overlay options
     * @param bufferedImage     overlay artwork, with transparency outside the intended clothing
     */
    public Overlay(String key, Model model, SkinConfigurationFile configurationFile, SerializableBufferedImage bufferedImage) {
        this.key = key;
        this.model = model;
        this.configurationFile = configurationFile;
        this.bufferedImage = bufferedImage;
    }

    /**
     * Returns the overlay's key without a model prefix.
     *
     * @return the unqualified key
     */
    public String key() {
        return key;
    }

    /**
     * Returns the identifier used by the overlay registry and persisted loadouts.
     *
     * @return the lowercase model name followed by the key, for example {@code classic/hat}
     */
    public String identifier() {
        return model.name().toLowerCase(Locale.ROOT) + "/" + key;
    }

    /**
     * Reads the arbitrary, case-sensitive conflict category from {@code Options.type}.
     *
     * @return the trimmed configured type, or the overlay key if the type is missing or blank
     */
    public String type() {
        String type = OverlayOptionsUtil.TYPE.getStringValue(configurationFile.getFileConfiguration()).trim();
        return type.isEmpty() ? key : type;
    }

    /**
     * Reads the texture drawing order from {@code Options.layer}.
     * <p>
     * Higher values are drawn over lower values. Layers affect texture composition only;
     * they do not add geometry to Minecraft's player model.
     * </p>
     *
     * @return the configured integer layer, defaulting to {@code 0}
     */
    public int layer() {
        return OverlayOptionsUtil.LAYER.getIntValue(configurationFile.getFileConfiguration());
    }

    /**
     * Reads source identifiers retained by combinations for expansion and selective removal.
     * <p>
     * Generated combinations store flattened leaf identifiers. Programmatically supplied
     * configurations may refer to other combinations, which the registry expands recursively.
     * </p>
     *
     * @return an immutable snapshot of {@code Options.components}, or an empty list for a leaf overlay
     */
    public List<String> components() {
        return List.copyOf(configurationFile.getFileConfiguration().getStringList("Options.components"));
    }

    /**
     * Returns the configuration wrapper currently used to read overlay options.
     *
     * @return the overlay's configuration wrapper
     */
    public SkinConfigurationFile configurationFile() {
        return configurationFile;
    }

    /**
     * Returns the player model represented by this overlay.
     *
     * @return the classic or slim model
     */
    public Model model() {
        return model;
    }

    /**
     * Returns the raw image before part filtering or layer composition.
     *
     * @return the current image wrapper; its underlying image is mutable
     */
    public SerializableBufferedImage getBufferedImage() {
        return bufferedImage;
    }

    /**
     * Replaces the configuration used by subsequent option reads without saving it.
     *
     * @param configurationFile initialized configuration wrapper to use
     */
    public void setConfigurationFile(SkinConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    /**
     * Replaces the raw image and invalidates the lazily cached skin parts without saving it.
     *
     * @param bufferedImage replacement artwork for this overlay's player model
     */
    public void setBufferedImage(SerializableBufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        this.skinParts = null;
    }

    /**
     * Returns a lazily created skin wrapper for the current raw image.
     * <p>
     * Individual parts are not created by this method. Call {@link SkinParts#createParts(Model)}
     * with this overlay's model when part extraction is needed.
     * </p>
     *
     * @return the cached skin wrapper, recreated after {@link #setBufferedImage(SerializableBufferedImage)}
     * @throws IllegalArgumentException if the image has not been supplied
     */
    public SkinParts getSkinParts() {
        if (skinParts == null) {
            if (bufferedImage == null) throw new IllegalArgumentException("BufferedImage cannot be null");
            skinParts = new SkinParts(bufferedImage, key);
        }

        return skinParts;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Overlay) obj;
        return Objects.equals(this.key, that.key) &&
                Objects.equals(this.configurationFile, that.configurationFile) &&
                Objects.equals(this.model, that.model) &&
                Objects.equals(this.bufferedImage, that.bufferedImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, configurationFile, model, bufferedImage);
    }

    @Override
    public String toString() {
        return "Overlay[" +
                "name=" + key + ", " +
                "configurationFile=" + configurationFile + ", " +
                "model=" + model + ", " +
                "bufferedImage=" + bufferedImage + ']';
    }
}
