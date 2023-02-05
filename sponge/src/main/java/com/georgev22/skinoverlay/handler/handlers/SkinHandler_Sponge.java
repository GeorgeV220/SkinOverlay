package com.georgev22.skinoverlay.handler.handlers;

import com.georgev22.library.exceptions.ReflectionException;
import com.georgev22.library.minecraft.Sponge8MinecraftUtils;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.SkinOverlaySponge;
import com.georgev22.skinoverlay.handler.SkinHandler.SkinHandler_;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.UserData;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.effect.VanishState;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.georgev22.library.utilities.Utils.Reflection.*;

public class SkinHandler_Sponge extends SkinHandler_ {


    private final ClassLoader classLoader;
    private final Class<?> serverPlayerClass;
    private final Class<?> serverWorldClass;
    private final Class<?> respawnPacketClass;
    private final Class<?> entityDataPacketClass;
    private final Class<?> entityDataSerializersClass;
    private final Class<?> entityDataAccessorClass;

    private final Class<?> addPlayerPacketClass;
    private final Class<?> removePlayerPacketClass;
    private final Class<?> packet;

    public SkinHandler_Sponge() {
        classLoader = SkinOverlaySponge.getInstance().serverImpl().getClass().getClassLoader();
        serverPlayerClass = Utils.Reflection.getClass("net.minecraft.server.level.ServerPlayer", classLoader);
        serverWorldClass = Utils.Reflection.getClass("net.minecraft.server.level.ServerLevel", classLoader);
        respawnPacketClass = Utils.Reflection.getClass("net.minecraft.network.protocol.game.ClientboundRespawnPacket", classLoader);
        entityDataPacketClass = Utils.Reflection.getClass("net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket", classLoader);
        entityDataSerializersClass = Utils.Reflection.getClass("net.minecraft.network.syncher.EntityDataSerializers", classLoader);
        entityDataAccessorClass = Utils.Reflection.getClass("net.minecraft.network.syncher.EntityDataAccessor", classLoader);
        if (Sponge8MinecraftUtils.MinecraftVersion.getCurrentVersion().equals(Sponge8MinecraftUtils.MinecraftVersion.V1_19_R2)) {
            removePlayerPacketClass = Utils.Reflection.getClass("net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket", classLoader);
            addPlayerPacketClass = Utils.Reflection.getClass("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket", classLoader);
        } else {
            removePlayerPacketClass = Utils.Reflection.getClass("net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket", classLoader);
            addPlayerPacketClass = Utils.Reflection.getClass("net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket", classLoader);
        }
        packet = Utils.Reflection.getClass("net.minecraft.network.protocol.Packet", classLoader);
    }

    @SneakyThrows
    @Override
    public void updateSkin(@NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName, Utils.@NotNull Callback<Boolean> callback) {
        updateSkin(playerObject, reset, skinName, UserData.getUser(playerObject.playerUUID()).getSkinProperty(), callback);
    }

