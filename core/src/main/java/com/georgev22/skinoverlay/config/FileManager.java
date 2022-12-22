package com.georgev22.skinoverlay.config;

import com.georgev22.library.yaml.configmanager.CFG;
import com.georgev22.skinoverlay.SkinOverlay;

import java.util.logging.Logger;

public final class FileManager {
    private static FileManager instance;
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();
    private CFG config;
    private CFG data;
    private CFG messages;

    public static FileManager getInstance() {
        return instance == null ? (instance = new FileManager()) : instance;
    }

    private FileManager() {
    }

    public void loadFiles(Logger logger, Class<?> clazz) throws Exception {
        this.messages = new CFG("messages", this.skinOverlay.getDataFolder(), false, logger, clazz);
        this.config = new CFG("config", this.skinOverlay.getDataFolder(), true, logger, clazz);
        this.data = new CFG("data", this.skinOverlay.getDataFolder(), true, logger, clazz);
    }

    public CFG getMessages() {
        return this.messages;
    }

    public CFG getConfig() {
        return this.config;
    }

    public CFG getData() {
        return this.data;
    }
}

