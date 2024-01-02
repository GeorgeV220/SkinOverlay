package com.georgev22.skinoverlay.handler.handlers;

import com.georgev22.skinoverlay.exceptions.SkinException;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.biome.BiomeManager;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static com.georgev22.skinoverlay.handler.handlers.SkinHandler_Unsupported.wrapper;

public final class SkinHandler_1_20_R1 extends SkinHandler {

    @Override
    public CompletableFuture<Boolean> updateSkin(@NotNull PlayerObject playerObject, @NotNull Skin skin) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Player player = playerObject.player();
                final CraftPlayer craftPlayer = (CraftPlayer) player;
                final ServerPlayer entityPlayer = craftPlayer.getHandle();

                ServerLevel world = entityPlayer.serverLevel();
                ServerPlayerGameMode gamemode = entityPlayer.gameMode;

                ClientboundRespawnPacket respawn = new ClientboundRespawnPacket(
                        world.dimensionTypeId(),
                        world.dimension(),
                        BiomeManager.obfuscateSeed(world.getSeed()),
                        gamemode.getGameModeForPlayer(),
                        gamemode.getPreviousGameModeForPlayer(),
                        world.isDebug(),
                        world.isFlat(),
                        ClientboundRespawnPacket.KEEP_ALL_DATA,
                        entityPlayer.getLastDeathLocation(),
                        entityPlayer.getPortalCooldown()
                );

                sendPacket(entityPlayer, new ClientboundPlayerInfoRemovePacket(List.of(player.getUniqueId())));
                sendPacket(entityPlayer, ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(entityPlayer)));

                sendPacket(entityPlayer, respawn);

                entityPlayer.onUpdateAbilities();

                entityPlayer.connection.teleport(player.getLocation());

                entityPlayer.resetSentInfo();

                PlayerList playerList = entityPlayer.server.getPlayerList();
                playerList.sendPlayerPermissionLevel(entityPlayer);
                playerList.sendLevelInfo(entityPlayer, world);
                playerList.sendAllPlayerInfo(entityPlayer);

                for (MobEffectInstance mobEffect : entityPlayer.getActiveEffects()) {
                    ClientboundUpdateMobEffectPacket effect = new ClientboundUpdateMobEffectPacket(entityPlayer.getId(), mobEffect);
                    sendPacket(entityPlayer, effect);
                }
                return true;
            } catch (Exception exception) {
                throw new SkinException(exception);
            }
        });
    }

    @Override
    public void applySkin(@NotNull PlayerObject playerObject, @NotNull Skin skin) {
        this.skinOverlay.getMinecraftScheduler().getScheduler().createDelayedTask(skinOverlay.getPlugin(), () -> {
            Player player = playerObject.player();
            player.hidePlayer(skinOverlay.getSkinOverlay().plugin(), player);
            player.showPlayer(skinOverlay.getSkinOverlay().plugin(), player);
            skinOverlay.getSkinHandler().updateSkin(playerObject, skin).handleAsync((aBoolean, throwable) -> {
                if (throwable != null) {
                    skinOverlay.getLogger().log(Level.SEVERE, "Error updating skin", throwable);
                    return false;
                }
                return aBoolean;
            }).thenAccept(aBoolean -> this.skinOverlay.getMinecraftScheduler().runTask(skinOverlay.getPlugin(), () -> {
                if (aBoolean)
                    skinOverlay.onlinePlayers().stream().filter(playerObjects -> playerObjects != playerObject).forEach(playerObjects -> {
                        Player p = playerObjects.player();
                        p.hidePlayer(skinOverlay.getPlugin(), player);
                        p.showPlayer(skinOverlay.getPlugin(), player);
                    });
            }));
        }, 20L);
    }

    @Override
    public GameProfile getInternalGameProfile(@NotNull PlayerObject playerObject) {
        Player player = playerObject.player();
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        final ServerPlayer entityPlayer = craftPlayer.getHandle();
        return entityPlayer.getGameProfile();
    }

    @Override
    public SGameProfile getGameProfile(@NotNull PlayerObject playerObject) {
        if (sGameProfiles.containsKey(playerObject)) {
            return sGameProfiles.get(playerObject);
        }
        return sGameProfiles.append(playerObject, wrapper(this.getInternalGameProfile(playerObject))).get(playerObject);
    }

    private void sendPacket(@NotNull ServerPlayer player, Packet<?> packet) {
        player.connection.send(packet);
    }
}