    @SneakyThrows
    @Override
    public void updateSkin(@NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName, Property property, @NotNull final Utils.Callback<Boolean> callback) {
        ServerPlayer receiver = (ServerPlayer) playerObject.player();

        receiver.user().offer(Keys.UPDATE_GAME_PROFILE, true);
        receiver.user().offer(Keys.SKIN_PROFILE_PROPERTY, ProfileProperty.of(ProfileProperty.TEXTURES, property.getValue(), property.getSignature()));

        long seedEncrypted = Hashing.sha256().hashString(String.valueOf(receiver.world().seed()), StandardCharsets.UTF_8).asLong();

        Object serverPlayer = serverPlayerClass.cast(receiver);

        Object serverWorld = fetchMethodAndInvoke(serverPlayerClass, "getLevel", serverPlayer, new Object[]{}, new Class[]{});

        Object gameMode = fetchField(serverPlayerClass, serverPlayer, "gameMode");

        Object dimensionType;
        try {
            dimensionType = fetchMethodAndInvoke(serverWorldClass, "dimensionTypeId", serverWorld, new Object[]{}, new Class[]{});
        } catch (Exception ignored) {
            try {
                dimensionType = fetchMethodAndInvoke(serverWorldClass, "dimensionTypeRegistration", serverWorld, new Object[]{}, new Class[]{});
            } catch (Exception ignored2) {
                dimensionType = fetchMethodAndInvoke(serverWorldClass, "dimensionType", serverWorld, new Object[]{}, new Class[]{});
            }
        }

        Object dimension = fetchMethodAndInvoke(serverWorldClass, "dimension", serverWorld, new Object[]{}, new Class[]{});

        Object gameModeForPlayer = fetchMethodAndInvoke(gameMode.getClass(), "getGameModeForPlayer", gameMode, new Object[]{}, new Class[]{});

        Object previousGameModeForPlayer = fetchMethodAndInvoke(gameMode.getClass(), "getPreviousGameModeForPlayer", gameMode, new Object[]{}, new Class[]{});

        if (previousGameModeForPlayer == null) {
            previousGameModeForPlayer = gameModeForPlayer;
        }

        Object addPlayer;
        Object removePlayer;
        //Add remove player packet
        if (Sponge8MinecraftUtils.MinecraftVersion.getCurrentVersion().equals(Sponge8MinecraftUtils.MinecraftVersion.V1_19_R2)) {
            removePlayer = invokeConstructor(removePlayerPacketClass, List.of(receiver.uniqueId()));
            addPlayer = fetchMethodAndInvoke(addPlayerPacketClass, "createPlayerInitializing", null,
                    new Object[]{List.of(serverPlayer)},
                    new Class[]{Collection.class}
            );

        } else {
            removePlayer = invokeConstructor(removePlayerPacketClass,
                    getEnum(Utils.Reflection.getClass(removePlayerPacketClass.getName() + "$Action", classLoader), "REMOVE_PLAYER"),
                    ImmutableList.of(serverPlayer));
            addPlayer = invokeConstructor(addPlayerPacketClass,
                    getEnum(Utils.Reflection.getClass(addPlayerPacketClass.getName() + "$Action", classLoader), "ADD_PLAYER"),
                    ImmutableList.of(serverPlayer));
        }

        Object respawnPacket;
        //Respawn packet
        try {

            switch (Sponge8MinecraftUtils.MinecraftVersion.getCurrentVersion()) {
                case V1_19_R2, UNKNOWN -> respawnPacket = invokeConstructor(
                        respawnPacketClass,
                        dimensionType,
                        dimension,
                        seedEncrypted,
                        gameModeForPlayer,
                        previousGameModeForPlayer,
                        fetchMethodAndInvoke(serverWorldClass, "isDebug", serverWorld, new Object[]{}, new Class[]{}),
                        fetchMethodAndInvoke(serverWorldClass, "isFlat", serverWorld, new Object[]{}, new Class[]{}),
                        (byte) 3,
                        fetchMethodAndInvoke(serverPlayerClass, "getLastDeathLocation", serverPlayer, new Object[]{}, new Class[]{})
                );
                case V1_19_R1 -> respawnPacket = invokeConstructor(
                        respawnPacketClass,
                        dimensionType,
                        dimension,
                        seedEncrypted,
                        gameModeForPlayer,
                        previousGameModeForPlayer,
                        fetchMethodAndInvoke(serverWorldClass, "isDebug", serverWorld, new Object[]{}, new Class[]{}),
                        fetchMethodAndInvoke(serverWorldClass, "isFlat", serverWorld, new Object[]{}, new Class[]{}),
                        true,
                        fetchMethodAndInvoke(serverPlayerClass, "getLastDeathLocation", serverPlayer, new Object[]{}, new Class[]{})
                );
                default -> respawnPacket = invokeConstructor(
                        respawnPacketClass,
                        dimensionType,
                        dimension,
                        seedEncrypted,
                        gameModeForPlayer,
                        previousGameModeForPlayer,
                        fetchMethodAndInvoke(serverWorldClass, "isDebug", serverWorld, new Object[]{}, new Class[]{}),
                        fetchMethodAndInvoke(serverWorldClass, "isFlat", serverWorld, new Object[]{}, new Class[]{}),
                        true);
            }
        } catch (Exception exception) {
            callback.onFailure(exception);
            return;
        }

        //EntityMetaData packet
        Object entityDataPacket;
        Object synchedEntityData = fetchMethodAndInvoke(serverPlayerClass, "getEntityData", serverPlayer, new Object[]{}, new Class[]{});
        Object entityDataAccessor;

        if (reset | skinName.equalsIgnoreCase("default")) {
            fetchMethodAndInvoke(synchedEntityData.getClass(), "set", synchedEntityData,
                    new Object[]{
                            entityDataAccessor = invokeConstructor(
                                    entityDataAccessorClass,
                                    Sponge8MinecraftUtils.MinecraftVersion.getCurrentVersion().isBelow(Sponge8MinecraftUtils.MinecraftVersion.V1_17_R1) ? 16 : 17,
                                    fetchField(entityDataSerializersClass, null, "BYTE")),
                            (byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40)
                    },
                    new Class[]{
                            entityDataAccessor.getClass(),
                            Object.class
                    });
        } else {
            fetchMethodAndInvoke(synchedEntityData.getClass(), "set", synchedEntityData,
                    new Object[]{
                            entityDataAccessor = invokeConstructor(
                                    entityDataAccessorClass,
                                    Sponge8MinecraftUtils.MinecraftVersion.getCurrentVersion().isBelow(Sponge8MinecraftUtils.MinecraftVersion.V1_17_R1) ? 16 : 17,
                                    fetchField(entityDataSerializersClass, null, "BYTE")),
                            (byte)
                                    ((skinOverlay.getConfig().getBoolean("Options.overlays." + skinName + ".cape", false) ? 0x01 : 0x00) |
                                            (skinOverlay.getConfig().getBoolean("Options.overlays." + skinName + ".jacket", false) ? 0x02 : 0x00) |
                                            (skinOverlay.getConfig().getBoolean("Options.overlays." + skinName + ".left_sleeve", false) ? 0x04 : 0x00) |
                                            (skinOverlay.getConfig().getBoolean("Options.overlays." + skinName + ".right_sleeve", false) ? 0x08 : 0x00) |
                                            (skinOverlay.getConfig().getBoolean("Options.overlays." + skinName + ".left_pants", false) ? 0x10 : 0x00) |
                                            (skinOverlay.getConfig().getBoolean("Options.overlays." + skinName + ".right_pants", false) ? 0x20 : 0x00) |
                                            (skinOverlay.getConfig().getBoolean("Options.overlays." + skinName + ".hat", false) ? 0x40 : 0x00))
                    },
                    new Class[]{
                            entityDataAccessor.getClass(),
                            Object.class
                    });
        }
        try {
            fetchMethodAndInvoke(synchedEntityData.getClass(), "markDirty", synchedEntityData, new Object[]{entityDataAccessor}, new Class[]{entityDataAccessorClass});
        } catch (Exception ignore) {
            markDirty(synchedEntityData, entityDataAccessor);
        }
        if (Sponge8MinecraftUtils.MinecraftVersion.getCurrentVersion().equals(Sponge8MinecraftUtils.MinecraftVersion.V1_19_R2)) {
            entityDataPacket = invokeConstructor(entityDataPacketClass,
                    fetchMethodAndInvoke(serverPlayerClass, "getId", serverPlayer, new Object[]{}, new Class[]{}),
                    fetchMethodAndInvoke(synchedEntityData.getClass(), "packDirty", synchedEntityData, new Object[]{}, new Class[]{})
            );
        } else {
            entityDataPacket = invokeConstructor(entityDataPacketClass,
                    fetchMethodAndInvoke(serverPlayerClass, "getId", serverPlayer, new Object[]{}, new Class[]{}),
                    synchedEntityData,
                    true
            );
        }

        ServerLocation serverLocation = receiver.serverLocation();
        Vector3d rotation = receiver.rotation();

        Object playerConnection = getFieldByType(serverPlayer, "ServerGamePacketListenerImpl");

        Object playerPositionPacket;
        Object playerCarriedItemPacket;
        try {
            Class<?> playerPositionPacketClass = Utils.Reflection.getClass("net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket", classLoader);
            Class<?> playerCarriedItemPacketClass = Utils.Reflection.getClass("net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket", classLoader);
            switch (Sponge8MinecraftUtils.MinecraftVersion.getCurrentVersion()) {
                case V1_19_R2, V1_19_R1, V1_18_R2, V1_18_R1 ->
                        playerPositionPacket = invokeConstructor(playerPositionPacketClass,
                                serverLocation.x(),
                                serverLocation.y(),
                                serverLocation.z(),
                                (float) rotation.y(),
                                (float) rotation.x(),
                                new HashSet<>(),
                                0,
                                false);
                case V1_17_R1 -> playerPositionPacket = invokeConstructor(playerPositionPacketClass,
                        serverLocation.x(),
                        serverLocation.y(),
                        serverLocation.z(),
                        (float) rotation.y(),
                        (float) rotation.x(),
                        false);
                case V1_16_R3, V1_16_R2 -> playerPositionPacket = invokeConstructor(playerPositionPacketClass,
                        serverLocation.x(),
                        serverLocation.y(),
                        serverLocation.z(),
                        (float) rotation.y(),
                        (float) rotation.x(),
                        new HashSet<>(),
                        0);
                default -> playerPositionPacket = invokeConstructor(playerPositionPacketClass,
                        serverLocation.x(),
                        serverLocation.y(),
                        serverLocation.z(),
                        (float) rotation.y(),
                        (float) rotation.x(),
                        new HashSet<>());
            }

            playerCarriedItemPacket = invokeConstructor(playerCarriedItemPacketClass,
                    receiver.inventory().hotbar().selectedSlotIndex()
            );
        } catch (Exception exception) {
            callback.onFailure(exception);
            return;
        }

        sendPacketToAll(removePlayer);
        sendPacketToAll(addPlayer);

        sendPacket(playerConnection, respawnPacket);
        sendPacket(playerConnection, entityDataPacket);
        receiver.offer(Keys.VANISH_STATE, VanishState.vanished());
        SchedulerManager.getScheduler().runTaskLater(skinOverlay.getClass(), () -> receiver.offer(Keys.VANISH_STATE, VanishState.unvanished()), 1L);
        fetchMethodAndInvoke(serverPlayerClass, "onUpdateAbilities", serverPlayer, new Object[]{}, new Class[]{});
        sendPacket(playerConnection, playerPositionPacket);
        sendPacket(playerConnection, playerCarriedItemPacket);

        if (Sponge8MinecraftUtils.MinecraftVersion.getCurrentVersion().isAboveOrEqual(Sponge8MinecraftUtils.MinecraftVersion.V1_17_R1)) {
            Object container = fetchDeclaredField(serverPlayerClass.getSuperclass(), serverPlayer, "containerMenu");
            fetchMethodAndInvoke(container.getClass(), "sendAllDataToRemote", container, new Object[]{}, new Class[]{});
        } else {
            Object container = fetchDeclaredField(serverPlayerClass.getSuperclass(), serverPlayer, "activeContainer");
            fetchMethodAndInvoke(serverPlayerClass, "updateInventory", serverPlayer, new Object[]{container}, new Class[]{container.getClass()});
        }

        fetchMethodAndInvoke(serverPlayerClass, "resetSentInfo", serverPlayer, new Object[]{}, new Class[]{});
        callback.onSuccess();
    }

