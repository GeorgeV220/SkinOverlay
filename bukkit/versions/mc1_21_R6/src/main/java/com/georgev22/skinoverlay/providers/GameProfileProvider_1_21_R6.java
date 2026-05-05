package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.datastructures.maps.HashObjectMap;
import com.georgev22.skinoverlay.datastructures.maps.ObjectMap;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.skin.SGameProfile;
import com.georgev22.skinoverlay.skin.SProperty;
import com.google.common.collect.ImmutableMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.lenni0451.reflect.stream.RStream;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GameProfileProvider_1_21_R6 extends GameProfileProvider {
    @Override
    public @NotNull GameProfile getInternalGameProfile(@NotNull SPlayer player) {
        Player bukkitPlayer = player.getPlayer();
        final CraftPlayer craftPlayer = (CraftPlayer) bukkitPlayer;
        final ServerPlayer entityPlayer = craftPlayer.getHandle();
        return entityPlayer.getGameProfile();
    }

    @Override
    public SGameProfile getGameProfile(@NotNull SPlayer player) {
        if (sGameProfiles.containsKey(player)) {
            return sGameProfiles.get(player);
        }
        ObjectMap<String, SProperty> propertyObjectMap = new HashObjectMap<>();
        GameProfile gameProfile = this.getInternalGameProfile(player);
        gameProfile.properties().forEach((s, property) -> propertyObjectMap.append(s, new SProperty(property.value(), property.signature())));
        SGameProfile sGameProfile = new SGameProfile(gameProfile.name(), gameProfile.id(), propertyObjectMap);
        return sGameProfiles.append(player, sGameProfile).get(player);
    }

    @Override
    public void applyUpdatedGameProfile(@NotNull SPlayer player) {
        GameProfile internalGameProfile = this.getInternalGameProfile(player);
        PropertyMap properties = internalGameProfile.properties();
        SGameProfile gameProfile = this.getGameProfile(player);
        ImmutableMultimap.Builder<String, Object> newProperties = ImmutableMultimap.builder();
        for (Map.Entry<String, SProperty> entry : gameProfile.getProperties().entrySet()) {
            newProperties.put(entry.getKey(), new Property(entry.getValue().value(), entry.getValue().signature()));
        }

        RStream.of(properties)
                .withSuper()
                .fields()
                .by("properties")
                .set(newProperties.build());
    }
}
