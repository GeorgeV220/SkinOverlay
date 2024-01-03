package com.georgev22.skinoverlay.storage.gsonAdapters;

import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.storage.data.User;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * Gson TypeAdapter for serializing and deserializing {@link User} objects.
 * <p>
 * This adapter is responsible for converting {@link User} objects to and from JSON format.
 * It handles the serialization and deserialization of the user's ID, default skin, and current skin properties.
 */
public class UserTypeAdapter implements JsonSerializer<User>, JsonDeserializer<User> {

    /**
     * Serializes a {@link User} object to a JSON representation.
     *
     * @param user                     The {@link User} object to be serialized.
     * @param type                     The type of the source object.
     * @param jsonSerializationContext The serialization context.
     * @return A {@link JsonElement} representing the serialized {@link User} object.
     */
    @Override
    public JsonElement serialize(@NotNull User user, Type type, @NotNull JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("entity_id", user.getId().toString());
        jsonObject.add("defaultSkin", jsonSerializationContext.serialize(user.defaultSkin()));
        jsonObject.add("skin", jsonSerializationContext.serialize(user.skin()));

        return jsonObject;
    }

    /**
     * Deserializes a JSON representation into a {@link User} object.
     *
     * @param jsonElement                The {@link JsonElement} containing the JSON representation.
     * @param type                       The target type to deserialize into.
     * @param jsonDeserializationContext The deserialization context.
     * @return A deserialized {@link User} object.
     */
    @Override
    public User deserialize(@NotNull JsonElement jsonElement, Type type, @NotNull JsonDeserializationContext jsonDeserializationContext) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        UUID entityId = UUID.fromString(jsonObject.get("entity_id").getAsString());
        Skin defaultSkin = jsonDeserializationContext.deserialize(jsonObject.get("defaultSkin"), Skin.class);
        Skin skin = jsonDeserializationContext.deserialize(jsonObject.get("skin"), Skin.class);

        User user = new User(entityId);
        user.setDefaultSkin(defaultSkin);
        user.setSkin(skin);

        return user;
    }

    /**
     * Converts a {@link User} object to its JSON representation.
     *
     * @param user The {@link User} object to be converted.
     * @return A JSON string representing the serialized {@link User} object.
     * @throws IllegalArgumentException If the provided {@link User} object is null.
     */
    public static String toJson(@NotNull User user) throws IllegalArgumentException {
        //noinspection ConstantValue
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return user.toJson();
    }

    /**
     * Converts a JSON string to a {@link User} object.
     *
     * @param json The JSON string representing the serialized {@link User} object.
     * @return A {@link User} object deserialized from the provided JSON string.
     * @throws IllegalArgumentException If the provided JSON string is null or empty.
     */
    public static User fromJson(@NotNull String json) {
        //noinspection ConstantValue
        if (json == null) {
            throw new IllegalArgumentException("Json cannot be null");
        }
        if (json.isEmpty()) {
            throw new IllegalArgumentException("Json cannot be empty");
        }
        return User.fromJson(json);
    }
}
