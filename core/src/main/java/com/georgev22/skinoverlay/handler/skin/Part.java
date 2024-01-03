package com.georgev22.skinoverlay.handler.skin;

import com.georgev22.library.yaml.serialization.ConfigurationSerializable;
import com.georgev22.library.yaml.serialization.SerializableAs;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a part of a player's skin.
 */
@SerializableAs("SkinPart")
public record Part(String name, SerializableBufferedImage image, int x, int y, int width, int height,
                   boolean isEmpty) implements ConfigurationSerializable {

    /**
     * Returns a string representation of the Part object.
     *
     * @return A string representation of the Part object.
     */
    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "Part{" +
                "name='" + name + '\'' +
                ", image=" + image +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", isEmpty=" + isEmpty +
                '}';
    }

    /**
     * Serializes the Part object to a map for YAML serialization.
     *
     * @return A map containing the serialized data.
     */
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("bufferedImage", image);
        map.put("x", x);
        map.put("y", y);
        map.put("width", width);
        map.put("height", height);
        map.put("isEmpty", isEmpty);
        return map;
    }

    /**
     * Deserializes the YAML-compatible map to a Part object.
     *
     * @param map The YAML-compatible map containing part information.
     * @return The deserialized Part object.
     */
    @Contract("_ -> new")
    public static @NotNull Part deserialize(@NotNull Map<String, Object> map) {
        return new Part(
                (String) map.get("name"),
                (SerializableBufferedImage) map.get("bufferedImage"),
                (int) map.get("x"),
                (int) map.get("y"),
                (int) map.get("width"),
                (int) map.get("height"),
                (boolean) map.get("isEmpty")
        );
    }

    /**
     * Serializes the Part object to a JSON string.
     *
     * @return The serialized JSON string.
     */
    public String toJson() {
        return SkinOverlay.getInstance().getGson().toJson(this);
    }

    /**
     * Deserializes a JSON string to a Part object.
     *
     * @param json The JSON string to deserialize.
     * @return The deserialized Part object.
     */
    public static Part fromJson(String json) {
        return SkinOverlay.getInstance().getGson().fromJson(json, Part.class);
    }
}