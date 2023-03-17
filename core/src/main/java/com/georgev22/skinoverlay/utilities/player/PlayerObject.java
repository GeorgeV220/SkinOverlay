package com.georgev22.skinoverlay.utilities.player;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.UserManager;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.georgev22.skinoverlay.utilities.Updater;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.audience.Audience;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public abstract class PlayerObject {
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    public PlayerObject playerObject() {
        return this;
    }

    public abstract Object player();

    public abstract Audience audience();

    public abstract UUID playerUUID();

    public abstract String playerName();

    public boolean isBedrock() {
        return this.playerUUID().toString().replace("-", "").startsWith("000000");
    }

    public abstract void sendMessage(String input);

    public abstract void sendMessage(List<String> input);

    public abstract void sendMessage(String... input);

    public abstract void sendMessage(String input, ObjectMap<String, String> placeholders, boolean ignoreCase);

    public abstract void sendMessage(List<String> input, ObjectMap<String, String> placeholders, boolean ignoreCase);

    public abstract void sendMessage(String[] input, ObjectMap<String, String> placeholders, boolean ignoreCase);

    public abstract boolean isOnline();

    public abstract boolean permission(String permission);

    public GameProfile gameProfile() {
        try {
            return SkinOverlay.getInstance().getSkinHandler().getGameProfile(playerObject());
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private final List<ObjectMap.Pair<String, UUID>> inform = Lists.newArrayList(
            ObjectMap.Pair.create("GeorgeV22", UUID.fromString("a4f5cd7f-362f-4044-931e-7128b4e6bad9"))
    );

    public void developerInform() {

        final ObjectMap.Pair<String, UUID> pair = ObjectMap.Pair.create(playerName(), playerUUID());

        boolean found = false;

        for (ObjectMap.Pair<String, UUID> loop : this.inform) {
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

    public void playerJoin() {
        if (permission("skinoverlay.updater")) {
            new Updater(this);
        }
        if (OptionsUtil.PROXY.getBooleanValue() & !skinOverlay.type().isProxy()) {
            return;
        }
        CompletableFuture<UserManager.User> future = skinOverlay.getUserManager().getUser(playerUUID());
        future.handleAsync((user, throwable) -> {
            if (throwable != null) {
                skinOverlay.getLogger().log(Level.SEVERE, "Error retrieving user: ", throwable);
                return null;
            }
            try {
                user.addCustomData("defaultSkinProperty", gameProfile().getProperties().get("textures").stream().filter(property -> property.getName().equalsIgnoreCase("textures")).findFirst().orElse(skinOverlay.getSkinHandler().getSkin(playerObject())));
            } catch (IOException | ExecutionException | InterruptedException e) {
                skinOverlay.getLogger().log(Level.SEVERE, "Something went wrong:", e);
                return null;
            }
            try {
                user.addCustomDataIfNotExists("skinOptions", Utilities.skinOptionsToBytes(new SkinOptions("default")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (user.getCustomData("skinProperty") != null && !(user.getCustomData("skinProperty") instanceof Property)) {
                user.addCustomData("skinProperty", Utilities.propertyFromLinkedTreeMap(user.getCustomData("skinProperty")));
            }
            skinOverlay.getUserManager().save(user);
            return user;
        }).handle((user, throwable) -> {
            if (throwable != null) {
                skinOverlay.getLogger().log(Level.SEVERE, "Error: ", throwable);
                return null;
            }
            if (user != null)
                if (!(skinOverlay.type().equals(SkinOverlayImpl.Type.VELOCITY) | skinOverlay.type().equals(SkinOverlayImpl.Type.BUNGEE))) {
                    updateSkin();
                }
            return user;
        });
    }

    public void playerQuit() {
        if (OptionsUtil.PROXY.getBooleanValue() & !skinOverlay.type().isProxy()) {
            return;
        }
        skinOverlay.getUserManager().getUser(playerUUID()).handleAsync((user, throwable) -> {
            if (throwable != null) {
                skinOverlay.getLogger().log(Level.SEVERE, "Error retrieving user: ", throwable);
                return null;
            }
            skinOverlay.getUserManager().save(user);
            return user;
        });
    }

    public void updateSkin() {
        skinOverlay.getUserManager().getUser(playerUUID()).handle((user, throwable) -> {
            if (throwable != null) {
                skinOverlay.getLogger().log(Level.SEVERE, "Error retrieving user: ", throwable);
                return null;
            }
            if (!isOnline())
                return user;
            try {
                if (Utilities.getSkinOptions(user.getCustomData("skinOptions")).getSkinName().equals("default")) {
                    return user;
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Utilities.updateSkin(playerObject(), true);
            return user;
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
