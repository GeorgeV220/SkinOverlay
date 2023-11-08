package com.georgev22.skinoverlay.storage.data;

import com.georgev22.library.maps.ConcurrentObjectMap;
import com.georgev22.library.utilities.Entity;

import java.util.UUID;

public class Data implements Entity {

    private final ConcurrentObjectMap<String, Object> customData;
    private final UUID uuid;

    public Data(UUID uuid) {
        this.customData = new ConcurrentObjectMap<>();
        this.uuid = uuid;
    }

    @Override
    public UUID getId() {
        return this.uuid;
    }

    @Override
    public ConcurrentObjectMap<String, Object> getCustomData() {
        return this.customData;
    }
}
