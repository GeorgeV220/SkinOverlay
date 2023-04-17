package com.georgev22.skinoverlay;

import com.georgev22.api.libraryloader.LibraryLoader;
import com.georgev22.api.libraryloader.exceptions.InvalidDependencyException;
import com.georgev22.api.libraryloader.exceptions.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;

import static com.georgev22.library.minecraft.BukkitMinecraftUtils.MinecraftVersion.V1_16_R3;
import static com.georgev22.library.minecraft.BukkitMinecraftUtils.MinecraftVersion.getCurrentVersion;

public class SkinOverlayBukkit extends JavaPlugin {

    private SkinOverlayPluginImpl skinOverlayPluginImpl;

    @Override
    public void onLoad() {
        skinOverlayPluginImpl = new SkinOverlayPluginImpl(this, false);
        try {
            if (getCurrentVersion().isBelow(V1_16_R3)) {
                new LibraryLoader(skinOverlayPluginImpl.getClass(), this.getDataFolder()).loadAll(true);
            }
        } catch (InvalidDependencyException | UnknownDependencyException e) {
            throw new RuntimeException(e);
        }
        skinOverlayPluginImpl.onLoad();
    }

    @Override
    public void onEnable() {
        skinOverlayPluginImpl.onEnable();
    }

    @Override
    public void onDisable() {
        skinOverlayPluginImpl.onDisable();
    }
}