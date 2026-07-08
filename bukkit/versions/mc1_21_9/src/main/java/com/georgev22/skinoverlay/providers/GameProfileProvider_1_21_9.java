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
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Map;

public class GameProfileProvider_1_21_9 extends GameProfileProvider {

    private static final Field PROPERTIES_FIELD;

    static {
        try {
            PROPERTIES_FIELD = PropertyMap.class.getDeclaredField("properties");
            PROPERTIES_FIELD.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }


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
        gameProfile.properties().forEach((name, property) -> propertyObjectMap
                .append(name, new SProperty(property.name(), property.value(), property.signature())));
        SGameProfile sGameProfile = new SGameProfile(gameProfile.name(), gameProfile.id(), propertyObjectMap);
        return sGameProfiles.append(player, sGameProfile).get(player);
    }

    @Override
    public void applyUpdatedGameProfile(@NotNull SPlayer player) {
        GameProfile internalGameProfile = this.getInternalGameProfile(player);
        PropertyMap properties = internalGameProfile.properties();
        SGameProfile gameProfile = this.getGameProfile(player);
        ImmutableMultimap.Builder<String, Property> newProperties = ImmutableMultimap.builder();
        for (Map.Entry<String, SProperty> entry : gameProfile.getProperties().entrySet()) {
            newProperties.put(entry.getKey(), new Property(entry.getKey(), entry.getValue().value(), entry.getValue().signature()));
        }

        try {
            PROPERTIES_FIELD.set(properties, newProperties.build());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
