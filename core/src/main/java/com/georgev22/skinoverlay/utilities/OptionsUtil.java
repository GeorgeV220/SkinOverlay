package com.georgev22.skinoverlay.utilities;

import com.georgev22.library.utilities.Color;
import com.georgev22.skinoverlay.SkinOverlay;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum OptionsUtil {

    PROXY("proxy", false, Optional.of("bungeecord")),

    SECRET("secret", "SECRET HERE", Optional.empty()),

    COMMAND_SKINOVERLAY("commands.skinoverlay", true, Optional.empty()),

    DATABASE_HOST("database.SQL.host", "localhost", Optional.empty()),

    DATABASE_PORT("database.SQL.port", 3306, Optional.empty()),

    DATABASE_USER("database.SQL.user", "youruser", Optional.empty()),

    DATABASE_PASSWORD("database.SQL.password", "yourpassword", Optional.empty()),

    DATABASE_DATABASE("database.SQL.database", "SkinOverlay", Optional.empty()),

    DATABASE_TABLE_NAME("database.SQL.table name", "skinoverlay_users", Optional.empty()),

    DATABASE_SQLITE("database.SQLite.file name", "skinoverlay", Optional.empty()),

    DATABASE_MONGO_HOST("database.MongoDB.host", "localhost", Optional.empty()),

    DATABASE_MONGO_PORT("database.MongoDB.port", 27017, Optional.empty()),

    DATABASE_MONGO_USER("database.MongoDB.user", "youruser", Optional.empty()),

    DATABASE_MONGO_PASSWORD("database.MongoDB.password", "yourpassword", Optional.empty()),

    DATABASE_MONGO_DATABASE("database.MongoDB.database", "skinoverlay", Optional.empty()),

    DATABASE_MONGO_COLLECTION("database.MongoDB.collection", "skinoverlay_users", Optional.empty()),

    DATABASE_TYPE("database.type", "SQLite", Optional.empty()),

    EXPERIMENTAL_FEATURES("experimental features", false, Optional.empty()),

    METRICS("metrics", true, Optional.empty()),

    DISCORD("discord", false, Optional.empty()),

    UPDATER("updater.enabled", true, Optional.empty()),

    OVERLAY_CAPE("overlays.%s.cape", false, Optional.empty()),

    OVERLAY_JACKET("overlays.%s.jacket", false, Optional.empty()),

    OVERLAY_LEFT_SLEEVE("overlays.%s.left_sleeve", false, Optional.empty()),

    OVERLAY_RIGHT_SLEEVE("overlays.%s.right_sleeve", false, Optional.empty()),

    OVERLAY_LEFT_PANTS("overlays.%s.left_pants", false, Optional.empty()),

    OVERLAY_RIGHT_PANTS("overlays.%s.right_pants", false, Optional.empty()),

    OVERLAY_HAT("overlays.%s.hat", false, Optional.empty()),
    DEFAULT_SKIN_UUID("default skin uuid", "8667ba71-b85a-4004-af54-457a9734eed7", Optional.empty()),
    SKIN_HOOK("skin hook", "SkinsRestorer", Optional.empty()),
    ;
    private static final SkinOverlay mainPlugin = SkinOverlay.getInstance();
    private final String pathName;
    private final Object value;
    private final Optional<String>[] oldPaths;

    @SafeVarargs
    @Contract(pure = true)
    OptionsUtil(final String pathName, final Object value, Optional<String>... oldPaths) {
        this.pathName = pathName;
        this.value = value;
        this.oldPaths = oldPaths;
    }

    public boolean getBooleanValue() {
        return mainPlugin.getConfig().getBoolean(getPath(), Boolean.parseBoolean(String.valueOf(getDefaultValue())));
    }

    public Object getObjectValue() {
        return mainPlugin.getConfig().get(getPath(), getDefaultValue());
    }

    public String getStringValue() {
        return mainPlugin.getConfig().getString(getPath(), String.valueOf(getDefaultValue()));
    }

    public @NotNull Long getLongValue() {
        return mainPlugin.getConfig().getLong(getPath(), Long.parseLong(String.valueOf(getDefaultValue())));
    }

    public @NotNull Integer getIntValue() {
        return mainPlugin.getConfig().getInt(getPath(), Integer.parseInt(String.valueOf(getDefaultValue())));
    }

    public @NotNull Double getDoubleValue() {
        return mainPlugin.getConfig().getDouble(getPath(), Double.parseDouble(String.valueOf(getDefaultValue())));
    }

    public @NotNull List<String> getStringList() {
        return mainPlugin.getConfig().getStringList(getPath());
    }

    /**
     * Converts and return a String List of color codes to a List of Color classes that represent the colors.
     *
     * @return a List of Color classes that represent the colors.
     */
    public @NotNull List<Color> getColors() {
        return getStringList().stream().map(Color::from).collect(Collectors.toList());
    }

    public boolean getBooleanValue(String arg) {
        return mainPlugin.getConfig().getBoolean(String.format(getPath(), arg), Boolean.parseBoolean(String.valueOf(getDefaultValue())));
    }

    public Object getObjectValue(String arg) {
        return mainPlugin.getConfig().get(getPath(), getDefaultValue());
    }

    public String getStringValue(String arg) {
        return mainPlugin.getConfig().getString(getPath(), String.valueOf(getDefaultValue()));
    }

    public @NotNull Long getLongValue(String arg) {
        return mainPlugin.getConfig().getLong(getPath(), Long.parseLong(String.valueOf(getDefaultValue())));
    }

    public @NotNull Integer getIntValue(String arg) {
        return mainPlugin.getConfig().getInt(getPath(), Integer.parseInt(String.valueOf(getDefaultValue())));
    }

    public @NotNull Double getDoubleValue(String arg) {
        return mainPlugin.getConfig().getDouble(getPath(), Double.parseDouble(String.valueOf(getDefaultValue())));
    }

    public @NotNull List<String> getStringList(String arg) {
        return mainPlugin.getConfig().getStringList(getPath());
    }

    /**
     * Converts and return a String List of color codes to a List of Color classes that represent the colors.
     *
     * @return a List of Color classes that represent the colors.
     */
    public @NotNull List<Color> getColors(String arg) {
        return getStringList().stream().map(Color::from).collect(Collectors.toList());
    }

    /**
     * Returns the path.
     *
     * @return the path.
     */
    public @NotNull String getPath() {
        if (mainPlugin.getConfig().get("Options." + getDefaultPath()) == null) {
            for (Optional<String> path : getOldPaths()) {
                if (path.isPresent()) {
                    if (mainPlugin.getConfig().get("Options." + path.get()) != null) {
                        return "Options." + path.get();
                    }
                }
            }
        }
        return "Options." + getDefaultPath();
    }

    /**
     * Returns the default path.
     *
     * @return the default path.
     */
    @Contract(pure = true)
    public @NotNull String getDefaultPath() {
        return this.pathName;
    }

    /**
     * Returns the old path if it exists.
     *
     * @return the old path if it exists.
     */
    public Optional<String>[] getOldPaths() {
        return oldPaths;
    }

    /**
     * Returns the default value if the path have no value.
     *
     * @return the default value if the path have no value.
     */
    public Object getDefaultValue() {
        return value;
    }

    public Optional<String> getOptionalStringValue() {
        return Optional.ofNullable(getStringValue());
    }
}
