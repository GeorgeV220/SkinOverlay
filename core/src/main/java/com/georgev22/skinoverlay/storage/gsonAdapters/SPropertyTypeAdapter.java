package com.georgev22.skinoverlay.storage.gsonAdapters;

import com.georgev22.skinoverlay.handler.SProperty;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/**
 * Gson TypeAdapter for serializing and deserializing {@link SProperty} objects.
 * <p>
 * This adapter is responsible for converting {@link SProperty} objects to and from JSON format.
 * It handles the serialization and deserialization of the property's name, value, and signature.
 */
public class SPropertyTypeAdapter implements
        JsonSerializer<SProperty>,
        JsonDeserializer<SProperty> {

    /**
     * Deserializes the JSON element to an {@code SProperty}.
     *
     * @param jsonElement                The JSON element containing name, value, and signature information.
     * @param typeOfT                    The type of the Object to deserialize to.
     * @param jsonDeserializationContext The context for deserialization.
     * @return The deserialized {@code SProperty}.
     */
    @Override
    public @NotNull SProperty deserialize(@NotNull JsonElement jsonElement, Type typeOfT, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        String value = jsonObject.get("value").getAsString();
        String signature = jsonObject.get("signature").getAsString();

        return new SProperty(name, value, signature);
    }

    /**
     * Serializes the {@code SProperty} to a JSON element.
     *
     * @param src                      The {@code SProperty} to serialize.
     * @param typeOfSrc                The actual type (fully genericized version) of the source object.
     * @param jsonSerializationContext The context for serialization.
     * @return The serialized JSON element.
     */
    @Override
    public @NotNull JsonElement serialize(@NotNull SProperty src, Type typeOfSrc, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", src.name());
        jsonObject.addProperty("value", src.value());
        jsonObject.addProperty("signature", src.signature());
        return jsonObject;
    }

    /**
     * Converts a JSON string to an {@code SProperty} object.
     *
     * @param json The JSON string representing the serialized {@link SProperty} object.
     * @return An {@link SProperty} object deserialized from the provided JSON string.
     * @throws IllegalArgumentException If the provided JSON string is null or empty.
     */
    public static SProperty fromJson(String json) {
        if (json == null) throw new IllegalArgumentException("Json cannot be null");
        if (json.isEmpty()) throw new IllegalArgumentException("Json cannot be empty");
        return SProperty.fromJson(json);
    }

    /**
     * Converts an {@code SProperty} object to its JSON representation.
     *
     * @param sProperty The {@code SProperty} object to be converted.
     * @return A JSON string representing the serialized {@code SProperty} object.
     * @throws IllegalArgumentException If the provided {@code SProperty} object is null.
     */
    public static String toJson(SProperty sProperty) {
        if (sProperty == null) throw new IllegalArgumentException("SProperty cannot be null");
        return sProperty.toJson();
    }
}
