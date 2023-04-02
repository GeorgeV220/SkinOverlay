package com.georgev22.skinoverlay.handler.profile;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SGameProfile_Velocity extends SGameProfile {

    private final String name;
    private final UUID uuid;
    private final ObjectMap<String, SProperty> properties;
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    public SGameProfile_Velocity(String name, UUID uuid) {
        super(name, uuid);
        this.name = name;
        this.uuid = uuid;
        this.properties = new HashObjectMap<>();
    }

    public SGameProfile_Velocity(String name, UUID uuid, ObjectMap<String, SProperty> properties) {
        super(name, uuid, properties);
        this.name = name;
        this.uuid = uuid;
        this.properties = properties;
    }

    @Override
    public void apply() {
        PlayerObject playerObject = skinOverlay.getPlayer(uuid).orElseThrow();
        List<GameProfile.Property> propertyList = new ArrayList<>();
        properties.forEach((s, sProperty) -> propertyList.add(new GameProfile.Property(sProperty.name(), sProperty.value(), sProperty.signature())));
        ConnectedPlayer connectedPlayer = (ConnectedPlayer) playerObject.player();

        connectedPlayer.setGameProfileProperties(propertyList);
    }
}
