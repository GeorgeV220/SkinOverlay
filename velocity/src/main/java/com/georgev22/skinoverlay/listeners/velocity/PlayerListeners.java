package com.georgev22.skinoverlay.listeners.velocity;

import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectVelocity;
import com.georgev22.skinoverlay.utilities.player.UserData;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class PlayerListeners {
    SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @Subscribe
    public void onLogin(PostLoginEvent loginEvent) {
        if (!loginEvent.getPlayer().isActive())
            return;
        final PlayerObject playerObject = new PlayerObjectVelocity(loginEvent.getPlayer());
        final UserData userData = UserData.getUser(playerObject);
        try {
            userData.load(new Utils.Callback<>() {
                public Boolean onSuccess() {
                    UserData.getAllUsersMap().append(userData.user().getUniqueId(), userData.user());
                    SchedulerManager.getScheduler().runTask(skinOverlay.getClass(), () -> {
                        userData.user().append("playerObject", Optional.of(playerObject));
                        try {
                            userData.setDefaultSkinProperty(playerObject.gameProfile().getProperties().get("textures").stream().filter(property -> property.getName().equalsIgnoreCase("textures")).findFirst().orElse(skinOverlay.getSkinHandler().getSkin(playerObject)));
                        } catch (IOException | ExecutionException | InterruptedException e) {
                            skinOverlay.getLogger().log(Level.SEVERE, "Something went wrong:", e);
                        }
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
        final PlayerObject playerObject = new PlayerObjectVelocity(serverConnectedEvent.getPlayer());
        final UserData userData = UserData.getUser(playerObject);
        if (userData.getSkinName().equals("default")) {
            return;
        }
        Utilities.updateSkin(playerObject, true, false);
    }

    @Subscribe
    public void onQuit(DisconnectEvent playerDisconnectEvent) {
        final PlayerObject playerObject = new PlayerObjectVelocity(playerDisconnectEvent.getPlayer());
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

