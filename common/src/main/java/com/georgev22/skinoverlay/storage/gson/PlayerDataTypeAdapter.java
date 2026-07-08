package com.georgev22.skinoverlay.storage.gson;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.storage.data.PlayerData;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * Gson TypeAdapter for serializing and deserializing {@link PlayerData} objects.
 * <p>
 * This adapter is responsible for converting {@link PlayerData} objects to and from JSON format.
 * It handles the serialization and deserialization of the user's ID, default skin, and current skin properties.
 */
public class PlayerDataTypeAdapter implements JsonSerializer<PlayerData>, JsonDeserializer<PlayerData> {

    /**
     * Serializes a {@link PlayerData} object to a JSON representation.
     *
     * @param user                     The {@link PlayerData} object to be serialized.
     * @param type                     The type of the source object.
     * @param jsonSerializationContext The serialization context.
     * @return A {@link JsonElement} representing the serialized {@link PlayerData} object.
     */
    @Override
    public JsonElement serialize(@NotNull PlayerData user, Type type, @NotNull JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("entity_id", user.getUniqueId().toString());
        jsonObject.add("defaultSkin", jsonSerializationContext.serialize(user.getDefaultSkin()));
        jsonObject.add("skin", jsonSerializationContext.serialize(user.getCurrentSkin()));

        return jsonObject;
    }

    /**
     * Deserializes a JSON representation into a {@link PlayerData} object.
     *
     * @param jsonElement                The {@link JsonElement} containing the JSON representation.
     * @param type                       The target type to deserialize into.
     * @param jsonDeserializationContext The deserialization context.
     * @return A deserialized {@link PlayerData} object.
     */
    @Override
    public PlayerData deserialize(@NotNull JsonElement jsonElement, Type type, @NotNull JsonDeserializationContext jsonDeserializationContext) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        UUID entityId = UUID.fromString(jsonObject.get("entity_id").getAsString());
        Skin defaultSkin = jsonDeserializationContext.deserialize(jsonObject.get("defaultSkin"), Skin.class);
        Skin skin = jsonDeserializationContext.deserialize(jsonObject.get("skin"), Skin.class);

        PlayerData user = new PlayerData(entityId);
        user.setDefaultSkin(defaultSkin);
        user.setCurrentSkin(skin);

        return user;
    }

    /**
     * Converts a {@link PlayerData} object to its JSON representation.
     *
     * @param user The {@link PlayerData} object to be converted.
     * @return A JSON string representing the serialized {@link PlayerData} object.
     * @throws IllegalArgumentException If the provided {@link PlayerData} object is null.
     */
    public static String toJson(@NotNull PlayerData user) throws IllegalArgumentException {
        //noinspection ConstantValue
        if (user == null) {
            throw new IllegalArgumentException("PlayerData cannot be null");
        }
        return SkinOverlay.getInstance().getGson().toJson(user);
    }

    /**
     * Converts a JSON string to a {@link PlayerData} object.
     *
     * @param json The JSON string representing the serialized {@link PlayerData} object.
     * @return A {@link PlayerData} object deserialized from the provided JSON string.
     * @throws IllegalArgumentException If the provided JSON string is null or empty.
     */
    public static PlayerData fromJson(@NotNull String entityId, @NotNull String json) {
        //noinspection ConstantValue
        if (json == null) {
            throw new IllegalArgumentException("Json cannot be null (entityId: " + entityId + ")");
        }
        if (json.isEmpty()) {
            throw new IllegalArgumentException("Json cannot be empty (entityId: " + entityId + ")");
        }
        return SkinOverlay.getInstance().getGson().fromJson(json, PlayerData.class);
    }
}