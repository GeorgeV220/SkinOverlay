package com.georgev22.skinoverlay.storage.data;

import com.georgev22.library.maps.ConcurrentObjectMap;
import com.georgev22.library.utilities.Entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class User implements Entity, Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    private final ConcurrentObjectMap<String, Object> customData;

    public User(UUID uuid) {
        this.customData = new ConcurrentObjectMap<>();
        addCustomData("entity_id", uuid.toString());
    }

    public Skin defaultSkin() {
        return getCustomData("defaultSkin");
    }

    public Skin skin() {
        return getCustomData("skin");
    }

    @Override
    public UUID getId() {
        return this.getCustomData("entity_id") != null ? UUID.fromString(this.getCustomData("entity_id")) : null;
    }

    @Override
    public ConcurrentObjectMap<String, Object> getCustomData() {
        return this.customData;
    }
}
