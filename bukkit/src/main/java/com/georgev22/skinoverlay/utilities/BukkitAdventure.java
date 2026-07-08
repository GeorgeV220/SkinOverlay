package com.georgev22.skinoverlay.utilities;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.SkinOverlayBukkit;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

public class BukkitAdventure {

    private static BukkitAudiences bukkitAudiences;

    public static void init() {
        bukkitAudiences = BukkitAudiences.create(SkinOverlayBukkit.getInstance());
        SkinOverlay.getInstance().setConsoleAudience(bukkitAudiences.console());
    }

    public static BukkitAudiences getBukkitAudiences() {
        return bukkitAudiences;
    }

    public static void close() {
        if (bukkitAudiences != null) {
            bukkitAudiences.close();
            bukkitAudiences = null;
        }
    }

}
