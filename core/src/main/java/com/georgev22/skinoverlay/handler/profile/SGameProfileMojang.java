package com.georgev22.skinoverlay.handler.profile;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.util.UUID;

public class SGameProfileMojang extends SGameProfile {

    private final String name;
    private final UUID uuid;
    private final ObjectMap<String, SProperty> properties;
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    public SGameProfileMojang(String name, UUID uuid) {
        super(name, uuid);
        this.name = name;
        this.uuid = uuid;
        this.properties = new HashObjectMap<>();
    }

    public SGameProfileMojang(String name, UUID uuid, ObjectMap<String, SProperty> properties) {
        super(name, uuid, properties);
        this.name = name;
        this.uuid = uuid;
        this.properties = properties;
    }

    @Override
    public void apply() {
        PlayerObject playerObject = skinOverlay.getPlayer(uuid).orElseThrow();
        ((GameProfile) playerObject.internalGameProfile()).getProperties().clear();
        properties.forEach((s, sProperty) -> ((GameProfile) playerObject.internalGameProfile()).getProperties().put(s, new Property(sProperty.name(), sProperty.value(), sProperty.signature())));
    }

}
