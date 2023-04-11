package com.georgev22.skinoverlay.handler.profile;

import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SGameProfile_Velocity extends SGameProfile {

    public SGameProfile_Velocity(String name, UUID uuid) {
        super(name, uuid);
    }

    public SGameProfile_Velocity(String name, UUID uuid, ObjectMap<String, SProperty> properties) {
        super(name, uuid, properties);
    }

    @Override
    public void apply() {
        PlayerObject playerObject = this.skinOverlay.getPlayer(getUUID()).orElseThrow();
        List<GameProfile.Property> propertyList = new ArrayList<>();
        this.getProperties().forEach((s, sProperty) -> propertyList.add(new GameProfile.Property(sProperty.name(), sProperty.value(), sProperty.signature())));
        ConnectedPlayer connectedPlayer = (ConnectedPlayer) playerObject.player();

        connectedPlayer.setGameProfileProperties(propertyList);
    }
}
