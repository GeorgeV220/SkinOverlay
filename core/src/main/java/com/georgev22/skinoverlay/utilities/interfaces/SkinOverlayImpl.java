package com.georgev22.skinoverlay.utilities.interfaces;

import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public interface SkinOverlayImpl {

    boolean isBungee();

    File getDataFolder();

    Logger getLogger();

    Description description();

    boolean setEnable(boolean enable);

    boolean isEnabled();

    void saveResource(@NotNull String resource, boolean replace);

    boolean isOnlineMode();

    List<PlayerObject> onlinePlayers();

    record Description(String name, String version, String main, List<String> authors) {
    }

}
