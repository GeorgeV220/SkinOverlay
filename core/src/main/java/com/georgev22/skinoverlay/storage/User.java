package com.georgev22.skinoverlay.storage;

import com.georgev22.library.utilities.EntityManager.Entity;
import com.georgev22.skinoverlay.handler.SProperty;

import java.util.UUID;

public class User extends Entity {

    public User(UUID uuid) {
        super(uuid);
    }

    public SProperty defaultProperty() {
        return getCustomData("defaultProperty");
    }

    public SProperty skinProperty() {
        return getCustomData("skinProperty");
    }

}
