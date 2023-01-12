package com.georgev22.skinoverlay.listeners.bukkit;

import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.UserData;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import javax.imageio.ImageIO;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PlayerListeners implements Listener, PluginMessageListener {
    SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        if (OptionsUtil.PROXY.getBooleanValue()) {
            return;
        }
        final PlayerObject playerObject = new PlayerObject.PlayerObjectWrapper(playerJoinEvent.getPlayer().getUniqueId(), skinOverlay.type()).getPlayerObject();
        final UserData userData = UserData.getUser(playerObject);
        try {
            userData.load(new Utils.Callback<>() {

                public Boolean onSuccess() {
                    UserData.getAllUsersMap().append(userData.user().getUniqueId(), userData.user());
                    SchedulerManager.getScheduler().runTask(SkinOverlay.getInstance().getClass(), () -> {
                        try {
                            userData.setDefaultSkinProperty(skinOverlay.getSkinHandler().getGameProfile(playerObject).getProperties().get("textures").iterator().next());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (userData.getSkinName().equals("default")) {
                            return;
                        }
                        Utilities.updateSkin(playerObject, true, false);
                    });
                    return true;
                }

                @Contract(pure = true)
                public @NotNull Boolean onFailure() {
                    return false;
                }

                public @NotNull Boolean onFailure(@NotNull Throwable throwable) {
                    throwable.printStackTrace();
                    return onFailure();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent playerQuitEvent) {
        if (OptionsUtil.PROXY.getBooleanValue()) {
            return;
        }
        PlayerObject playerObject = new PlayerObject.PlayerObjectWrapper(playerQuitEvent.getPlayer().getUniqueId(), skinOverlay.type()).getPlayerObject();
        final UserData userData = UserData.getUser(playerObject);
        userData.save(true, new Utils.Callback<>() {

            public Boolean onSuccess() {
                UserData.getAllUsersMap().append(userData.user().getUniqueId(), userData.user());
                return true;
            }

            @Contract(pure = true)
            public @NotNull Boolean onFailure() {
                return false;
            }

            public @NotNull Boolean onFailure(@NotNull Throwable throwable) {
                throwable.printStackTrace();
                return onFailure();
            }
        });
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!channel.equalsIgnoreCase("skinoverlay:bungee")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        String uuid = in.readUTF();
        String skinName = in.readUTF();
        PlayerObject playerObject = new PlayerObject.PlayerObjectWrapper(UUID.fromString(Objects.requireNonNull(uuid)), skinOverlay.type()).getPlayerObject();
        if (subChannel.equalsIgnoreCase("change")) {
            Utilities.setSkin(() -> ImageIO.read(new File(skinOverlay.getSkinsDataFolder(), skinName + ".png")), skinName, playerObject, null);
        } else if (subChannel.equalsIgnoreCase("reset")) {
            Utilities.setSkin(() -> null, skinName, playerObject, null);
        } else if (subChannel.equalsIgnoreCase("changeWithProperties")) {
            String name = in.readUTF();
            String value = in.readUTF();
            String signature = in.readUTF();
            Utilities.setSkin(skinName, playerObject, new String[]{name, value, signature});
        } else if (subChannel.equalsIgnoreCase("resetWithProperties")) {
            String name = in.readUTF();
            String value = in.readUTF();
            String signature = in.readUTF();
            Utilities.setSkin(skinName, playerObject, new String[]{name, value, signature});
        }
    }
}

