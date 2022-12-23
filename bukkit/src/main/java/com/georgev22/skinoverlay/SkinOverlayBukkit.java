package com.georgev22.skinoverlay;

import co.aikar.commands.PaperCommandManager;
import com.georgev22.library.minecraft.BukkitMinecraftUtils;
import com.georgev22.library.minecraft.BungeeMinecraftUtils;
import com.georgev22.library.yaml.file.FileConfiguration;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.handler.SkinHandler.SkinHandler_Unsupported;
import com.georgev22.skinoverlay.handler.handlers.*;
import com.georgev22.skinoverlay.listeners.bukkit.DeveloperInformListener;
import com.georgev22.skinoverlay.listeners.bukkit.PlayerListeners;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectBukkit;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static com.georgev22.library.minecraft.BukkitMinecraftUtils.MinecraftVersion.*;

public class SkinOverlayBukkit extends JavaPlugin implements SkinOverlayImpl {


    @Override
    public void onLoad() {
        SkinOverlay.getInstance().onLoad(this, new PaperCommandManager(this));
        Bukkit.getScheduler().runTaskTimer(this, () -> SkinOverlay.getInstance().getScheduler().mainThreadHeartbeat(Bukkit.getCurrentTick()), 0, 1);
    }

    public void onEnable() {
        if (getCurrentVersion().equals(V1_17_R1)) {
            SkinOverlay.getInstance().setSkinHandler(new SkinHandler_1_17());
        } else if (getCurrentVersion().equals(V1_18_R1)) {
            SkinOverlay.getInstance().setSkinHandler(new SkinHandler_1_18());
        } else if (getCurrentVersion().equals(V1_18_R2)) {
            SkinOverlay.getInstance().setSkinHandler(new SkinHandler_1_18_R2());
        } else if (getCurrentVersion().equals(V1_19_R1)) {
            SkinOverlay.getInstance().setSkinHandler(new SkinHandler_1_19());
        } else if (getCurrentVersion().equals(V1_19_R2)) {
            SkinOverlay.getInstance().setSkinHandler(new SkinHandler_1_19_R2());
        } else {
            SkinOverlay.getInstance().setSkinHandler(new SkinHandler_Unsupported());
        }

        SkinOverlay.getInstance().onEnable();
        BukkitMinecraftUtils.registerListeners(this, new PlayerListeners(), new DeveloperInformListener());
    }

    public void onDisable() {
        SkinOverlay.getInstance().onDisable();
    }


    @Override
    public boolean isBungee() {
        return false;
    }

    @Override
    public Description description() {
        return new Description(getName(), getDescription().getVersion(), getDescription().getMain(), getDescription().getAuthors());
    }

    @Override
    public boolean setEnable(boolean enable) {
        setEnabled(enable);
        return isEnabled();
    }

    @Override
    public boolean isOnlineMode() {
        return Bukkit.getOnlineMode();
    }

    @Override
    public List<PlayerObject> onlinePlayers() {
        List<PlayerObject> playerObjects = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> playerObjects.add(new PlayerObjectBukkit(player)));
        return playerObjects;
    }
}