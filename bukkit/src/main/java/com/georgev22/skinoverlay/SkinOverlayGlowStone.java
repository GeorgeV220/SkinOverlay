package com.georgev22.skinoverlay;

import com.georgev22.api.libraryloader.annotations.MavenLibrary;
import net.glowstone.util.library.Library;
import net.glowstone.util.library.LibraryManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SkinOverlayGlowStone extends JavaPlugin {

    private SkinOverlayPluginImpl skinOverlayPluginImpl;

    @Override
    public void onLoad() {
        skinOverlayPluginImpl = new SkinOverlayPluginImpl(this, true);
        List<Library> libraries = new ArrayList<>();
        MavenLibrary[] libs = skinOverlayPluginImpl.getClass().getDeclaredAnnotationsByType(MavenLibrary.class);

        for (MavenLibrary lib : libs) {
            if (
                    !lib.groupId().equalsIgnoreCase("")
                            || !lib.artifactId().equalsIgnoreCase("")
                            || !lib.version().equalsIgnoreCase("")
            )
                libraries.add(new Library(lib.groupId(), lib.artifactId(), lib.version(), lib.repo().value()));
            else {
                String[] dependency = lib.value().split(":", 4);
                libraries.add(new Library(dependency[0], dependency[1], dependency[2], dependency[3]));
            }
        }
        new LibraryManager("https://repo1.maven.org/maven2", new File(skinOverlayPluginImpl.dataFolder(), "libraries").toURI().getPath(),
                false,
                3, libraries).run();
        skinOverlayPluginImpl.logger().info("Glowstone Skin Overlay Plugin Loaded");
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
