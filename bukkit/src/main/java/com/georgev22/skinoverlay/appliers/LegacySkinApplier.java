package com.georgev22.skinoverlay.appliers;

import com.georgev22.skinoverlay.exceptions.ReflectionException;
import com.georgev22.skinoverlay.player.SPlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static com.georgev22.skinoverlay.utilities.BukkitMinecraftUtils.MinecraftReflection.getNMSClass;
import static com.georgev22.skinoverlay.utilities.BukkitMinecraftUtils.MinecraftReflection.getOBCClass;
import static com.georgev22.skinoverlay.utilities.Utils.Reflection.*;

@SuppressWarnings("deprecation")
public class LegacySkinApplier extends SkinApplier {

    private final Class<?> playOutRespawn;
    private final Class<?> playOutPlayerInfo;
    private final Class<?> playOutPosition;
    private final Class<?> packet;
    private final Class<?> playOutHeldItemSlot;
    private final Method getHandleMethod;
    private Enum<?> removePlayerEnum;
    private Enum<?> addPlayerEnum;

    public LegacySkinApplier() {
        try {
            packet = getNMSClass("Packet", "net.minecraft.network.protocol.Packet");
            playOutHeldItemSlot = getNMSClass("PacketPlayOutHeldItemSlot", "net.minecraft.network.protocol.game.PacketPlayOutHeldItemSlot");
            playOutPosition = getNMSClass("PacketPlayOutPosition", "net.minecraft.network.protocol.game.PacketPlayOutPosition");
            playOutPlayerInfo = getNMSClass("PacketPlayOutPlayerInfo", "net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo");
            playOutRespawn = getNMSClass("PacketPlayOutRespawn", "net.minecraft.network.protocol.game.PacketPlayOutRespawn");

            try {
                removePlayerEnum = getEnum(playOutPlayerInfo, "EnumPlayerInfoAction", "REMOVE_PLAYER");
                addPlayerEnum = getEnum(playOutPlayerInfo, "EnumPlayerInfoAction", "ADD_PLAYER");
            } catch (Exception e1) {
                try {
                    Class<?> enumPlayerInfoActionClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo$EnumPlayerInfoAction");

                    removePlayerEnum = getEnum(enumPlayerInfoActionClass, 4);
                    addPlayerEnum = getEnum(enumPlayerInfoActionClass, 0);
                } catch (Exception e2) {
                    try {
                        removePlayerEnum = getEnum(playOutPlayerInfo, "Action", "REMOVE_PLAYER");
                        addPlayerEnum = getEnum(playOutPlayerInfo, "Action", "ADD_PLAYER");
                    } catch (Exception e3) {
                        try {
                            Class<?> enumPlayerInfoAction = getNMSClass("EnumPlayerInfoAction", null);

                            removePlayerEnum = getEnum(enumPlayerInfoAction, "REMOVE_PLAYER");
                            addPlayerEnum = getEnum(enumPlayerInfoAction, "ADD_PLAYER");
                        } catch (Exception e4) {
                            //ignore
                        }
                    }
                }
            }

            getHandleMethod = getOBCClass("entity.CraftPlayer").getDeclaredMethod("getHandle");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void applySkin(@NotNull SPlayer player) {
        this.skinOverlay.getScheduler().createDelayedTask(this.skinOverlay.getPlugin(), () -> {
            Player bukkitPlayer = player.getPlayer();
            bukkitPlayer.hidePlayer(bukkitPlayer);
            bukkitPlayer.showPlayer(bukkitPlayer);
            skinOverlay.getSkinApplier().sendPackets(player).handleAsync((result, throwable) -> {
                if (throwable != null) {
                    skinOverlay.getLogger().log(Level.SEVERE, "Error updating skin", throwable);
                    return false;
                }
                return result;
            }).thenAccept(result -> this.skinOverlay.getScheduler().runTask(this.skinOverlay.getPlugin(), () -> {
                if (result)
                    skinOverlay.getPlayerProvider().getOnlinePlayers().stream().filter(online -> online != player).forEach(online -> {
                        Player p = online.getPlayer();
                        p.hidePlayer(bukkitPlayer);
                        p.showPlayer(bukkitPlayer);
                    });
            }));
        }, 20L);
    }

    private void sendPacket(Object playerConnection, Object packet) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        fetchMethodAndInvoke(playerConnection.getClass(), "sendPacket", playerConnection, new Object[]{packet}, new Class<?>[]{this.packet});
    }

    @Contract("_ -> new")
    protected @NotNull CompletableFuture<Boolean> sendPackets(@NotNull SPlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Player bukkitPlayer = player.getPlayer();

                final Object entityPlayer = getHandleMethod.invoke(player);
                Object removePlayer;
                Object addPlayer;
                try {
                    removePlayer = invokeConstructor(playOutPlayerInfo, removePlayerEnum, ImmutableList.of(entityPlayer));
                    addPlayer = invokeConstructor(playOutPlayerInfo, addPlayerEnum, ImmutableList.of(entityPlayer));
                } catch (ReflectionException e) {
                    int ping = (int) fetchField(entityPlayer.getClass(), entityPlayer, "ping");
                    removePlayer = invokeConstructor(playOutPlayerInfo, bukkitPlayer.getPlayerListName(), false, 9999);
                    addPlayer = invokeConstructor(playOutPlayerInfo, bukkitPlayer.getPlayerListName(), true, ping);
                }

                Object world;
                try {
                    world = fetchMethodAndInvoke(entityPlayer.getClass(), "getWorld", entityPlayer, new Object[0], new Class[0]);
                } catch (Exception ignore) {
                    world = fetchMethodAndInvoke(entityPlayer.getClass(), "getWorldServer", entityPlayer, new Object[0], new Class[0]);
                }
                Object difficulty;
                try {
                    difficulty = fetchMethodAndInvoke(world.getClass(), "getDifficulty", world, new Object[0], new Class[0]);
                } catch (Exception e) {
                    try {
                        difficulty = fetchField(world.getClass(), world, "difficulty");
                    } catch (NoSuchFieldException ignore) {
                        skinOverlay.getLogger().info(world.getClass().getSimpleName() + " does not have getDifficulty method or difficulty field!!");
                        difficulty = null;
                    }

                }

                Object worldData = null;
                try {
                    worldData = fetchMethodAndInvoke(world.getClass(), "getWorldData", world, new Object[0], new Class[0]);
                } catch (Exception ignored) {
                    try {
                        worldData = fetchField(world.getClass(), world, "worldData");
                    } catch (Exception ignored2) {
                        skinOverlay.getLogger().info(world.getClass().getSimpleName() + " does not have getWorldData method or worldData field!!");
                    }
                }

                Object worldType = null;
                try {
                    worldType = fetchMethodAndInvoke(Objects.requireNonNull(worldData).getClass(), "getType", worldData, new Object[0], new Class[0]);
                } catch (Exception ignored) {
                    try {
                        worldType = fetchMethodAndInvoke(Objects.requireNonNull(worldData).getClass(), "getGameType", worldData, new Object[0], new Class[0]);
                    } catch (Exception ignored2) {
                    }
                }

                Object playerIntManager = getFieldByType(entityPlayer, "PlayerInteractManager");
                Enum<?> enumGamemode = (Enum<?>) fetchMethodAndInvoke(playerIntManager.getClass(), "getGameMode", playerIntManager, new Object[0], new Class[0]);

                //noinspection UnstableApiUsage
                int dimension = bukkitPlayer.getWorld().getEnvironment().getId();

                Object respawn;
                try {
                    respawn = invokeConstructor(playOutRespawn, dimension, difficulty, worldType, enumGamemode);
                } catch (Exception ignored) {
                    Object worldObject = getFieldByType(entityPlayer, "World");
                    Object dimensionManager = null;
                    try {
                        dimensionManager = getFieldByType(worldObject, "DimensionManager");
                    } catch (ReflectionException e) {
                        try {
                            Class<?> dimensionManagerClass = getNMSClass("DimensionManager", "net.minecraft.world.level.dimension.DimensionManager");

                            for (Method m : dimensionManagerClass.getDeclaredMethods()) {
                                if (m.getReturnType() == dimensionManagerClass && m.getParameterCount() == 1 && m.getParameterTypes()[0] == Integer.TYPE) {
                                    m.setAccessible(true);
                                    dimensionManager = m.invoke(null, dimension);
                                }
                            }
                        } catch (InvocationTargetException | IllegalAccessException | ClassNotFoundException e2) {
                            throw new RuntimeException(e2);
                        }
                    }

                    if (dimensionManager == null) {
                        throw new ReflectionException("Could not get DimensionManager from " + worldObject.getClass().getSimpleName());
                    }

                    try {
                        respawn = invokeConstructor(playOutRespawn, dimensionManager, difficulty, worldType, enumGamemode);
                    } catch (Exception ignored2) {
                        try {
                            respawn = invokeConstructor(playOutRespawn, dimensionManager, worldType, enumGamemode);
                        } catch (Exception ignored3) {
                            long seedEncrypted = Hashing.sha256().hashString(String.valueOf(bukkitPlayer.getWorld().getSeed()), StandardCharsets.UTF_8).asLong();
                            try {
                                respawn = invokeConstructor(playOutRespawn, dimensionManager, seedEncrypted, worldType, enumGamemode);
                            } catch (Exception ignored5) {
                                Object dimensionKey = fetchMethodAndInvoke(worldObject.getClass(), "getDimensionKey", worldObject, new Object[0], new Class[0]);
                                boolean debug = (boolean) fetchMethodAndInvoke(worldObject.getClass(), "isDebugWorld", worldObject, new Object[0], new Class[0]);
                                boolean flat = (boolean) fetchMethodAndInvoke(worldObject.getClass(), "isFlatWorld", worldObject, new Object[0], new Class[0]);
                                List<Object> gameModeList = getFieldByTypeList(playerIntManager, "EnumGamemode");

                                Enum<?> enumGamemodePrevious = null;
                                for (Object obj : gameModeList) {
                                    if (obj != enumGamemode)
                                        enumGamemodePrevious = (Enum<?>) obj;
                                }

                                try {
                                    Object typeKey = fetchMethodAndInvoke(worldObject.getClass(), "getTypeKey", worldObject, new Object[0], new Class[0]);

                                    respawn = invokeConstructor(playOutRespawn, typeKey, dimensionKey, seedEncrypted, enumGamemode, enumGamemodePrevious, debug, flat, true);
                                } catch (Exception ignored6) {
                                    respawn = invokeConstructor(playOutRespawn, dimensionManager, dimensionKey, seedEncrypted, enumGamemode, enumGamemodePrevious, debug, flat, true);
                                }
                            }
                        }
                    }
                }

                Location l = bukkitPlayer.getLocation();
                Object pos;
                try {
                    pos = invokeConstructor(playOutPosition, l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), new HashSet<Enum<?>>(), 0);
                } catch (Exception e1) {
                    try {
                        pos = invokeConstructor(playOutPosition, l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), new HashSet<Enum<?>>());
                    } catch (Exception e3) {
                        pos = invokeConstructor(playOutPosition, l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), false);
                    }
                }

                Object slot = invokeConstructor(playOutHeldItemSlot, bukkitPlayer.getInventory().getHeldItemSlot());
                Object playerConnection = getFieldByType(entityPlayer, "PlayerConnection");

                sendPacket(playerConnection, removePlayer);
                sendPacket(playerConnection, addPlayer);

                sendPacket(playerConnection, respawn);

                fetchMethodAndInvoke(entityPlayer.getClass(), "updateAbilities", entityPlayer, new Object[0], new Class[0]);

                sendPacket(playerConnection, pos);
                sendPacket(playerConnection, slot);

                fetchMethodAndInvoke(player.getClass(), "updateScaledHealth", player, new Object[0], new Class[0]);
                bukkitPlayer.updateInventory();
                fetchMethodAndInvoke(entityPlayer.getClass(), "triggerHealthUpdate", entityPlayer, new Object[0], new Class[0]);

                if (player.isOp()) {
                    this.skinOverlay.getScheduler().runTask(this.skinOverlay.getPlugin(), () -> {
                        bukkitPlayer.setOp(false);
                        bukkitPlayer.setOp(true);
                    });
                }
                return true;
            } catch (ReflectionException | InvocationTargetException | IllegalAccessException | NoSuchMethodException |
                     NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }, runnable -> this.skinOverlay.getScheduler().runTask(this.skinOverlay.getPlugin(), runnable));
    }


}
