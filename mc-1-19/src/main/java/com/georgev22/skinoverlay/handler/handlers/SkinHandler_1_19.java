
package com.georgev22.skinoverlay.handler.handlers;

import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.Skin;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.utilities.player.User;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import io.papermc.lib.PaperLib;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.biome.BiomeManager;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

import static com.georgev22.skinoverlay.handler.handlers.SkinHandler_Unsupported.wrapper;

public class SkinHandler_1_19 extends SkinHandler {

    @Override
    public CompletableFuture<Boolean> updateSkin(@NotNull PlayerObject playerObject, @NotNull Skin skin) {
        return CompletableFuture.supplyAsync(() -> {
            try {

                Player player = (Player) playerObject.player();
                final CraftPlayer craftPlayer = (CraftPlayer) player;
                final ServerPlayer entityPlayer = craftPlayer.getHandle();


                ClientboundPlayerInfoPacket removePlayer = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, ImmutableList.of(entityPlayer));
                ClientboundPlayerInfoPacket addPlayer = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, ImmutableList.of(entityPlayer));

                ServerLevel world = entityPlayer.getLevel();
                ServerPlayerGameMode gamemode = entityPlayer.gameMode;

                ClientboundRespawnPacket respawn = new ClientboundRespawnPacket(
                        world.dimensionTypeId(),
                        world.dimension(),
                        BiomeManager.obfuscateSeed(world.getSeed()),
                        gamemode.getGameModeForPlayer(),
                        gamemode.getPreviousGameModeForPlayer(),
                        world.isDebug(),
                        world.isFlat(),
                        true,
                        entityPlayer.getLastDeathLocation()
                );

                Location l = player.getLocation();
                ClientboundPlayerPositionPacket pos = new ClientboundPlayerPositionPacket(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), new HashSet<>(), 0, false);
                ClientboundSetCarriedItemPacket slot = new ClientboundSetCarriedItemPacket(player.getInventory().getHeldItemSlot());

                sendPacket(entityPlayer, removePlayer);
                sendPacket(entityPlayer, addPlayer);

                sendPacket(entityPlayer, respawn);

                SynchedEntityData synchedEntityData = entityPlayer.getEntityData();

                synchedEntityData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), skin.skinOptions().getFlags());


                ClientboundSetEntityDataPacket clientboundSetEntityDataPacket = new ClientboundSetEntityDataPacket(entityPlayer.getId(), synchedEntityData, true);

                sendPacket(entityPlayer, clientboundSetEntityDataPacket);

                entityPlayer.onUpdateAbilities();

                sendPacket(entityPlayer, pos);
                sendPacket(entityPlayer, slot);
                craftPlayer.updateScaledHealth();
                player.updateInventory();
                entityPlayer.resetSentInfo();
                return true;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Override
    public GameProfile getGameProfile0(@NotNull PlayerObject playerObject) {
        Player player = (Player) playerObject.player();
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        final ServerPlayer entityPlayer = craftPlayer.getHandle();
        if (PaperLib.isSpigot())
            try {
                Field field = entityPlayer.getClass().getDeclaredField("ct");
                if (Modifier.isPrivate(field.getModifiers())) {
                    return (GameProfile) Utils.Reflection.fetchDeclaredField(entityPlayer.getClass().getSuperclass(), entityPlayer, "ct");
                }
            } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException exception) {
                exception.printStackTrace();
            }
        return entityPlayer.getGameProfile();
    }

    @Override
    public SGameProfile getGameProfile(@NotNull PlayerObject playerObject) {
        if (sGameProfiles.containsKey(playerObject)) {
            return sGameProfiles.get(playerObject);
        }
        return sGameProfiles.append(playerObject, wrapper(this.getGameProfile0(playerObject))).get(playerObject);
    }

    private void sendPacket(@NotNull ServerPlayer player, Packet<?> packet) {
        player.connection.send(packet);
    }

    @Override
    protected void updateSkin0(User user, PlayerObject playerObject, boolean forOthers) {
        SchedulerManager.getScheduler().runTaskLater(skinOverlay.getClass(), () -> {
            Player player = (Player) playerObject.player();
            player.hidePlayer((Plugin) skinOverlay.getSkinOverlay().plugin(), player);
            player.showPlayer((Plugin) skinOverlay.getSkinOverlay().plugin(), player);
            skinOverlay.getSkinHandler().updateSkin(playerObject, user.skin()).handleAsync((aBoolean, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return false;
                }
                return aBoolean;
            }).thenAccept(aBoolean -> SchedulerManager.getScheduler().runTask(skinOverlay.getClass(), () -> {
                if (aBoolean)
                    if (forOthers) {
                        skinOverlay.onlinePlayers().stream().filter(playerObjects -> playerObjects != playerObject).forEach(playerObjects -> {
                            Player p = (Player) playerObjects.player();
                            p.hidePlayer((Plugin) skinOverlay.getSkinOverlay().plugin(), player);
                            p.showPlayer((Plugin) skinOverlay.getSkinOverlay().plugin(), player);
                        });
                    }
            }));
        }, 20L);
    }
}

