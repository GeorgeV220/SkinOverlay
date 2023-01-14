package com.georgev22.skinoverlay.listeners.velocity;

import com.georgev22.library.minecraft.VelocityMinecraftUtils;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.SkinOverlayVelocity;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectWrapper;
import com.georgev22.skinoverlay.utilities.player.UserData;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PlayerListeners {
    SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @Subscribe
    public void onLogin(LoginEvent loginEvent) {
        if (!loginEvent.getPlayer().isActive())
            return;
        final PlayerObject playerObject = new PlayerObjectWrapper(loginEvent.getPlayer().getUsername(), loginEvent.getPlayer().getUniqueId(), SkinOverlay.getInstance().type());
        final UserData userData = UserData.getUser(playerObject);
        try {
            userData.load(new Utils.Callback<>() {
                public Boolean onSuccess() {
                    UserData.getAllUsersMap().append(userData.user().getUniqueId(), userData.user());
                    SchedulerManager.getScheduler().runTask(skinOverlay.getClass(), () -> {
                        try {
                            userData.setDefaultSkinProperty(skinOverlay.getSkinHandler().getGameProfile(playerObject).getProperties().get("textures").iterator().next());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        VelocityMinecraftUtils.printMsg(SkinOverlayVelocity.getInstance().getProxy(), "Player name: "+playerObject.playerName() + "\n Property name: " + userData.getSkinProperty().getName() + "\n value: " + userData.getSkinProperty().getValue() + "\n signature: " + userData.getSkinProperty().getSignature());
                    });
                    return true;
                }

                @Contract(pure = true)
                public @NotNull Boolean onFailure() {
                    return false;
                }

                public @NotNull Boolean onFailure(@NotNull Throwable throwable) {
                    throwable.printStackTrace();
                    return this.onFailure();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public void onChange(ServerConnectedEvent serverConnectedEvent) {
        if (!serverConnectedEvent.getPlayer().isActive())
            return;
        final PlayerObject playerObject = new PlayerObjectWrapper(serverConnectedEvent.getPlayer().getUsername(), serverConnectedEvent.getPlayer().getUniqueId(), SkinOverlay.getInstance().type());
        final UserData userData = UserData.getUser(playerObject);
        if (userData.getSkinName().equals("default")) {
            return;
        }
        Utilities.updateSkin(playerObject, true, false);
    }

    @Subscribe
    public void onQuit(DisconnectEvent playerDisconnectEvent) {
        final PlayerObject playerObject = new PlayerObjectWrapper(playerDisconnectEvent.getPlayer().getUsername(), playerDisconnectEvent.getPlayer().getUniqueId(), SkinOverlay.getInstance().type());
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
                return this.onFailure();
            }
        });
    }
}

