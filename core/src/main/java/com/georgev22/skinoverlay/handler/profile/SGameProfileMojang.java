package com.georgev22.skinoverlay.handler.profile;

import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.util.UUID;

public class SGameProfileMojang extends SGameProfile {

    public SGameProfileMojang(String name, UUID uuid) {
        super(name, uuid);
    }

    public SGameProfileMojang(String name, UUID uuid, ObjectMap<String, SProperty> properties) {
        super(name, uuid, properties);
    }

    @Override
    public void apply() {
        PlayerObject playerObject = skinOverlay.getPlayer(getUUID()).orElseThrow();
        ((GameProfile) playerObject.internalGameProfile()).getProperties().clear();
        this.getProperties().forEach((s, sProperty) -> ((GameProfile) playerObject.internalGameProfile()).getProperties().put(s, new Property(sProperty.name(), sProperty.value(), sProperty.signature())));
    }

}
