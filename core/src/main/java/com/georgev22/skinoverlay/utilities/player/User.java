package com.georgev22.skinoverlay.utilities.player;

import com.georgev22.library.utilities.EntityManager.Entity;
import com.georgev22.skinoverlay.handler.Skin;

import java.io.Serial;
import java.util.UUID;

public class User extends Entity {

    @Serial
    private static final long serialVersionUID = 1L;

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
