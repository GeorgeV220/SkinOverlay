package com.georgev22.skinoverlay.utilities.config;

import com.georgev22.library.utilities.Color;
import com.georgev22.library.yaml.file.FileConfiguration;
import com.georgev22.skinoverlay.SkinOverlay;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum OverlayOptionsUtil {
    PARTS_OVERLAY_JACKET("overlay.jacket", false, Optional.empty()),

    PARTS_OVERLAY_LEFT_SLEEVE("overlay.left_sleeve", false, Optional.empty()),

    PARTS_OVERLAY_RIGHT_SLEEVE("overlay.right_sleeve", false, Optional.empty()),

    PARTS_OVERLAY_LEFT_PANTS("overlay.left_pants", false, Optional.empty()),

    PARTS_OVERLAY_RIGHT_PANTS("overlay.right_pants", false, Optional.empty()),

    PARTS_OVERLAY_HAT("overlay.hat", false, Optional.empty()),

    // PLAYER PARTS

    PARTS_PLAYER_JACKET("player.jacket", false, Optional.empty()),

    PARTS_PLAYER_LEFT_SLEEVE("player.left_sleeve", false, Optional.empty()),

    PARTS_PLAYER_RIGHT_SLEEVE("player.right_sleeve", false, Optional.empty()),

    PARTS_PLAYER_LEFT_PANTS("player.left_pants", false, Optional.empty()),

    PARTS_PLAYER_RIGHT_PANTS("player.right_pants", false, Optional.empty()),

    PARTS_PLAYER_HAT("player.hat", false, Optional.empty()),
    ;

    private static final SkinOverlay mainPlugin = SkinOverlay.getInstance();
    private final String pathName;
    private final Object value;
    private final Optional<String>[] oldPaths;

    @SafeVarargs
    @Contract(pure = true)
    OverlayOptionsUtil(final String pathName, final Object value, Optional<String>... oldPaths) {
        this.pathName = pathName;
        this.value = value;
        this.oldPaths = oldPaths;
    }

    public boolean getBooleanValue(FileConfiguration fileConfiguration) {
        return fileConfiguration.getBoolean(getPath(fileConfiguration), Boolean.parseBoolean(String.valueOf(getDefaultValue(fileConfiguration))));
    }

    public Object getObjectValue(FileConfiguration fileConfiguration) {
        return fileConfiguration.get(getPath(fileConfiguration), getDefaultValue(fileConfiguration));
    }

    public String getStringValue(FileConfiguration fileConfiguration) {
        return fileConfiguration.getString(getPath(fileConfiguration), String.valueOf(getDefaultValue(fileConfiguration)));
    }

    public @NotNull Long getLongValue(FileConfiguration fileConfiguration) {
        return fileConfiguration.getLong(getPath(fileConfiguration), Long.parseLong(String.valueOf(getDefaultValue(fileConfiguration))));
    }

    public @NotNull Integer getIntValue(FileConfiguration fileConfiguration) {
        return fileConfiguration.getInt(getPath(fileConfiguration), Integer.parseInt(String.valueOf(getDefaultValue(fileConfiguration))));
    }

    public @NotNull Double getDoubleValue(FileConfiguration fileConfiguration) {
        return fileConfiguration.getDouble(getPath(fileConfiguration), Double.parseDouble(String.valueOf(getDefaultValue(fileConfiguration))));
    }

    public @NotNull List<String> getStringList(FileConfiguration fileConfiguration) {
        return fileConfiguration.getStringList(getPath(fileConfiguration));
    }

    /**
     * Converts and return a String List of color codes to a List of Color classes that represent the colors.
     *
     * @return a List of Color classes that represent the colors.
     */
    public @NotNull List<Color> getColors(FileConfiguration fileConfiguration) {
        return getStringList(fileConfiguration).stream().map(Color::from).collect(Collectors.toList());
    }

    /**
     * Returns the path.
     *
     * @return the path.
     */
    public @NotNull String getPath(FileConfiguration fileConfiguration) {
        if (fileConfiguration.get("Options." + getDefaultPath(fileConfiguration)) == null) {
            for (Optional<String> path : getOldPaths(fileConfiguration)) {
                if (path.isPresent()) {
                    if (fileConfiguration.get("Options." + path.get()) != null) {
                        return "Options." + path.get();
                    }
                }
            }
        }
        return "Options." + getDefaultPath(fileConfiguration);
    }

    /**
     * Returns the default path.
     *
     * @return the default path.
     */
    @Contract(pure = true)
    public @NotNull String getDefaultPath(FileConfiguration fileConfiguration) {
        return this.pathName;
    }

    /**
     * Returns the old path if it exists.
     *
     * @return the old path if it exists.
     */
    public Optional<String>[] getOldPaths(FileConfiguration fileConfiguration) {
        return oldPaths;
    }

    /**
     * Returns the default value if the path have no value.
     *
     * @return the default value if the path have no value.
     */
    public Object getDefaultValue(FileConfiguration fileConfiguration) {
        return value;
    }

    public Optional<String> getOptionalStringValue(FileConfiguration fileConfiguration) {
        return Optional.ofNullable(getStringValue(fileConfiguration));
    }
}
