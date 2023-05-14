package com.georgev22.skinoverlay.config;

import com.georgev22.library.yaml.configmanager.CFG;
import com.georgev22.skinoverlay.SkinOverlay;

public final class FileManager {
    private static FileManager instance;
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();
    private CFG config;

    public static FileManager getInstance() {
        return instance == null ? (instance = new FileManager()) : instance;
    }

    private FileManager() {
    }

    public void loadFiles() throws Exception {
        this.config = new CFG("config", this.skinOverlay.getDataFolder(), true, true, skinOverlay.getLogger(), skinOverlay.getClass());
    }

    public CFG getConfig() {
        return this.config;
    }

}

