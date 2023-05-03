package com.georgev22.skinoverlay.handler.handlers;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.handler.profile.SGameProfileGlowStone;
import com.georgev22.skinoverlay.storage.User;
import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.ClientSettings;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.profile.GlowPlayerProfile;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.entity.DestroyEntitiesMessage;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.game.PositionRotationMessage;
import net.glowstone.net.message.play.game.RespawnMessage;
import net.glowstone.net.message.play.game.UserListItemMessage;
import net.glowstone.net.message.play.inv.HeldItemMessage;
import org.bukkit.GameMode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class SkinHandler_GlowStone extends SkinHandler {

    /**
     * Update the skin for the specified {@link PlayerObject}
     *
     * @param playerObject Player's {@link PlayerObject} object.
     * @param skinOptions  Skin options
     */
    @Override
    public CompletableFuture<Boolean> updateSkin(@NotNull PlayerObject playerObject, @NotNull SkinOptions skinOptions) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                GlowPlayer glowPlayer = (GlowPlayer) playerObject.player();
                GlowSession glowSession = glowPlayer.getSession();
                GlowServer glowServer = glowPlayer.getServer();

                DestroyEntitiesMessage destroyEntitiesMessage = new DestroyEntitiesMessage(Collections.singletonList(glowPlayer.getEntityId()));
                UserListItemMessage userListItemMessage = new UserListItemMessage(UserListItemMessage.Action.ADD_PLAYER, glowPlayer.getUserListEntry());

                GlowWorld glowWorld = glowPlayer.getWorld();
                GameMode gameMode = glowPlayer.getGameMode();

                RespawnMessage respawnMessage = new RespawnMessage(
                        glowWorld.getEnvironment().getId(),
                        glowWorld.getDifficulty().getValue(),
                        glowPlayer.getGameMode().getValue(),
                        glowWorld.getWorldType().getName().toLowerCase()
                );

                PositionRotationMessage playerPositionLookMessage = new PositionRotationMessage(
                        glowPlayer.getLocation().getX(),
                        glowPlayer.getLocation().getY(),
                        glowPlayer.getLocation().getZ(),
                        glowPlayer.getLocation().getYaw(),
                        glowPlayer.getLocation().getPitch()
                );

                HeldItemMessage heldItemMessage = new HeldItemMessage(glowPlayer.getInventory().getHeldItemSlot());

                glowSession.send(destroyEntitiesMessage);
                glowSession.send(userListItemMessage);
                glowSession.send(respawnMessage);

                glowPlayer.getMetadata().set(MetadataIndex.PLAYER_SKIN_PARTS, skinOptions.getFlags());

                ClientSettings clientSettings = glowPlayer.getSettings();

                glowPlayer.setSettings(new ClientSettings(
                        clientSettings.getLocale(),
                        clientSettings.getViewDistance(),
                        clientSettings.getChatFlags(),
                        clientSettings.isChatColors(),
                        skinOptions.getFlags(),
                        clientSettings.getMainHand()
                ));

                EntityMetadataMessage entityMetadataMessage = new EntityMetadataMessage(glowPlayer.getEntityId(), glowPlayer.getMetadata().getChanges());

                glowSession.send(entityMetadataMessage);

                glowServer.sendPlayerAbilities(glowPlayer);

                glowSession.send(playerPositionLookMessage);
                glowSession.send(heldItemMessage);

                glowPlayer.updateInventory();
                return true;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    /**
     * Update the skin for the specified {@link PlayerObject} and {@link SProperty}
     *
     * @param playerObject Player's {@link PlayerObject} object.
     * @param skinOptions  Skin options)
     * @param property     {@link SProperty} to set
     */
    @Override
    public CompletableFuture<Boolean> updateSkin(@NotNull PlayerObject playerObject, @NotNull SkinOptions skinOptions, SProperty property) {
        return this.updateSkin(playerObject, skinOptions);
    }

    /**
     * Retrieves {@link PlayerObject}'s {@link SGameProfile}
     *
     * @param playerObject {@link PlayerObject} object
     * @return {@link PlayerObject}'s {@link SGameProfile}
     */
    @Override
    public GlowPlayerProfile getGameProfile0(@NotNull PlayerObject playerObject) {
        return ((GlowPlayer) playerObject.player()).getProfile();
    }

    /**
     * Retrieves {@link PlayerObject}'s {@link SGameProfile}
     *
     * @param playerObject {@link PlayerObject} object
     * @return {@link PlayerObject}'s {@link SGameProfile}
     */
    @Override
    public SGameProfile getGameProfile(@NotNull PlayerObject playerObject) {
        if (sGameProfiles.containsKey(playerObject)) {
            return sGameProfiles.get(playerObject);
        }
        return sGameProfiles.append(playerObject, wrapper(getGameProfile0(playerObject))).get(playerObject);
    }

    protected void updateSkin0(User user, PlayerObject playerObject, boolean forOthers) {
        SchedulerManager.getScheduler().runTaskLater(skinOverlay.getClass(), () -> {
            GlowPlayer player = (GlowPlayer) playerObject.player();
            player.hidePlayer(player);
            player.showPlayer(player);
            skinOverlay.getSkinHandler().updateSkin(playerObject, SkinOptions.getSkinOptions(user.getCustomData("skinOptions"))).handleAsync((aBoolean, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return false;
                }
                return aBoolean;
            }).thenAccept(aBoolean -> SchedulerManager.getScheduler().runTask(skinOverlay.getClass(), () -> {
                if (aBoolean)
                    if (forOthers) {
                        skinOverlay.onlinePlayers().stream().filter(playerObjects -> playerObjects != playerObject).forEach(playerObjects -> {
                            GlowPlayer p = (GlowPlayer) playerObjects.player();
                            p.hidePlayer(player);
                            p.showPlayer(player);
                        });
                    }
            }));
        }, 20L);
    }

    @Contract("_ -> new")
    private @NotNull SGameProfile wrapper(@NotNull GlowPlayerProfile glowPlayerProfile) {
        ObjectMap<String, SProperty> propertyObjectMap = new HashObjectMap<>();
        glowPlayerProfile.getProperties().forEach(property -> propertyObjectMap.append(property.getName(), new SProperty(property.getName(), property.getValue(), property.getSignature())));
        return new SGameProfileGlowStone(glowPlayerProfile.getName(), glowPlayerProfile.getId(), propertyObjectMap);
    }
}
