package com.georgev22.skinoverlay.datastructures.maps.utilities;

import com.georgev22.skinoverlay.datastructures.maps.ObjectMap;
import com.google.gson.*;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Type;

public class ObjectMapSerializerDeserializer implements JsonSerializer<ObjectMap<?, ?>>, JsonDeserializer<ObjectMap<?, ?>> {

    private static final String CLASSNAME = "CLASSNAME";
    private static final String INSTANCE = "INSTANCE";

    @Override
    public JsonElement serialize(@NonNull ObjectMap<?, ?> src, Type typeOfSrc, @NonNull JsonSerializationContext context) {
        JsonObject retValue = new JsonObject();
        String className = src.getClass().getName();
        retValue.addProperty(CLASSNAME, className);
        JsonElement elem = context.serialize(src);
        retValue.add(INSTANCE, elem);
        return retValue;
    }

    @Override
    public ObjectMap<?, ?> deserialize(@NonNull JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
        String className = prim.getAsString();

        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e.getMessage());
        }
        return context.deserialize(jsonObject.get(INSTANCE), clazz);
    }
}
