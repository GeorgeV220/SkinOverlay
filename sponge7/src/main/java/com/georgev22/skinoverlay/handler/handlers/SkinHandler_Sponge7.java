package com.georgev22.skinoverlay.handler.handlers;

import com.flowpowered.math.vector.Vector3d;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.UserManager;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.handler.profile.SGameProfile_Sponge7;
import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public class SkinHandler_Sponge7 extends SkinHandler {

    @Override
    public CompletableFuture<Boolean> updateSkin(@NotNull PlayerObject playerObject, @NotNull SkinOptions skinOptions) {
        AtomicReference<CompletableFuture<Boolean>> atomicReference = new AtomicReference<>();
        skinOverlay.getUserManager().getUser(playerObject.playerUUID()).handle((user, throwable) -> {
            if (throwable != null) {
                skinOverlay.getLogger().log(Level.SEVERE, "Error: ", throwable);
                return null;
            }
            return user;
        }).thenAccept(user -> {
            if (user != null)
                atomicReference.set(updateSkin(playerObject, skinOptions, user.getCustomData("skinProperty")));
        });
        return atomicReference.get();
    }

    @Override
    public CompletableFuture<Boolean> updateSkin(@NotNull PlayerObject playerObject, @NotNull SkinOptions skinOptions, SProperty property) {
        return CompletableFuture.supplyAsync(() -> {
            Player receiver = (Player) playerObject.player();


            Collection<ProfileProperty> oldProperties = receiver.getProfile().getPropertyMap().get("textures");
            ProfileProperty newTextures = Sponge.getServer().getGameProfileManager().createProfileProperty(property.name(), property.value(), property.signature());
            oldProperties.removeIf(property2 -> property2.getName().equals(property.name()));
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
            return true;
        });
    }


    @Override
    public GameProfile getGameProfile0(@NotNull PlayerObject playerObject) {
        return ((Player) playerObject.player()).getProfile();
    }

    @Override
    public SGameProfile getGameProfile(@NotNull PlayerObject playerObject) {
        if (sGameProfiles.containsKey(playerObject)) {
            return sGameProfiles.get(playerObject);
        }
        return sGameProfiles.append(playerObject, wrapper(this.getGameProfile0(playerObject))).get(playerObject);
    }

    public static @NotNull SGameProfile wrapper(@NotNull GameProfile gameProfile) {
        ObjectMap<String, SProperty> propertyObjectMap = new HashObjectMap<>();
        gameProfile.getPropertyMap().forEach((s, property) -> propertyObjectMap.append(s, new SProperty(property.getName(), property.getValue(), property.getSignature().orElse(null))));
        return new SGameProfile_Sponge7(gameProfile.getName().orElse(null), gameProfile.getUniqueId(), propertyObjectMap);
    }

    @Override
    protected void updateSkin0(UserManager.User user, PlayerObject playerObject, boolean forOthers) {
        updateSkin1(user, playerObject, forOthers);
    }

}
