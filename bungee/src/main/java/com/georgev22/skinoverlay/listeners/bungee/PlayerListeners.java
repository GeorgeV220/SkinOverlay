package com.georgev22.skinoverlay.listeners.bungee;

import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectBungee;
import com.georgev22.skinoverlay.utilities.player.UserData;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;


public class PlayerListeners implements Listener {
    SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @EventHandler
    public void onLogin(PostLoginEvent postLoginEvent) {
        if (!postLoginEvent.getPlayer().isConnected())
            return;
        final PlayerObject playerObject = new PlayerObjectBungee(postLoginEvent.getPlayer());
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

    @EventHandler
    public void onConnect(ServerConnectedEvent serverConnectedEvent) {
        if (!serverConnectedEvent.getPlayer().isConnected())
            return;
        final PlayerObject playerObject = new PlayerObjectBungee(serverConnectedEvent.getPlayer());
        final UserData userData = UserData.getUser(playerObject);
        if (userData.getSkinName().equals("default")) {
            return;
        }
        Utilities.updateSkin(playerObject, true, false);
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent playerDisconnectEvent) {
        PlayerObject playerObject = new PlayerObjectBungee(playerDisconnectEvent.getPlayer());
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

