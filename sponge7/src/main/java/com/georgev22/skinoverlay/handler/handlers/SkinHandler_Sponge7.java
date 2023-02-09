package com.georgev22.skinoverlay.handler.handlers;

import com.flowpowered.math.vector.Vector3d;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.UserData;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Collection;

public class SkinHandler_Sponge7 extends SkinHandler.SkinHandler_ {

    public SkinHandler_Sponge7() {

    }

    @Override
    public void updateSkin(@NotNull PlayerObject playerObject, @NotNull String skinName, Utils.@NotNull Callback<Boolean> callback) {
        updateSkin(playerObject, skinName, UserData.getUser(playerObject.playerUUID()).getSkinProperty(), callback);
    }

    @Override
    public void updateSkin(@NotNull PlayerObject playerObject, @NotNull String skinName, Property property, Utils.@NotNull Callback<Boolean> callback) {
        Player receiver = (Player) playerObject.player();


        Collection<ProfileProperty> oldProperties = receiver.getProfile().getPropertyMap().get("textures");
        ProfileProperty newTextures = Sponge.getServer().getGameProfileManager().createProfileProperty(property.getName(), property.getValue(), property.getSignature());
        oldProperties.removeIf(property2 -> property2.getName().equals(property.getName()));
        oldProperties.add(newTextures);

        receiver.getTabList().removeEntry(receiver.getUniqueId());
        receiver.getTabList().addEntry(TabListEntry.builder()
                .displayName(receiver.getDisplayNameData().displayName().get())
                .latency(receiver.getConnection().getLatency())
                .list(receiver.getTabList())
                .gameMode(receiver.getGameModeData().type().get())
                .profile(receiver.getProfile())
                .build());


        Location<World> loc = receiver.getLocation();
        Vector3d rotation = receiver.getRotation();

        for (WorldProperties w : Sponge.getServer().getAllWorldProperties()) {
            if (!w.getUniqueId().equals(receiver.getWorld().getUniqueId())) {
                Sponge.getServer().loadWorld(w.getUniqueId());
                Sponge.getServer().getWorld(w.getUniqueId()).ifPresent(value -> receiver.setLocation(value.getSpawnLocation()));
                receiver.setLocationAndRotation(loc, rotation);
                break;
            }
        }

        receiver.offer(Keys.VANISH, true);
        SchedulerManager.getScheduler().runTaskLater(SkinOverlay.getInstance().getClass(), () -> receiver.offer(Keys.VANISH, false), 1L);
        callback.onSuccess();
    }


    @Override
    protected GameProfile getGameProfile0(@NotNull PlayerObject playerObject) {
        org.spongepowered.api.profile.GameProfile spongeGameProfile = ((Player) playerObject.player()).getProfile();
        GameProfile gameProfile = new GameProfile(playerObject.playerUUID(), playerObject.playerName());
        spongeGameProfile.getPropertyMap().forEach((s, profileProperty) -> {
            gameProfile.getProperties().put(s, new Property(profileProperty.getName(), profileProperty.getValue(), profileProperty.getSignature().orElseThrow()));
        });
        return gameProfile;
    }

}
