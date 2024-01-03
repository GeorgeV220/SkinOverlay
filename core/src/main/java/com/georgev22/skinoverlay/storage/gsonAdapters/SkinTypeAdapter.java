package com.georgev22.skinoverlay.storage.gsonAdapters;

import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.handler.skin.SkinParts;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * Gson TypeAdapter for serializing and deserializing {@link Skin} objects.
 * <p>
 * This adapter is responsible for converting {@link Skin} objects to and from JSON format.
 * It handles the serialization and deserialization of the skin's ID, property, skin parts, and skin name properties.
 */
public class SkinTypeAdapter implements JsonSerializer<Skin>, JsonDeserializer<Skin> {

    /**
     * Serializes a {@link Skin} object to a JSON representation.
     *
     * @param skin                     The {@link Skin} object to be serialized.
     * @param type                     The type of the source object.
     * @param jsonSerializationContext The serialization context.
     * @return A {@link JsonElement} representing the serialized {@link Skin} object.
     */
    @Override
    public JsonElement serialize(@NotNull Skin skin, Type type, @NotNull JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("entity_id", skin.getId().toString());
        jsonObject.add("property", jsonSerializationContext.serialize(skin.skinProperty()));
        jsonObject.add("skinParts", jsonSerializationContext.serialize(skin.skinParts()));
        jsonObject.addProperty("skinName", skin.skinName());

        return jsonObject;
    }

    /**
     * Deserializes a JSON representation into a {@link Skin} object.
     *
     * @param jsonElement                The {@link JsonElement} containing the JSON representation.
     * @param type                       The target type to deserialize into.
     * @param jsonDeserializationContext The deserialization context.
     * @return A deserialized {@link Skin} object.
     */
    @Override
    public Skin deserialize(@NotNull JsonElement jsonElement, Type type, @NotNull JsonDeserializationContext jsonDeserializationContext) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        UUID entityId = UUID.fromString(jsonObject.get("entity_id").getAsString());
        SProperty property = jsonDeserializationContext.deserialize(jsonObject.get("property"), SProperty.class);
        SkinParts skinParts = jsonDeserializationContext.deserialize(jsonObject.get("skinParts"), SkinParts.class);
        String skinName = jsonObject.get("skinName").getAsString();

        Skin skin = new Skin(entityId, property, skinParts);
        skin.setSkinName(skinName);

        return skin;
    }

    /**
     * Converts a JSON string to a {@link Skin} object.
     *
     * @param json The JSON string representing the serialized {@link Skin} object.
     * @return A {@link Skin} object deserialized from the provided JSON string.
     * @throws IllegalArgumentException If the provided JSON string is null or empty.
     */
    public static Skin fromJson(String json) {
        if (json == null) throw new IllegalArgumentException("Json cannot be null");
        if (json.isEmpty()) throw new IllegalArgumentException("Json cannot be empty");
        return Skin.fromJson(json);
    }

    /**
     * Converts a {@link Skin} object to its JSON representation.
     *
     * @param skin The {@link Skin} object to be converted.
     * @return A JSON string representing the serialized {@link Skin} object.
     * @throws IllegalArgumentException If the provided {@link Skin} object is null.
     */
    public static String toJson(Skin skin) {
        if (skin == null) throw new IllegalArgumentException("Skin cannot be null");
        return skin.toJson();
    }
}
