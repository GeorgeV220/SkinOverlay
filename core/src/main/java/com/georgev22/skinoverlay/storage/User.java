package com.georgev22.skinoverlay.storage;

import com.georgev22.library.utilities.EntityManager;
import com.georgev22.skinoverlay.handler.Skin;

import java.util.UUID;

public class User extends EntityManager.Entity {

    public User(UUID uuid) {
        super(uuid);
    }

    public Skin defaultSkin() {
        return getCustomData("defaultSkin");
    }

    public Skin skin() {
        return getCustomData("skin");
    }

}
