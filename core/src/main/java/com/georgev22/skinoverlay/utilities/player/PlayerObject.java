package com.georgev22.skinoverlay.utilities.player;

import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.SkinOverlay;
import com.mojang.authlib.GameProfile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public abstract class PlayerObject {
    public abstract Object getPlayer();

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

    public GameProfile gameProfile() {
        try {
            return SkinOverlay.getInstance().getSkinHandler().getGameProfile(this);
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
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
