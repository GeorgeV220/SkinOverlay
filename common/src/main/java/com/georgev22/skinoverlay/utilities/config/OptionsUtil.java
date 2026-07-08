package com.georgev22.skinoverlay.utilities.config;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.color.Color;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum OptionsUtil {
    DEBUG("debug", false, Optional.empty()),

    PROXY("proxy", false, Optional.of("bungeecord")),

    SECRET("secret", "SECRET HERE", Optional.empty()),

    MINESKIN_API_KEY("mineskin api key", "none", Optional.empty()),

    COMMAND_SKINOVERLAY("commands.skinoverlay", true, Optional.empty()),

    DATABASE_HOST("database.SQL.host", "localhost", Optional.empty()),

    DATABASE_PORT("database.SQL.port", 3306, Optional.empty()),

    DATABASE_USER("database.SQL.user", "youruser", Optional.empty()),

    DATABASE_PASSWORD("database.SQL.password", "yourpassword", Optional.empty()),

    DATABASE_DATABASE("database.SQL.database", "SkinOverlay", Optional.empty()),

    DATABASE_FILE_NAME("database.SQL.SQLite file name", "SkinOverlay", Optional.empty()),

    DATABASE_MONGO_HOST("database.MongoDB.host", "localhost", Optional.empty()),

    DATABASE_MONGO_PORT("database.MongoDB.port", 27017, Optional.empty()),

    DATABASE_MONGO_USER("database.MongoDB.user", "youruser", Optional.empty()),

    DATABASE_MONGO_PASSWORD("database.MongoDB.password", "yourpassword", Optional.empty()),

    DATABASE_MONGO_DATABASE("database.MongoDB.database", "SkinOverlay", Optional.empty()),

    DATABASE_TYPE("database.type", "File", Optional.empty()),

    EXPERIMENTAL_FEATURES("experimental features", false, Optional.empty()),

    METRICS("metrics", true, Optional.empty()),

    DISCORD("discord", false, Optional.empty()),

    UPDATER("updater.enabled", true, Optional.empty()),

    DEFAULT_SKIN_UUID("default skin uuid", "8667ba71-b85a-4004-af54-457a9734eed7", Optional.empty()),
    DEFAULT_SKIN_NAME("default skin name", "default", Optional.empty()),
    CUSTOM_SKIN_NAME("custom skin name", "Custom", Optional.empty()),
    CUSTOM_SKIN_URL_NAME("custom skin url name", "Custom URL", Optional.empty()),
    UNKNOWN_SKIN_NAME("unknown skin name", "Unknown", Optional.empty()),

    SKIN_HOOK("skin hook", "SkinsRestorer", Optional.empty()),
    LOCALE("locale", "en_US", Optional.empty()),
    SAVE_INTERVAL("save interval", 20, Optional.empty()),
    CONNECTION_TYPE("connection type", "PluginMessage", Optional.empty()),
    REDIS_HOST("redis.host", "localhost", Optional.empty()),
    REDIS_PORT("redis.port", 6379, Optional.empty()),
    REDIS_PASSWORD("redis.password", "", Optional.empty()),
    ;
    private static final FileManager fileManager = FileManager.getInstance();
    private final String pathName;
    private final Object defaultValue;
    private final Optional<String>[] oldPaths;
    private String resolvedPath;
    private Object cachedValue;

    @SafeVarargs
    OptionsUtil(final String pathName, final Object defaultValue, Optional<String>... oldPaths) {
        this.pathName = pathName;
        this.defaultValue = defaultValue;
        this.oldPaths = oldPaths;
    }

    /**
     * Reloads and caches all configuration options.
     *
     * <p>
     * This method must be called:
     * <ul>
     *     <li>On plugin startup</li>
     *     <li>After a configuration reload</li>
     * </ul>
     *
     * <p>
     * After this method is called, all getters operate in O(1) time
     * without accessing the YAML configuration.
     */
    public static void reloadAll() {
        for (OptionsUtil option : values()) {
            option.reload();
        }
    }

    /**
     * Reloads and caches the value of this option.
     *
     * <p>
     * The configuration path is resolved once and stored.
     * The value is then read from the configuration and cached.
     */
    public void reload() {
        resolvedPath = null;
        String path = getPath();
        Object val = fileManager.getConfig().getFileConfiguration().get(path);
        cachedValue = (val != null) ? val : defaultValue;
    }

    /**
     * Returns the cached boolean value of this option.
     *
     * @return the boolean value
     */
    public boolean getBooleanValue() {
        if (cachedValue instanceof Boolean b) {
            return b;
        }
        return Boolean.parseBoolean(String.valueOf(cachedValue));
    }

    /**
     * Returns the cached integer value of this option.
     *
     * @return the integer value
     */
    public @NonNull Integer getIntValue() {
        if (cachedValue instanceof Number n) {
            return n.intValue();
        }
        return Integer.parseInt(String.valueOf(cachedValue));
    }

    /**
     * Returns the cached long value of this option.
     *
     * @return the long value
     */
    public @NonNull Long getLongValue() {
        if (cachedValue instanceof Number n) {
            return n.longValue();
        }
        return Long.parseLong(String.valueOf(cachedValue));
    }

    /**
     * Returns the cached double value of this option.
     *
     * @return the double value
     */
    public @NonNull Double getDoubleValue() {
        if (cachedValue instanceof Number n) {
            return n.doubleValue();
        }
        return Double.parseDouble(String.valueOf(cachedValue));
    }

    /**
     * Returns the cached float value of this option.
     *
     * @return the float value
     */
    public @NonNull Float getFloatValue() {
        if (cachedValue instanceof Number n) {
            return n.floatValue();
        }
        return Float.parseFloat(String.valueOf(cachedValue));
    }

    /**
     * Returns the cached string value of this option.
     *
     * @return the string value
     */
    public String getStringValue() {
        return String.valueOf(cachedValue);
    }

    /**
     * Returns the cached string list value of this option.
     *
     * @return a list of strings, or an empty list if the value is not a list
     */
    @SuppressWarnings("unchecked")
    public @NonNull List<String> getStringList() {
        return cachedValue instanceof List
                ? (List<String>) cachedValue
                : List.of();
    }

    /**
     * Converts the cached string list into a list of {@link Color} objects.
     *
     * @return a list of parsed colors
     */
    public @NonNull List<Color> getColors() {
        return getStringList().stream()
                .map(Color::from)
                .collect(Collectors.toList());
    }

    /**
     * Returns the cached string value wrapped in an {@link Optional}.
     *
     * @return an optional containing the string value
     */
    public @NonNull Optional<String> getOptionalStringValue() {
        return Optional.ofNullable(getStringValue());
    }

    /**
     * Resolves and returns the configuration path for this option.
     *
     * <p>
     * The resolution order is:
     * <ol>
     *     <li>Cached path</li>
     *     <li>Current path</li>
     *     <li>Legacy paths</li>
     * </ol>
     *
     * <p>
     * The resolved path is cached after the first lookup.
     *
     * @return the resolved configuration path
     */
    public @NonNull String getPath() {
        if (resolvedPath != null) {
            return resolvedPath;
        }

        String base = "Options." + pathName;
        if (fileManager.getConfig().getFileConfiguration().get(base) != null) {
            return resolvedPath = base;
        }

        for (Optional<String> old : oldPaths) {
            if (old.isPresent()) {
                String oldPath = "Options." + old.get();
                if (fileManager.getConfig().getFileConfiguration().get(oldPath) != null) {
                    return resolvedPath = oldPath;
                }
            }
        }

        return resolvedPath = base;
    }

    /**
     * Returns the default (current) path name without the "Options." prefix.
     *
     * @return the default path name
     */
    @Contract(pure = true)
    public @NonNull String getDefaultPath() {
        return this.pathName;
    }

    /**
     * Returns the legacy paths associated with this option.
     *
     * @return an array of legacy paths
     */
    public Optional<String>[] getOldPaths() {
        return oldPaths;
    }

    /**
     * Returns the default value of this option.
     *
     * @return the default value
     */
    public Object getDefaultValue() {
        return defaultValue;
    }
}