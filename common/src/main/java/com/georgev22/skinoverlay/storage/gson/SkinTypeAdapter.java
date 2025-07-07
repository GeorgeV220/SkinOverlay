package com.georgev22.skinoverlay.storage.gson;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.skin.SProperty;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.skin.SkinParts;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.UUID;

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
        jsonObject.add("property", jsonSerializationContext.serialize(skin.getProperty()));
        jsonObject.add("skinParts", jsonSerializationContext.serialize(skin.getSkinParts()));

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

        Skin skin = new Skin(entityId);
        skin.setProperty(property);
        skin.setSkinParts(skinParts);

        return skin;
    }

    /**
     * Converts a JSON string to a {@link Skin} object.
     *
     * @param json The JSON string representing the serialized {@link Skin} object.
     * @return A {@link Skin} object deserialized from the provided JSON string.
     * @throws IllegalArgumentException If the provided JSON string is null or empty.
     */
    public static Skin fromJson(String entityId, String json) {
        if (json == null) throw new IllegalArgumentException("Json cannot be null (entityId: " + entityId + ")");
        if (json.isEmpty()) throw new IllegalArgumentException("Json cannot be empty (entityId: " + entityId + ")");
        return SkinOverlay.getInstance().getGson().fromJson(json, Skin.class);
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
        return SkinOverlay.getInstance().getGson().toJson(skin);
    }
}