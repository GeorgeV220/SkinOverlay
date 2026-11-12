package com.georgev22.skinoverlay.utilities;

import com.georgev22.skinoverlay.skin.SProperty;
import com.georgev22.skinoverlay.storage.data.PlayerData;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.storage.gson.*;
import com.georgev22.skinoverlay.storage.gson.PlayerDataTypeAdapter;
import com.georgev22.skinoverlay.utilities.skin.Part;
import com.georgev22.skinoverlay.utilities.skin.SkinParts;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jspecify.annotations.NonNull;

import java.io.Reader;
import java.io.Writer;

/**
 * Utility class for working with Gson, providing convenience methods for JSON serialization
 * and deserialization, as well as a Gson instance pre-configured with custom type adapters.
 *
 */
public class GsonUtils {

    /**
     * Default Gson instance without pretty printing.
     */
    private static final Gson defaultGson = builder().create();

    /**
     * Gson instance with pretty printing enabled.
     */
    private static final Gson prettyGson = builder().setPrettyPrinting().create();

    /**
     * Creates a new {@link GsonBuilder} with registered type adapters.
     *
     * @return a configured {@link GsonBuilder}
     */
    private static @NonNull GsonBuilder builder() {
        return new GsonBuilder()
                .registerTypeAdapter(SerializableBufferedImage.class, new SerializableBufferedImageTypeAdapter())
                .registerTypeAdapter(Part.class, new PartTypeAdapter())
                .registerTypeAdapter(SkinParts.class, new SkinPartsTypeAdapter())
                .registerTypeAdapter(SProperty.class, new SPropertyTypeAdapter())
                .registerTypeAdapter(Skin.class, new SkinTypeAdapter())
                .registerTypeAdapter(PlayerData.class, new PlayerDataTypeAdapter());
    }

    /**
     * Converts an object to its JSON representation.
     *
     * @param obj    the object to serialize
     * @param pretty whether to pretty print the JSON output
     * @return JSON string representing the object
     */
    public static @NonNull String toJson(Object obj, boolean pretty) {
        if (pretty) {
            return prettyGson.toJson(obj);
        } else {
            return defaultGson.toJson(obj);
        }
    }

    /**
     * Converts an arbitrary object to a {@link JsonObject} using Gson.
     * <p>
     * This method serializes the given object into a Gson {@link JsonElement} tree
     * and returns it as a {@link JsonObject}. If the object does not naturally
     * serialize to a JSON object (for example, if it is a primitive, array, or null),
     * a {@link IllegalArgumentException} is thrown.
     * </p>
     *
     * @param obj the object to convert to a {@link JsonObject}; must not be null
     * @return a {@link JsonObject} representation of the object
     * @throws NullPointerException     if {@code obj} is null
     * @throws IllegalArgumentException if the object cannot be represented as a {@link JsonObject}
     * @see com.google.gson.Gson#toJsonTree(Object)
     * @see com.google.gson.JsonElement#getAsJsonObject()
     */
    public static @NonNull JsonObject toJsonObject(Object obj) {
        JsonElement element = defaultGson.toJsonTree(obj);

        if (element.isJsonObject()) {
            return element.getAsJsonObject();
        } else {
            throw new IllegalArgumentException(
                    "Cannot convert object of type " + obj.getClass().getName() + " to JsonObject"
            );
        }
    }

    /**
     * Writes an object to a writer in JSON format.
     *
     * @param obj    the object to serialize
     * @param pretty whether to pretty print the JSON output
     * @param writer the writer to write the JSON to
     */
    public static void write(Object obj, boolean pretty, Writer writer) {
        if (pretty) {
            prettyGson.toJson(obj, writer);
        } else {
            defaultGson.toJson(obj, writer);
        }
    }

    /**
     * Deserializes a JSON string into an object of the specified class.
     *
     * @param <T>   the type of the desired object
     * @param json  the JSON string to deserialize
     * @param clazz the class of T
     * @return the deserialized object
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return defaultGson.fromJson(json, clazz);
    }

    /**
     * Deserializes a JSON from a reader into an object of the specified class.
     *
     * @param <T>    the type of the desired object
     * @param reader the reader to read the JSON from
     * @param clazz  the class of T
     * @return the deserialized object
     */
    public static <T> T fromJson(Reader reader, Class<T> clazz) {
        return defaultGson.fromJson(reader, clazz);
    }

    /**
     * Converts an object into a {@link JsonElement} representation.
     *
     * @param src the object to convert
     * @return a {@link JsonElement} representing the object
     */
    public static JsonElement toJsonTree(Object src) {
        return defaultGson.toJsonTree(src);
    }

    /**
     * Returns a Gson instance.
     *
     * @param pretty whether to return a pretty-printing Gson instance
     * @return the configured {@link Gson} instance
     */
    public static Gson getGson(boolean pretty) {
        return pretty ? prettyGson : defaultGson;
    }
}
