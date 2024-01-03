package com.georgev22.skinoverlay.utilities;

import com.georgev22.library.yaml.serialization.ConfigurationSerializable;

import com.georgev22.skinoverlay.SkinOverlay;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.awt.image.BufferedImage;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a {@link BufferedImage} that can be serialized and deserialized.
 */
@SuppressWarnings("ClassCanBeRecord")
public final class SerializableBufferedImage implements ConfigurationSerializable {
    private final BufferedImage bufferedImage;

    public SerializableBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    /**
     * Serializes the {@link BufferedImage} to a YAML-compatible map for serialization.
     *
     * @return A YAML-compatible map containing width, height, and pixel information.
     */
    @Override
    public @NotNull @Unmodifiable Map<String, Object> serialize() {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[] pixels = new int[width * height];
        bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);
        String encodedPixels = Base64.getEncoder().encodeToString(toByteArray(pixels));
        return Map.of(
                "width", width,
                "height", height,
                "pixels", encodedPixels
        );
    }

    /**
     * Deserializes the YAML-compatible map to a {@link SerializableBufferedImage}.
     *
     * @param map The YAML-compatible map containing width, height, and pixel information.
     * @return The deserialized {@link SerializableBufferedImage}.
     */
    @Contract(pure = true)
    public static @NotNull SerializableBufferedImage deserialize(@NotNull Map<String, Object> map) {
        int width = (int) map.get("width");
        int height = (int) map.get("height");
        String encodedPixels = (String) map.get("pixels");
        int[] pixels = fromByteArray(Base64.getDecoder().decode(encodedPixels));

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);

        return new SerializableBufferedImage(bufferedImage);
    }

    @Contract(pure = true)
    public static byte @NotNull [] toByteArray(int @NotNull [] pixels) {
        byte[] result = new byte[pixels.length * 4];
        for (int i = 0, j = 0; i < pixels.length; i++, j += 4) {
            result[j] = (byte) ((pixels[i] >> 24) & 0xFF);
            result[j + 1] = (byte) ((pixels[i] >> 16) & 0xFF);
            result[j + 2] = (byte) ((pixels[i] >> 8) & 0xFF);
            result[j + 3] = (byte) (pixels[i] & 0xFF);
        }
        return result;
    }

    @Contract(pure = true)
    public static int @NotNull [] fromByteArray(byte @NotNull [] bytes) {
        int[] result = new int[bytes.length / 4];
        for (int i = 0, j = 0; i < result.length; i++, j += 4) {
            result[i] = ((bytes[j] & 0xFF) << 24) |
                    ((bytes[j + 1] & 0xFF) << 16) |
                    ((bytes[j + 2] & 0xFF) << 8) |
                    (bytes[j + 3] & 0xFF);
        }
        return result;
    }

    /**
     * Checks if this {@link SerializableBufferedImage} is equal to another object.
     *
     * @param obj The object to compare.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SerializableBufferedImage) obj;
        return Objects.equals(this.bufferedImage, that.bufferedImage);
    }

    /**
     * Computes the hash code of this {@link SerializableBufferedImage}.
     *
     * @return The computed hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(bufferedImage);
    }

    /**
     * Returns a string representation of this {@link SerializableBufferedImage}.
     *
     * @return A string representation containing the buffered image.
     */
    @Override
    public @NotNull String toString() {
        return "SerializableBufferedImage[" +
                "bufferedImage=" + bufferedImage + ']';
    }

    /**
     * Serializes the SerializableBufferedImage object to a JSON string.
     *
     * @return The serialized JSON string.
     */
    public String toJson() {
        return SkinOverlay.getInstance().getGson().toJson(this);
    }

    /**
     * Deserializes a JSON string to a SerializableBufferedImage object.
     *
     * @param json The JSON string to deserialize.
     * @return The deserialized SerializableBufferedImage object.
     */
    public static SerializableBufferedImage fromJson(String json) {
        return SkinOverlay.getInstance().getGson().fromJson(json, SerializableBufferedImage.class);
    }

    /**
     * Returns the buffered image.
     *
     * @return The buffered image
     */
    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }


}
