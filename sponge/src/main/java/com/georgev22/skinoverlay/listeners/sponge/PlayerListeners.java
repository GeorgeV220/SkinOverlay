package com.georgev22.skinoverlay.listeners.sponge;

import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectWrapper;
import com.georgev22.skinoverlay.utilities.player.UserData;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

import java.io.IOException;

public class PlayerListeners {
    SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @Listener
    public void onLogin(ServerSideConnectionEvent.Login loginEvent) {
        if (!loginEvent.user().isOnline())
            return;
        final PlayerObject playerObject = new PlayerObjectWrapper(loginEvent.user().name(), loginEvent.user().uniqueId(), SkinOverlay.getInstance().type());
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
                    return this.onFailure();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Listener
    public void onLogin(ServerSideConnectionEvent.Disconnect disconnectEvent) {
        final PlayerObject playerObject = new PlayerObjectWrapper(disconnectEvent.player().name(), disconnectEvent.player().uniqueId(), SkinOverlay.getInstance().type());
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

