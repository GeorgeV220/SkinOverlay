package com.georgev22.skinoverlay.utilities.gson;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.handler.SProperty;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Map;

/**
 * A Gson TypeAdapter for serializing and deserializing an {@link ObjectMap} with
 * support for custom {@link SProperty} objects.
 */
public class ObjectMapSPropertyTypeAdapter extends TypeAdapter<ObjectMap<String, Object>> {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    /**
     * Writes the specified {@link ObjectMap} to the specified {@link JsonWriter}.
     *
     * @param out the {@link JsonWriter} to write to
     * @param map the {@link ObjectMap} to write
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void write(JsonWriter out, ObjectMap<String, Object> map) throws IOException {
        out.beginObject();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof SProperty) {
                out.name("sProperty_" + key);
                skinOverlay.getUserManager().getGson().toJson(value, SProperty.class, out);
            } else {
                out.name(key);
                skinOverlay.getUserManager().getGson().toJson(value, Object.class, out);
            }
        }
        out.endObject();
    }

    /**
     * Reads an {@link ObjectMap} from the specified {@link JsonReader}.
     *
     * @param in the {@link JsonReader} to read from
     * @return the deserialized {@link ObjectMap}
     * @throws IOException if an I/O error occurs
     */
    @Override
    public ObjectMap<String, Object> read(JsonReader in) throws IOException {
        ObjectMap<String, Object> map = new HashObjectMap<>();
        in.beginObject();
        while (in.hasNext()) {
            String key = in.nextName();
            if (key.startsWith("sProperty")) {
                String sPropertyKey = key.substring("sProperty".length() + 1);
                map.put(sPropertyKey, skinOverlay.getUserManager().getGson().fromJson(in, SProperty.class));
            } else {
                Object value = skinOverlay.getUserManager().getGson().fromJson(in, Object.class);
                map.put(key, value);
            }
        }
        in.endObject();
        return map;
    }
}
