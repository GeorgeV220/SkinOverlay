package com.georgev22.skinoverlay.utilities.interfaces;

import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public interface SkinOverlayImpl {

    Type type();

    File dataFolder();

    Logger logger();

    Description description();

    boolean enable(boolean enable);

    boolean enabled();

    void saveResource(@NotNull String resource, boolean replace);

    boolean onlineMode();

    List<PlayerObject> onlinePlayers();

    Object plugin();

    Object serverImpl();

    String serverVersion();

    void print(String... msg);

    record Description(String name, String version, String main, List<String> authors) {
    }

    enum Type {
        BUKKIT,
        BUNGEE,
        VELOCITY,
        SPONGE8,
        SPONGE7;

        public boolean isProxy() {
            return this.equals(VELOCITY) || this.equals(BUNGEE);
        }
    }

}