    @Override
    protected GameProfile getGameProfile0(@NotNull PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException {
        try {
            return (GameProfile) fetchMethodAndInvoke(serverPlayerClass, "getGameProfile", playerObject.player(), new Object[]{}, new Class[]{});
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            return super.getGameProfile0(playerObject);
        }
    }

    private void sendPacket(Object playerConnection, Object packet) throws ReflectionException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        fetchMethodAndInvoke(playerConnection.getClass(), "send", playerConnection, new Object[]{packet}, new Class<?>[]{this.packet});
    }

    private void sendPacketToAll(Object packet) {
        SkinOverlay.getInstance().onlinePlayers().forEach(playerObject -> {
            ServerPlayer spongeServerPlayer = (ServerPlayer) playerObject.player();
            Object serverPlayer = serverPlayerClass.cast(spongeServerPlayer);
            Object playerConnection = getFieldByType(serverPlayer, "ServerGamePacketListenerImpl");
            try {
                sendPacket(playerConnection, packet);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void markDirty(@NotNull Object obj, @NotNull Object dataWatcherObject) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        Object getItem = fetchDeclaredMethodAndInvoke(obj.getClass(), "getItem", obj, new Object[]{dataWatcherObject}, new Class[]{entityDataAccessorClass});
        fetchMethodAndInvoke(getItem.getClass(), "setDirty", getItem, new Object[]{true}, new Class[]{boolean.class});
        setDeclaredFieldValue(obj.getClass(), obj, "isDirty", true);
    }
}
