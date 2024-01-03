package com.georgev22.skinoverlay.storage.gsonAdapters;

import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.lang.reflect.Type;
import java.util.Base64;

/**
 * Gson TypeAdapter for serializing and deserializing {@link SerializableBufferedImage} objects.
 * <p>
 * This adapter is responsible for converting {@link SerializableBufferedImage} objects to and from JSON format.
 * It handles the serialization and deserialization of the image's width, height, and pixel information.
 */
public class SerializableBufferedImageTypeAdapter implements
        JsonSerializer<SerializableBufferedImage>,
        JsonDeserializer<SerializableBufferedImage> {

    /**
     * Deserializes the JSON element to a {@link SerializableBufferedImage}.
     *
     * @param jsonElement                The JSON element containing width, height, and pixel information.
     * @param type                       The type to deserialize.
     * @param jsonDeserializationContext The context for deserialization.
     * @return The deserialized {@link SerializableBufferedImage}.
     */
    @Override
    public @NotNull SerializableBufferedImage deserialize(@NotNull JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        int width = jsonObject.get("width").getAsInt();
        int height = jsonObject.get("height").getAsInt();
        String encodedPixels = jsonObject.get("pixels").getAsString();
        int[] pixels = SerializableBufferedImage.fromByteArray(Base64.getDecoder().decode(encodedPixels));

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);

        return new SerializableBufferedImage(bufferedImage);
    }

    /**
     * Serializes the {@link SerializableBufferedImage} to a JSON element.
     *
     * @param serializableBufferedImage The {@link SerializableBufferedImage} to serialize.
     * @param type                      The type to serialize.
     * @param jsonSerializationContext  The context for serialization.
     * @return The serialized JSON element.
     */
    @Override
    public JsonElement serialize(@NotNull SerializableBufferedImage serializableBufferedImage, Type type, @NotNull JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(serializableBufferedImage.serialize());
    }

    /**
     * Converts a {@link SerializableBufferedImage} object to its JSON representation.
     *
     * @param serializableBufferedImage The {@link SerializableBufferedImage} object to be converted.
     * @return A JSON string representing the serialized {@link SerializableBufferedImage} object.
     * @throws IllegalArgumentException If the provided {@link SerializableBufferedImage} object is null.
     */
    public static String toJson(SerializableBufferedImage serializableBufferedImage) {
        if (serializableBufferedImage == null)
            throw new IllegalArgumentException("SerializableBufferedImage cannot be null");
        return serializableBufferedImage.toJson();
    }

    /**
     * Converts a JSON string to a {@link SerializableBufferedImage} object.
     *
     * @param json The JSON string representing the serialized {@link SerializableBufferedImage} object.
     * @return A {@link SerializableBufferedImage} object deserialized from the provided JSON string.
     * @throws IllegalArgumentException If the provided JSON string is null or empty.
     */
    public static SerializableBufferedImage fromJson(String json) {
        if (json == null) throw new IllegalArgumentException("Json cannot be null");
        if (json.isEmpty()) throw new IllegalArgumentException("Json cannot be empty");
        return SerializableBufferedImage.fromJson(json);
    }
}
