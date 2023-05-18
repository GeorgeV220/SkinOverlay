package com.georgev22.skinoverlay.handler.profile;

import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.protocol.Property;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public class SGameProfileBungee extends SGameProfile {


    public SGameProfileBungee(String name, UUID uuid) {
        super(name, uuid);
    }

    public SGameProfileBungee(String name, UUID uuid, ObjectMap<String, SProperty> properties) {
        super(name, uuid, properties);
    }

    @Override
    public void apply() {
        PlayerObject playerObject = skinOverlay.getPlayer(getUUID()).orElseThrow();
        LoginResult initialHandler = ((InitialHandler) ((ProxiedPlayer) playerObject.player()).getPendingConnection()).getLoginProfile();
        List<Property> bungeeProperties = new ArrayList<>();
        this.getProperties().forEach((s, sProperty) -> bungeeProperties.add(new Property(sProperty.name(), sProperty.value(), sProperty.signature())));
        initialHandler.setProperties(bungeeProperties.toArray(Property[]::new));
    }
}
