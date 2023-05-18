package com.georgev22.skinoverlay.handler.profile;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import net.glowstone.entity.meta.profile.GlowPlayerProfile;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public class SGameProfileGlowStone extends SGameProfile {

    public SGameProfileGlowStone(String name, UUID uuid) {
        super(name, uuid);
    }

    public SGameProfileGlowStone(String name, UUID uuid, ObjectMap<String, SProperty> properties) {
        super(name, uuid, properties);
    }

    @Override
    public void apply() {
        PlayerObject playerObject = skinOverlay.getPlayer(getUUID()).orElseThrow();
        GlowPlayerProfile playerProfile = (GlowPlayerProfile) playerObject.internalGameProfile();
        playerProfile.clearProperties();
        playerProfile.setProperties(
                this.getProperties().values().stream().map(sProperty -> new ProfileProperty(
                        sProperty.name(),
                        sProperty.value(),
                        sProperty.signature())
                ).toList());
    }

}
