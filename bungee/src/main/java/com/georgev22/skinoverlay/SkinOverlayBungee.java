package com.georgev22.skinoverlay;

import co.aikar.commands.BungeeCommandManager;
import com.georgev22.api.libraryloader.LibraryLoader;
import com.georgev22.api.libraryloader.annotations.MavenLibrary;
import com.georgev22.api.libraryloader.exceptions.InvalidDependencyException;
import com.georgev22.api.libraryloader.exceptions.UnknownDependencyException;
import com.georgev22.library.minecraft.BungeeMinecraftUtils;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.library.yaml.file.FileConfiguration;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.listeners.bungee.DeveloperInformListener;
import com.georgev22.skinoverlay.listeners.bungee.PlayerListeners;
import com.georgev22.skinoverlay.utilities.BungeeCordPluginMessageUtils;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectBungee;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.connection.InitialHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@MavenLibrary(groupId = "org.mongodb", artifactId = "mongo-java-driver", version = "3.12.7")
@MavenLibrary(groupId = "mysql", artifactId = "mysql-connector-java", version = "8.0.22")
@MavenLibrary(groupId = "org.xerial", artifactId = "sqlite-jdbc", version = "3.34.0")
@MavenLibrary(groupId = "com.google.guava", artifactId = "guava", version = "30.1.1-jre")
@MavenLibrary(groupId = "org.postgresql", artifactId = "postgresql", version = "42.2.18")
@MavenLibrary(groupId = "commons-io", artifactId = "commons-io", version = "2.11.0")
@MavenLibrary(groupId = "commons-codec", artifactId = "commons-codec", version = "1.15")
@MavenLibrary(groupId = "commons-lang", artifactId = "commons-lang", version = "2.6")
@MavenLibrary("com.mojang:authlib:3.11.50:https://nexus.velocitypowered.com/repository/maven-public/")
@MavenLibrary("org.apache.commons:commons-lang3:3.12.0:https://repo1.maven.org/maven2/")
public class SkinOverlayBungee extends Plugin implements SkinOverlayImpl {

    private int tick = 0;

    private boolean enabled = false;

    @Override
    public void onLoad() {
        try {
            new LibraryLoader(this.getClass(), this.getDataFolder()).loadAll(true);
        } catch (InvalidDependencyException | UnknownDependencyException e) {
            throw new RuntimeException(e);
        }
        SkinOverlay.getInstance().onLoad(this);
    }

    @Override
    public void onEnable() {
        getProxy().getScheduler().schedule(this, () -> SchedulerManager.getScheduler().mainThreadHeartbeat(tick++), 0, 50L, TimeUnit.MILLISECONDS);
        SkinOverlay.getInstance().setSkinHandler(new SkinHandler() {
            @Override
            public void updateSkin(@NotNull FileConfiguration fileConfiguration, @NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName) {
                if (reset) {
                    new BungeeCordPluginMessageUtils().sendDataTooAllServers("reset", playerObject.playerUUID().toString(), "default");
                } else {
                    new BungeeCordPluginMessageUtils().sendDataTooAllServers("change", playerObject.playerUUID().toString(), skinName);
                }
            }

            @Override
            public void updateSkin(@NotNull FileConfiguration fileConfiguration, @NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName, Property property) {
                if (reset) {
                    new BungeeCordPluginMessageUtils().sendDataTooAllServers("resetWithProperties", playerObject.playerUUID().toString(), "default", property.getName(), property.getValue(), property.getSignature());
                } else {
                    new BungeeCordPluginMessageUtils().sendDataTooAllServers("changeWithProperties", playerObject.playerUUID().toString(), skinName, property.getName(), property.getValue(), property.getSignature());
                }
            }

            @Override
            protected <T> GameProfile getGameProfile0(@NotNull PlayerObject playerObject) {
                GameProfile gameProfile = new GameProfile(playerObject.playerUUID(), playerObject.playerName());
                if (!gameProfile.getProperties().containsKey("textures")) {
                    for (net.md_5.bungee.protocol.Property property : ((InitialHandler) ((ProxiedPlayer) playerObject.getPlayer()).getPendingConnection()).getLoginProfile().getProperties()) {
                        gameProfile.getProperties().put(property.getName(), new Property(property.getName(), property.getValue(), property.getSignature()));
                    }
                }
                return gameProfile;
            }
        });
        SkinOverlay.getInstance().setCommandManager(new BungeeCommandManager(this));
        SkinOverlay.getInstance().onEnable();
        SkinOverlay.getInstance().setupCommands();
        BungeeMinecraftUtils.registerListeners(this, new PlayerListeners(), new DeveloperInformListener());
        getProxy().registerChannel("skinoverlay:bungee");
        enabled = true;
    }

    @Override
    public void onDisable() {
        SkinOverlay.getInstance().onDisable();
        enabled = false;
    }

    @Override
    public Type type() {
        return Type.BUNGEE;
    }

    @Override
    public Description description() {
        return new Description(getDescription().getName(), getDescription().getVersion(), getDescription().getMain(), Collections.singletonList(getDescription().getAuthor()));
    }

    @Override
    public boolean setEnable(boolean enable) {
        if (enable) {
            onEnable();
        } else {
            onDisable();
        }
        return isEnabled();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void saveResource(@NotNull String resource, boolean replace) {
        try {
            Utils.saveResource(resource, replace, getDataFolder(), this.getClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isOnlineMode() {
        return getProxy().getConfig().isOnlineMode();
    }

    @Override
    public List<PlayerObject> onlinePlayers() {
        return getProxy().getPlayers().stream().map(PlayerObjectBungee::new).collect(Collectors.toList());
    }

    @Override
    public Object getPlugin() {
        return this;
    }

    @Override
    public ProxyServer getServerImpl() {
        return getProxy();
    }
}
