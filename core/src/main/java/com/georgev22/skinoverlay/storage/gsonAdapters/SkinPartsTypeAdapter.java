package com.georgev22.skinoverlay.storage.gsonAdapters;

import com.georgev22.skinoverlay.handler.skin.SkinParts;
import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/**
 * Gson TypeAdapter for serializing and deserializing {@link SkinParts} objects.
 * <p>
 * This adapter is responsible for converting {@link SkinParts} objects to and from JSON format.
 * It handles the serialization of the buffered image and the skin name properties.
 */
public class SkinPartsTypeAdapter implements JsonSerializer<SkinParts>, JsonDeserializer<SkinParts> {

    /**
     * Serializes a {@link SkinParts} object to a JSON representation.
     *
     * @param src       The {@link SkinParts} object to be serialized.
     * @param typeOfSrc The type of the source object.
     * @param context   The serialization context.
     * @return A {@link JsonElement} representing the serialized {@link SkinParts} object.
     */
    @Override
    public JsonElement serialize(@NotNull SkinParts src, Type typeOfSrc, @NotNull JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("bufferedImage", context.serialize(src.getFullSkin()));
        jsonObject.addProperty("skinName", src.getSkinName());
        return jsonObject;
    }

    /**
     * Deserializes a JSON representation into a {@link SkinParts} object.
     *
     * @param json    The {@link JsonElement} containing the JSON representation.
     * @param typeOfT The target type to deserialize into.
     * @param context The deserialization context.
     * @return A deserialized {@link SkinParts} object.
     */
    @Override
    public SkinParts deserialize(@NotNull JsonElement json, Type typeOfT, @NotNull JsonDeserializationContext context) {
        JsonObject jsonObject = json.getAsJsonObject();

        SerializableBufferedImage bufferedImage = context.deserialize(jsonObject.get("bufferedImage"), SerializableBufferedImage.class);
        String skinName = jsonObject.get("skinName").getAsString();

        return new SkinParts(bufferedImage, skinName);
    }

    /**
     * Converts a {@link SkinParts} object to its JSON representation.
     *
     * @param skinParts The {@link SkinParts} object to be converted.
     * @return A JSON string representing the serialized {@link SkinParts} object.
     * @throws IllegalArgumentException If the provided {@link SkinParts} object is null.
     */
    public static String toJson(@NotNull SkinParts skinParts) throws IllegalArgumentException {
        //noinspection ConstantValue
        if (skinParts == null) throw new IllegalArgumentException("SkinParts cannot be null");
        return skinParts.toJson();
    }

    /**
     * Converts a JSON string to a {@link SkinParts} object.
     *
     * @param json The JSON string representing the serialized {@link SkinParts} object.
     * @return A {@link SkinParts} object deserialized from the provided JSON string.
     * @throws IllegalArgumentException If the provided JSON string is null or empty.
     */
    public static SkinParts fromJson(@NotNull String json) {
        //noinspection ConstantValue
        if (json == null) throw new IllegalArgumentException("Json cannot be null");
        if (json.isEmpty()) throw new IllegalArgumentException("Json cannot be empty");
        return SkinParts.fromJson(json);
    }
}
