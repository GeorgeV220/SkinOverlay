package com.georgev22.skinoverlay.handler.handlers;

import com.georgev22.library.exceptions.ReflectionException;
import com.georgev22.library.utilities.Utils;
import com.georgev22.library.yaml.file.FileConfiguration;
import com.georgev22.skinoverlay.SkinOverlaySponge;
import com.georgev22.skinoverlay.handler.SkinHandler.SkinHandler_;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.UserData;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import static com.georgev22.library.utilities.Utils.Reflection.*;

public class SkinHandler_Sponge extends SkinHandler_ {


    private final ClassLoader classLoader;
    private final Class<?> serverPlayerClass;
    private final Class<?> serverWorldClass;
    private final Class<?> respawnPacketClass;
    private final Class<?> entityDataPacketClass;
    private final Class<?> entityDataSerializersClass;
    private final Class<?> entityDataAccesorClass;

    private final Class<?> packet;

    public SkinHandler_Sponge() {
        classLoader = SkinOverlaySponge.getInstance().getServerImpl().getClass().getClassLoader();
        serverPlayerClass = Utils.Reflection.getClass("net.minecraft.server.level.ServerPlayer", classLoader);
        serverWorldClass = Utils.Reflection.getClass("net.minecraft.server.level.ServerLevel", classLoader);
        respawnPacketClass = Utils.Reflection.getClass("net.minecraft.network.protocol.game.ClientboundRespawnPacket", classLoader);
        entityDataPacketClass = Utils.Reflection.getClass("net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket", classLoader);
        entityDataSerializersClass = Utils.Reflection.getClass("net.minecraft.network.syncher.EntityDataSerializers", classLoader);
        entityDataAccesorClass = Utils.Reflection.getClass("net.minecraft.network.syncher.EntityDataAccessor", classLoader);
        packet = Utils.Reflection.getClass("net.minecraft.network.protocol.Packet", classLoader);
    }

