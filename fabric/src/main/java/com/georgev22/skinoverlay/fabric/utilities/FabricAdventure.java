package com.georgev22.skinoverlay.fabric.utilities;

import com.georgev22.skinoverlay.SkinOverlay;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.server.MinecraftServer;

public class FabricAdventure {

    private static MinecraftServerAudiences fabricAudiences;

    public static void init(MinecraftServer server) {
        fabricAudiences = MinecraftServerAudiences.of(server);
        SkinOverlay.getInstance().setConsoleAudience(fabricAudiences.console());
    }

    public static MinecraftServerAudiences getFabricAudiences() {
        if (fabricAudiences == null) {
            throw new IllegalStateException("Tried to access Adventure without a running server!");
        }
        return fabricAudiences;
    }

    public static void close() {
        if (fabricAudiences != null) {
            fabricAudiences.close();
            fabricAudiences = null;
        }
    }

}
