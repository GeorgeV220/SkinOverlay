package com.georgev22.skinoverlay.utilities.player;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.maps.ObjectMap.Pair;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.UserManager;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.event.events.player.PlayerObjectUserEvent;
import com.georgev22.skinoverlay.event.events.player.skin.PlayerObjectPreUpdateSkinEvent;
import com.georgev22.skinoverlay.event.events.user.data.UserModifyDataEvent;
import com.georgev22.skinoverlay.event.events.user.data.load.UserPostLoadEvent;
import com.georgev22.skinoverlay.event.events.user.data.add.UserAddDataEvent;
import com.georgev22.skinoverlay.event.events.user.data.load.UserPreLoadEvent;
import com.georgev22.skinoverlay.exceptions.UserException;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.georgev22.skinoverlay.utilities.Updater;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.google.common.collect.Lists;
import net.kyori.adventure.audience.Audience;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

/**
 * The PlayerObject class represents a player in the game.
 */
public abstract class PlayerObject {
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    /**
     * Returns the PlayerObject instance.
     *
     * @return The PlayerObject instance.
     */
    public PlayerObject playerObject() {
        return this;
    }

    /**
     * Returns the player object.
     *
     * @return The player object.
     */
    public abstract Object player();

    /**
     * Returns the audience of the player.
     *
     * @return The audience of the player.
     */
    public abstract Audience audience();

    /**
     * Returns the UUID of the player.
     *
     * @return The UUID of the player.
     */
    public abstract UUID playerUUID();

    /**
     * Returns the name of the player.
     *
     * @return The name of the player.
     */
    public abstract String playerName();

    /**
     * Checks if the player is using Bedrock Edition.
     *
     * @return true if the player is using Bedrock Edition, false otherwise.
     */
    public boolean isBedrock() {
        return this.playerUUID().toString().replace("-", "").startsWith("000000");
    }

    /**
     * Sends a message to the player.
     *
     * @param input The message to be sent.
     */
    public abstract void sendMessage(String input);

    /**
     * Sends a list of messages to the player.
     *
     * @param input The list of messages to be sent.
     */
    public abstract void sendMessage(List<String> input);

    /**
     * Sends an array of messages to the player.
     *
     * @param input The array of messages to be sent.
     */
    public abstract void sendMessage(String... input);

    /**
     * Sends a message to the player with placeholders.
     *
     * @param input        The message to be sent.
     * @param placeholders The map of placeholders and their values.
     * @param ignoreCase   Whether to ignore the case of the placeholders.
     */
    public abstract void sendMessage(String input, ObjectMap<String, String> placeholders, boolean ignoreCase);

    /**
     * Sends a list of messages to the player with placeholders.
     *
     * @param input        The list of messages to be sent.
     * @param placeholders The map of placeholders and their values.
     * @param ignoreCase   Whether to ignore the case of the placeholders.
     */
    public abstract void sendMessage(List<String> input, ObjectMap<String, String> placeholders, boolean ignoreCase);

    /**
     * Sends an array of messages to the player with placeholders.
     *
     * @param input        The array of messages to be sent.
     * @param placeholders The map of placeholders and their values.
     * @param ignoreCase   Whether to ignore the case of the placeholders.
     */
    public abstract void sendMessage(String[] input, ObjectMap<String, String> placeholders, boolean ignoreCase);

    /**
     * Checks if the player is online.
     *
     * @return true if the player is online, false otherwise.
     */
    public abstract boolean isOnline();

    /**
     * Checks if the player has a permission.
     *
     * @param permission The permission to be checked.
     * @return true if the player has the permission, false otherwise.
     */
    public abstract boolean permission(String permission);

