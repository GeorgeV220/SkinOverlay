package com.georgev22.skinoverlay.utilities.interfaces;

import com.georgev22.library.maps.ObservableObjectMap;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import net.kyori.adventure.platform.AudienceProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * An interface representing the core functionality of the SkinOverlay plugin.
 */
public interface SkinOverlayImpl {

    /**
     * Returns the type of server implementation (Bukkit, Bungee, Velocity, Sponge 8 or Sponge 7).
     *
     * @return The type of server implementation.
     */
    Type type();

    /**
     * Returns the plugin's data folder.
     *
     * @return The plugin's data folder.
     */
    File dataFolder();

    /**
     * Returns the plugin's logger.
     *
     * @return The plugin's logger.
     */
    Logger logger();

    /**
     * Returns the plugin's description.
     *
     * @return The plugin's description.
     */
    Description description();

    /**
     * Enables or disables the plugin.
     *
     * @param enable true to enable, false to disable.
     * @return True if the plugin was successfully enabled or disabled, false otherwise.
     */
    boolean enable(boolean enable);

    /**
     * Returns whether the plugin is currently enabled.
     *
     * @return True if the plugin is enabled, false otherwise.
     */
    boolean enabled();

    /**
     * Saves a resource from the plugin's JAR file to the plugin's data folder.
     *
     * @param resource The path of the resource to save.
     * @param replace  True to replace the file if it already exists, false otherwise.
     */
    void saveResource(@NotNull String resource, boolean replace);

    /**
     * Returns whether the server is running in online mode.
     *
     * @return True if the server is running in online mode, false otherwise.
     */
    boolean onlineMode();

    /**
     * Returns an ObservableObjectMap of PlayerObject instances representing all online players on the server.
     *
     * @return An ObservableObjectMap of PlayerObject instances representing all online players on the server.
     */
    ObservableObjectMap<UUID, PlayerObject> onlinePlayers();

    /**
     * Returns the plugin instance.
     *
     * @return The plugin instance.
     */
    Object plugin();

    /**
     * Returns the server implementation instance.
     *
     * @return The server implementation instance.
     */
    Object serverImpl();

    /**
     * Returns the version of the server implementation.
     *
     * @return The version of the server implementation.
     */
    String serverVersion();

    /**
     * Prints a message to the console.
     *
     * @param msg The message(s) to print.
     */
    default void print(String... msg) {
        Arrays.stream(msg).forEach(s -> logger().info(s));
    }

    AudienceProvider adventure();

    /**
     * A record representing the plugin's description.
     */
    record Description(String name, String version, String main, List<String> authors) {
    }

    /**
     * An enum representing the type of server implementation.
     */
    enum Type {
        BUKKIT,
        BUNGEE,
        VELOCITY,
        SPONGE8,
        SPONGE7;

        /**
         * Returns whether the server implementation is a proxy server.
         *
         * @return True if the server implementation is a proxy server, false otherwise.
         */
        public boolean isProxy() {
            return this.equals(VELOCITY) || this.equals(BUNGEE);
        }
    }
}