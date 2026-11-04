package com.georgev22.skinoverlay.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.bxteam.quark.fabric.FabricLibraryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public final class FabricSkinOverlay implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("skinoverlay");

    @Override
    public void onInitialize() {
        File dataFolder = FabricLoader.getInstance().getConfigDir().resolve("skinoverlay").toFile();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            throw new IllegalStateException("Unable to create Fabric data folder: " + dataFolder);
        }

        FabricLibraryManager libraryManager = new FabricLibraryManager(
                "skinoverlay",
                LOGGER
        );

        libraryManager.excludeGroupIds("net.fabricmc", "net.fabricmc.fabric-api", "net.kyori", "org.slf4j");
        libraryManager.loadFromGradle();

        new FabricSkinOverlayRuntime(this, dataFolder, LOGGER).initialize();
    }
}
