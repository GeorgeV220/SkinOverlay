package com.georgev22.skinoverlay.utilities.interfaces;

import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public interface SkinOverlayImpl {

    Type type();

    File getDataFolder();

    Logger getLogger();

    Description description();

    boolean setEnable(boolean enable);

    boolean isEnabled();

    void saveResource(@NotNull String resource, boolean replace);

    boolean isOnlineMode();

    List<PlayerObject> onlinePlayers();

    Object getPlugin();

    Object getServerImpl();

    record Description(String name, String version, String main, List<String> authors) {
    }

    enum Type {
        PAPER,
        BUNGEE,
        VELOCITY,
        SPONGE8,
        SPONGE7
    }

}
