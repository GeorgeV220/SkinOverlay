package com.georgev22.skinoverlay.handler.profile;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.property.ProfileProperty;

import java.util.Optional;
import java.util.UUID;

public class SGameProfile_Sponge7 extends SGameProfile {

    private final String name;
    private final UUID uuid;
    private final ObjectMap<String, SProperty> properties;
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    public SGameProfile_Sponge7(String name, UUID uuid) {
        super(name, uuid);
        this.name = name;
        this.uuid = uuid;
        this.properties = new HashObjectMap<>();
    }

    public SGameProfile_Sponge7(String name, UUID uuid, ObjectMap<String, SProperty> properties) {
        super(name, uuid, properties);
        this.name = name;
        this.uuid = uuid;
        this.properties = properties;
    }

    @Override
    public void apply() {
        PlayerObject playerObject = skinOverlay.getPlayer(uuid).orElseThrow();
        ((GameProfile) playerObject.internalGameProfile()).getPropertyMap().clear();
        properties.forEach((s, sProperty) -> ((GameProfile) playerObject.internalGameProfile()).addProperty(s, new ProfileProperty() {
            @Override
            public @NotNull String getName() {
                return sProperty.name();
            }

            @Override
            public @NotNull String getValue() {
                return sProperty.value();
            }

            @Override
            public @NotNull Optional<String> getSignature() {
                return Optional.ofNullable(sProperty.signature());
            }
        }));
    }
}