    @SneakyThrows
    @Override
    public void updateSkin(@NotNull FileConfiguration fileConfiguration, @NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName) {
        Property property = UserData.getUser(playerObject.playerUUID()).getSkinProperty();

        ServerPlayer receiver = (ServerPlayer) playerObject.getPlayer();

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

        Object respawnPacket;
        //Respawn packet
        try {
            Object deathLocation = fetchMethodAndInvoke(serverPlayerClass, "getLastDeathLocation", serverPlayer, new Object[]{}, new Class[]{});

            switch (Sponge.platform().minecraftVersion().name()) {
                case "1.19.3" -> respawnPacket = invokeConstructor(
                        respawnPacketClass,
                        dimensionType,
                        dimension,
                        seedEncrypted,
                        gameModeForPlayer,
                        previousGameModeForPlayer,
                        fetchMethodAndInvoke(serverWorldClass, "isDebug", serverWorld, new Object[]{}, new Class[]{}),
                        fetchMethodAndInvoke(serverWorldClass, "isFlat", serverWorld, new Object[]{}, new Class[]{}),
                        (byte) 3,
                        deathLocation
                );
                case "1.19.2" -> respawnPacket = invokeConstructor(
                        respawnPacketClass,
                        dimensionType,
                        dimension,
                        seedEncrypted,
                        gameModeForPlayer,
                        previousGameModeForPlayer,
                        fetchMethodAndInvoke(serverWorldClass, "isDebug", serverWorld, new Object[]{}, new Class[]{}),
                        fetchMethodAndInvoke(serverWorldClass, "isFlat", serverWorld, new Object[]{}, new Class[]{}),
                        true,
                        deathLocation
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
            exception.printStackTrace();
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
                                    entityDataAccesorClass,
                                    17,
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
                                    entityDataAccesorClass,
                                    17,
                                    fetchField(entityDataSerializersClass, null, "BYTE")),
                            (byte)
                                    ((fileConfiguration.getBoolean("Options.overlays." + skinName + ".cape", false) ? 0x01 : 0x00) |
                                            (fileConfiguration.getBoolean("Options.overlays." + skinName + ".jacket", false) ? 0x02 : 0x00) |
                                            (fileConfiguration.getBoolean("Options.overlays." + skinName + ".left_sleeve", false) ? 0x04 : 0x00) |
                                            (fileConfiguration.getBoolean("Options.overlays." + skinName + ".right_sleeve", false) ? 0x08 : 0x00) |
                                            (fileConfiguration.getBoolean("Options.overlays." + skinName + ".left_pants", false) ? 0x10 : 0x00) |
                                            (fileConfiguration.getBoolean("Options.overlays." + skinName + ".right_pants", false) ? 0x20 : 0x00) |
                                            (fileConfiguration.getBoolean("Options.overlays." + skinName + ".hat", false) ? 0x40 : 0x00))
                    },
                    new Class[]{
                            entityDataAccessor.getClass(),
                            Object.class
                    });
        }
        if (Sponge.platform().minecraftVersion().name().equals("1.19.3")) {
            markDirty(synchedEntityData, entityDataAccessor);
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

        receiver.tabList().removeEntry(receiver.uniqueId());
        receiver.tabList().addEntry(TabListEntry.builder()
                .displayName(receiver.displayName().get())
                .latency(receiver.connection().latency())
                .list(receiver.tabList())
                .gameMode(receiver.gameMode().get())
                .profile(receiver.profile())
                .build());

        ServerLocation serverLocation = receiver.serverLocation();
        Vector3d rotation = receiver.rotation();

        Object playerConnection = getFieldByType(serverPlayer, "ServerGamePacketListenerImpl");
        sendPacket(playerConnection, respawnPacket);
        sendPacket(playerConnection, entityDataPacket);

        Object playerPositionPacket;
        Object playerCarriedItemPacket;
        try {
            Class<?> playerPositionPacketClass = Utils.Reflection.getClass("net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket", classLoader);
            Class<?> playerCarriedItemPacketClass = Utils.Reflection.getClass("net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket", classLoader);

            playerPositionPacket = invokeConstructor(playerPositionPacketClass,
                    serverLocation.x(),
                    serverLocation.y(),
                    serverLocation.z(),
                    (float) rotation.y(),
                    (float) rotation.x(),
                    new HashSet<>(),
                    0,
                    false);
            playerCarriedItemPacket = invokeConstructor(playerCarriedItemPacketClass,
                    receiver.inventory().hotbar().selectedSlotIndex()
            );
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }

        /*receiver.offer(Keys.VANISH_STATE, VanishState.vanished());
        SchedulerManager.getScheduler().runTaskLater(skinOverlay.getClass(), () -> receiver.offer(Keys.VANISH_STATE, VanishState.unvanished()), 1L);*/
        sendPacket(playerConnection, playerPositionPacket);
        sendPacket(playerConnection, playerCarriedItemPacket);

        fetchMethodAndInvoke(serverPlayerClass, "resetSentInfo", serverPlayer, new Object[]{}, new Class[]{});
    }

    @Override
    public void updateSkin(@NotNull FileConfiguration fileConfiguration, @NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName, Property property) {
        updateSkin(fileConfiguration, playerObject, reset, skinName);
    }

    @Override
    protected GameProfile getGameProfile0(@NotNull PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException {
        try {
            return (GameProfile) fetchMethodAndInvoke(serverPlayerClass, "getGameProfile", playerObject.getPlayer(), new Object[]{}, new Class[]{});
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            return super.getGameProfile0(playerObject);
        }
    }

    private void sendPacket(Object playerConnection, Object packet) throws ReflectionException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        fetchMethodAndInvoke(playerConnection.getClass(), "send", playerConnection, new Object[]{packet}, new Class<?>[]{this.packet});
    }

    private void markDirty(@NotNull Object obj, @NotNull Object datawatcherobject) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        Object getItem = fetchDeclaredMethodAndInvoke(obj.getClass(), "getItem", obj, new Object[]{datawatcherobject}, new Class[]{datawatcherobject.getClass()});
        fetchMethodAndInvoke(getItem.getClass(), "setDirty", getItem, new Object[]{true}, new Class[]{boolean.class});
        setDeclaredFieldValue(obj.getClass(), obj, "isDirty", true);
    }
}
