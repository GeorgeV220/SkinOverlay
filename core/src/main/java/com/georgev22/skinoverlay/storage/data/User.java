package com.georgev22.skinoverlay.storage.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class User extends Data implements Serializable {

    @Serial
    private static final long serialVersionUID = 3L;

    public User(UUID uuid) {
        super(uuid);
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
}
