package com.georgev22.skinoverlay.handler.handlers;

import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.yaml.file.FileConfiguration;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.UserData;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.effect.VanishState;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.storage.ServerWorldProperties;
import org.spongepowered.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;

public class SkinHandler_Sponge extends SkinHandler {

    @SneakyThrows
    @Override
    public void updateSkin(@NotNull FileConfiguration fileConfiguration, @NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName) {
        Property property = UserData.getUser(playerObject.playerUUID()).getSkinProperty();

        ServerPlayer receiver = (ServerPlayer) playerObject.getPlayer();

        receiver.user().offer(Keys.UPDATE_GAME_PROFILE, true);
        receiver.user().offer(Keys.SKIN_PROFILE_PROPERTY, ProfileProperty.of(ProfileProperty.TEXTURES, property.getValue(), property.getSignature()));

        receiver.tabList().removeEntry(receiver.uniqueId());
        receiver.tabList().addEntry(TabListEntry.builder()
                .displayName(receiver.displayName().get())
                .latency(receiver.connection().latency())
                .list(receiver.tabList())
                .gameMode(receiver.gameMode().get())
                .profile(receiver.profile())
                .build());

        ServerLocation loc = receiver.serverLocation();
        Vector3d rotation = receiver.rotation();

        for (ResourceKey w : Sponge.server().worldManager().offlineWorldKeys()) {
            Optional<ServerWorldProperties> worldPropertiesOptional = Sponge.server().worldManager().loadProperties(w).join();

            if (worldPropertiesOptional.isPresent()
                    && !worldPropertiesOptional.get().uniqueId().equals(receiver.world().uniqueId())) {
                ServerWorld world = Sponge.server().worldManager().loadWorld(w).join();
                receiver.setLocation(world.location(world.properties().spawnPosition()));
                receiver.setLocationAndRotation(loc, rotation);
                break;
            }
        }

        receiver.offer(Keys.VANISH_STATE, VanishState.vanished());
        SchedulerManager.getScheduler().runTaskLater(skinOverlay.getClass(), () -> receiver.offer(Keys.VANISH_STATE, VanishState.unvanished()), 1L);

    }

    @Override
    public void updateSkin(@NotNull FileConfiguration fileConfiguration, @NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName, Property property) {
        updateSkin(fileConfiguration, playerObject, reset, skinName);
    }

    @Override
    protected <T> GameProfile getGameProfile0(@NotNull PlayerObject playerObject) {
        ServerPlayer serverPlayer = (ServerPlayer) playerObject.getPlayer();
        List<ProfileProperty> properties = serverPlayer.profile().properties();
        GameProfile gameProfile = new GameProfile(playerObject.playerUUID(), playerObject.playerName());
        properties.forEach(profileProperty -> gameProfile.getProperties().put(profileProperty.name(),
                new Property(profileProperty.name(), profileProperty.value(), profileProperty.signature().orElseThrow())));
        return gameProfile;
    }
}
