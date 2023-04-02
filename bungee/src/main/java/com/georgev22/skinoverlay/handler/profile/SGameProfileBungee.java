package com.georgev22.skinoverlay.handler.profile;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.protocol.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SGameProfileBungee extends SGameProfile {

    private final String name;
    private final UUID uuid;
    private final ObjectMap<String, SProperty> properties;
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    public SGameProfileBungee(String name, UUID uuid) {
        super(name, uuid);
        this.name = name;
        this.uuid = uuid;
        this.properties = new HashObjectMap<>();
    }

    public SGameProfileBungee(String name, UUID uuid, ObjectMap<String, SProperty> properties) {
        super(name, uuid, properties);
        this.name = name;
        this.uuid = uuid;
        this.properties = properties;
    }

    @Override
    public void apply() {
        PlayerObject playerObject = skinOverlay.getPlayer(uuid).orElseThrow();
        LoginResult initialHandler = ((InitialHandler) ((ProxiedPlayer) playerObject.player()).getPendingConnection()).getLoginProfile();
        List<Property> bungeeProperties = new ArrayList<>();
        this.properties.forEach((s, sProperty) -> bungeeProperties.add(new Property(sProperty.name(), sProperty.value(), sProperty.value())));
        initialHandler.setProperties(bungeeProperties.toArray(Property[]::new));
    }
}
