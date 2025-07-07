package com.georgev22.skinoverlay.skin;

import com.georgev22.skinoverlay.maps.HashObjectMap;
import com.georgev22.skinoverlay.maps.ObjectMap;

import java.util.UUID;

public class SGameProfile {

    private final String name;
    private final UUID uuid;
    private final ObjectMap<String, SProperty> properties = new HashObjectMap<>();

    public SGameProfile(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public SGameProfile(String name, UUID uuid, ObjectMap<String, SProperty> properties) {
        this.name = name;
        this.uuid = uuid;
        this.properties.putAll(properties);
    }

    public String getName() {
        return name;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public ObjectMap<String, SProperty> getProperties() {
        return properties;
    }

    public SProperty getProperty(String property) {
        return this.properties.get(property);
    }

    public SGameProfile addProperty(String property, SProperty value) {
        this.properties.append(property, value);
        return this;
    }

    public SGameProfile removeProperty(String property) {
        this.properties.remove(property);
        return this;
    }

    public SGameProfile setProperty(String property, SProperty value) {
        this.properties.append(property, value);
        return this;
    }
}
