package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.datastructures.maps.HashObjectMap;
import com.georgev22.skinoverlay.datastructures.maps.ObjectMap;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.skin.SGameProfile;
import com.georgev22.skinoverlay.skin.SProperty;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.GameProfile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VelocityGameProfileProvider extends GameProfileProvider {

    @Override
    public GameProfile getInternalGameProfile(@NotNull SPlayer player) {
        return ((Player) player.getPlayer()).getGameProfile();
    }

    @Override
    public SGameProfile getGameProfile(@NotNull SPlayer player) {
        if (sGameProfiles.containsKey(player)) {
            return sGameProfiles.get(player);
        }
        ObjectMap<String, SProperty> propertyObjectMap = new HashObjectMap<>();
        GameProfile gameProfile = this.getInternalGameProfile(player);
        gameProfile.getProperties().forEach(property ->
                propertyObjectMap.append(property.getName(), new SProperty(property.getValue(), property.getSignature())));
        SGameProfile sGameProfile = new SGameProfile(gameProfile.getName(), gameProfile.getId(), propertyObjectMap);
        return sGameProfiles.append(player, sGameProfile).get(player);
    }

    @Override
    public void applyUpdatedGameProfile(@NotNull SPlayer player) {
        Player velocityPlayer = player.getPlayer();
        List<GameProfile.Property> propertyList = new ArrayList<>();
        getGameProfile(player).getProperties().forEach((s, sProperty) -> propertyList.add(
                new GameProfile.Property(s, sProperty.value(), sProperty.signature())));
        velocityPlayer.setGameProfileProperties(propertyList);
    }
}