    /**
     * Returns the game profile associated with this player object.
     *
     * @return the game profile associated with this player object
     * @throws RuntimeException if there is an error retrieving the game profile
     */
    public SGameProfile gameProfile() {
        try {
            return SkinOverlay.getInstance().getSkinHandler().getGameProfile(playerObject());
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the internal game profile object associated with this player object.
     *
     * @return the internal game profile object associated with this player object
     * @throws RuntimeException if there is an error retrieving the game profile
     */
    public Object internalGameProfile() {
        try {
            return SkinOverlay.getInstance().getSkinHandler().getGameProfile0(playerObject());
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private final List<Pair<String, UUID>> inform = Lists.newArrayList(
            Pair.create("GeorgeV22", UUID.fromString("a4f5cd7f-362f-4044-931e-7128b4e6bad9"))
    );

    /**
     * Checks if a player is registered to receive developer information.
     */
    public void developerInform() {

        final Pair<String, UUID> pair = Pair.create(playerName(), playerUUID());

        boolean found = false;

        for (Pair<String, UUID> loop : this.inform) {
            if (loop.key().equals(pair.key())) {
                found = true;
                break;
            }
            if (loop.value().equals(pair.value())) {
                found = true;
                break;
            }
        }

        if (!found) {
            return;
        }

        SchedulerManager.getScheduler().runTaskLater(skinOverlay.getClass(), () -> {
            if (!isOnline() && player() == null) {
                return;
            }

            sendMessage(Lists.newArrayList(

                    "",

                    "",

                    "&7Hey &f%player%&7, details are listed below.",

                    "&7Version: &c%version%",

                    "&7Java Version: &c%javaversion%",

                    "&7Server Version: &c%serverversion%",

                    "&7Name: &c%name%",

                    "&7Author: &c%author%",

                    "&7Main package: &c%package%",

                    "&7Main path: &c%main%",

                    "&7Experimental Features: &c" + OptionsUtil.EXPERIMENTAL_FEATURES.getBooleanValue(),

                    ""

            ), new HashObjectMap<String, String>()
                    .append("%player%", playerName())
                    .append("%version%", skinOverlay.getDescription().version())
                    .append("%package%", skinOverlay.getClass().getPackage().getName())
                    .append("%name%", skinOverlay.getDescription().name())
                    .append("%author%", String.join(", ", skinOverlay.getDescription().authors()))
                    .append("%main%", skinOverlay.getDescription().main())
                    .append("%javaversion%", System.getProperty("java.version"))
                    .append("%serverversion%", skinOverlay.getSkinOverlay().serverVersion()), false);
        }, 20L * 10L);
    }

    /**
     * Called when a player joins the server.
     * If the player has the "skinoverlay.updater" permission, a new Updater instance is created.
     * If the server is not a proxy server and the "proxy" option is set to true, the method does not continue.
     * Retrieves the User object associated with the player's UUID from the UserManager and asynchronously handles it.
     * Adds custom data to the user object, saves it, and updates the player's skin.
     */
    public void playerJoin() {
        new Updater(this);
        if (!skinOverlay.getSkinOverlay().type().isProxy() && OptionsUtil.PROXY.getBooleanValue()) {
            return;
        }
        UserPreLoadEvent userPreLoadEvent = (UserPreLoadEvent) skinOverlay.getEventManager()
                .callEvent(new UserPreLoadEvent(this.playerUUID(), true));
        if (userPreLoadEvent.isCancelled()) {
            return;
        }
        CompletableFuture<UserManager.User> future = skinOverlay.getUserManager().getUser(playerUUID());
        future.handleAsync((user, throwable) -> {
            if (throwable != null) {
                skinOverlay.getLogger().log(Level.SEVERE, "Error retrieving user: ", throwable);
                return null;
            }
            if (user == null) {
                throw new UserException("User not found!");
            }
            UserModifyDataEvent userModifyDataEvent = (UserModifyDataEvent) skinOverlay.getEventManager()
                    .callEvent(new UserModifyDataEvent(user, true));
            if (userModifyDataEvent.isCancelled()) {
                return user;
            }
            try {
                UserAddDataEvent event = (UserAddDataEvent) skinOverlay.getEventManager()
                        .callEvent(new UserAddDataEvent(
                                user,
                                Pair.create(
                                        "defaultSkinProperty",
                                        gameProfile().getProperties().get("textures") != null
                                                ? gameProfile().getProperties().get("textures")
                                                : skinOverlay.getSkinHandler().getSkin(playerObject())
                                ),
                                true));
                if (!event.isCancelled()) {
                    user.addCustomData(event.getData().key(), event.getData().value());
                }
            } catch (IOException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                UserAddDataEvent event = (UserAddDataEvent) skinOverlay.getEventManager()
                        .callEvent(new UserAddDataEvent(
                                user,
                                Pair.create(
                                        "skinOptions",
                                        Utilities.skinOptionsToBytes(new SkinOptions("default"))
                                ),
                                true)
                        );
                if (!event.isCancelled()) {
                    user.addCustomDataIfNotExists(event.getData().key(), event.getData().value());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            userModifyDataEvent = (UserModifyDataEvent) skinOverlay.getEventManager()
                    .callEvent(new UserModifyDataEvent(user, true));
            if (!userModifyDataEvent.isCancelled()) {
                skinOverlay.getUserManager().save(user);
            }
            return user;
        }).handleAsync((user, throwable) -> {
            if (throwable != null) {
                skinOverlay.getLogger().log(Level.SEVERE, "Error: ", throwable);
                return null;
            }
            return user;
        }).thenAcceptAsync(user -> {
            if (user != null) {
                PlayerObjectUserEvent playerObjectUserEvent = (PlayerObjectUserEvent) skinOverlay.getEventManager()
                        .callEvent(new PlayerObjectUserEvent(playerObject(), user, true));
                if (playerObjectUserEvent.isCancelled())
                    return;
                UserPostLoadEvent userPostLoadEvent = (UserPostLoadEvent) skinOverlay.getEventManager()
                        .callEvent(new UserPostLoadEvent(user, true));
                if (userPostLoadEvent.isCancelled()) {
                    return;
                }
                if (!OptionsUtil.PROXY.getBooleanValue())
                    updateSkin();
            }
        });
    }

    /**
     * Called when a player quits the server.
     * If the server is not a proxy server and the "proxy" option is set to true, the method does nothing.
     * Retrieves the User object associated with the player's UUID from the UserManager and asynchronously handles it.
     * Saves the user object.
     */
    public void playerQuit() {
        skinOverlay.getSkinOverlay().onlinePlayers().remove(playerUUID());
        if (!skinOverlay.getSkinOverlay().type().isProxy() && OptionsUtil.PROXY.getBooleanValue()) {
            return;
        }
        skinOverlay.getUserManager().getUser(playerUUID()).handleAsync((user, throwable) -> {
            if (throwable != null) {
                skinOverlay.getLogger().log(Level.SEVERE, "Error retrieving user: ", throwable);
                return null;
            }
            return user;
        }).thenAcceptAsync(user -> {
            if (user != null) {
                UserModifyDataEvent userModifyDataEvent = (UserModifyDataEvent) skinOverlay.getEventManager()
                        .callEvent(new UserModifyDataEvent(user, true));
                if (!userModifyDataEvent.isCancelled()) {
                    skinOverlay.getUserManager().save(user);
                }
            }
        });
    }

    /**
     * Updates the player's skin.
     * Retrieves the User object associated with the player's UUID from the UserManager and asynchronously handles it.
     * If the player is not online or their skin options have the default skin name, no further action is taken.
     * Updates the player's skin using the SkinHandler.
     */
    public void updateSkin() {
        skinOverlay.getUserManager().getUser(playerUUID()).handleAsync((user, throwable) -> {
            if (throwable != null) {
                skinOverlay.getLogger().log(Level.SEVERE, "Error retrieving user: ", throwable);
                return null;
            }
            if (!isOnline())
                return user;
            return user;
        }).thenAcceptAsync(user -> {
            if (user != null) {
                PlayerObjectPreUpdateSkinEvent event = (PlayerObjectPreUpdateSkinEvent) skinOverlay.getEventManager()
                        .callEvent(new PlayerObjectPreUpdateSkinEvent(this, user, true));
                if (event.isCancelled())
                    return;
                try {
                    SkinOptions skinOptions = Utilities.getSkinOptions(user.getCustomData("skinOptions"));
                    if (skinOptions == null)
                        return;
                    if (skinOptions.getSkinName().equals("default"))
                        return;
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                skinOverlay.getSkinHandler().updateSkin(event.getPlayerObject(), true);
            }
        }).handleAsync((unused, throwable) -> {
            if (throwable != null) {
                skinOverlay.getLogger().log(Level.SEVERE, "Error: ", throwable);
                return null;
            }
            return unused;
        });
    }


    @Override
    public String toString() {
        return "PlayerObject{\n" +
                "playerName: " + playerName() + "\n" +
                "playerUUID: " + playerUUID() + "\n" +
                "isBedrock: " + isBedrock() + "\n" +
                "isOnline: " + isOnline() + "\n" +
                "gameProfile: " + gameProfile().toString() + "\n" +
                "}";
    }
}
