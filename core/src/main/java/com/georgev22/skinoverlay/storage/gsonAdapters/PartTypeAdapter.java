package com.georgev22.skinoverlay.storage.gsonAdapters;

import com.georgev22.skinoverlay.handler.skin.Part;
import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/**
 * Gson TypeAdapter for serializing and deserializing {@link Part} objects.
 * <p>
 * This adapter is responsible for converting {@link Part} objects to and from JSON format.
 * It handles the serialization and deserialization of the part's name, image, position, dimensions, and emptiness status.
 */
public class PartTypeAdapter implements JsonSerializer<Part>, JsonDeserializer<Part> {

    /**
     * Deserializes the JSON element to a Part object.
     *
     * @param jsonElement                The JSON element containing part information.
     * @param typeOfT                    The type of the Object to deserialize to.
     * @param jsonDeserializationContext The context for deserialization.
     * @return The deserialized Part object.
     * @throws JsonParseException If there is an issue during the deserialization process.
     */
    @Override
    public Part deserialize(@NotNull JsonElement jsonElement, Type typeOfT, @NotNull JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        SerializableBufferedImage image = jsonDeserializationContext.deserialize(jsonObject.get("bufferedImage"), SerializableBufferedImage.class);
        int x = jsonObject.get("x").getAsInt();
        int y = jsonObject.get("y").getAsInt();
        int width = jsonObject.get("width").getAsInt();
        int height = jsonObject.get("height").getAsInt();
        boolean isEmpty = jsonObject.get("isEmpty").getAsBoolean();

        return new Part(name, image, x, y, width, height, isEmpty);
    }

    /**
     * Serializes the Part object to a JSON element.
     *
     * @param src                      The Part object to serialize.
     * @param typeOfSrc                The actual type (fully genericized version) of the source object.
     * @param jsonSerializationContext The context for serialization.
     * @return The serialized JSON element.
     */
    @Override
    public JsonElement serialize(@NotNull Part src, Type typeOfSrc, @NotNull JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", src.name());
        jsonObject.add("bufferedImage", jsonSerializationContext.serialize(src.image()));
        jsonObject.addProperty("x", src.x());
        jsonObject.addProperty("y", src.y());
        jsonObject.addProperty("width", src.width());
        jsonObject.addProperty("height", src.height());
        jsonObject.addProperty("isEmpty", src.isEmpty());
        return jsonObject;
    }

    /**
     * Converts a {@link Part} object to its JSON representation.
     *
     * @param part The {@link Part} object to be converted.
     * @return A JSON string representing the serialized {@link Part} object.
     * @throws IllegalArgumentException If the provided {@link Part} object is null.
     */
    public static String toJson(Part part) {
        if (part == null) throw new IllegalArgumentException("Part cannot be null");
        return part.toJson();
    }

    /**
     * Converts a JSON string to a {@link Part} object.
     *
     * @param json The JSON string representing the serialized {@link Part} object.
     * @return A {@link Part} object deserialized from the provided JSON string.
     * @throws IllegalArgumentException If the provided JSON string is null or empty.
     */
    public static Part fromJson(String json) {
        if (json == null) throw new IllegalArgumentException("Json cannot be null");
        if (json.isEmpty()) throw new IllegalArgumentException("Json cannot be empty");
        return Part.fromJson(json);
    }
}
