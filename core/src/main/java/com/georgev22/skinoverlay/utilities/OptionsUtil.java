package com.georgev22.skinoverlay.utilities;

import com.georgev22.library.minecraft.xseries.XMaterial;
import com.georgev22.library.minecraft.colors.Color;
import com.georgev22.library.minecraft.inventory.ItemBuilder;
import com.georgev22.skinoverlay.SkinOverlay;
import com.google.common.collect.Lists;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.georgev22.library.utilities.Utils.Assertions.notNull;

public enum OptionsUtil {

    BUNGEE("bungeecord", false, Optional.empty()),

    COMMAND_SKINOVERLAY("commands.skinoverlay", true, Optional.empty()),

    DATABASE_HOST("database.SQL.host", "localhost", Optional.empty()),

    DATABASE_PORT("database.SQL.port", 3306, Optional.empty()),

    DATABASE_USER("database.SQL.user", "youruser", Optional.empty()),

    DATABASE_PASSWORD("database.SQL.password", "yourpassword", Optional.empty()),

    DATABASE_DATABASE("database.SQL.database", "VoteRewards", Optional.empty()),

    DATABASE_TABLE_NAME("database.SQL.table name", "voterewards_users", Optional.empty()),

    DATABASE_SQLITE("database.SQLite.file name", "voterewards", Optional.empty()),

    DATABASE_MONGO_HOST("database.MongoDB.host", "localhost", Optional.empty()),

    DATABASE_MONGO_PORT("database.MongoDB.port", 27017, Optional.empty()),

    DATABASE_MONGO_USER("database.MongoDB.user", "youruser", Optional.empty()),

    DATABASE_MONGO_PASSWORD("database.MongoDB.password", "yourpassword", Optional.empty()),

    DATABASE_MONGO_DATABASE("database.MongoDB.database", "VoteRewards", Optional.empty()),

    DATABASE_MONGO_COLLECTION("database.MongoDB.collection", "voterewards_users", Optional.empty()),

    DATABASE_TYPE("database.type", "SQLite", Optional.empty()),

    EXPERIMENTAL_FEATURES("experimental features", false, Optional.empty()),

    METRICS("metrics", true, Optional.empty()),

    DISCORD("discord", false, Optional.empty()),


    ;
    private final String pathName;
    private final Object value;
    private final Optional<String>[] oldPaths;
    private static final SkinOverlay mainPlugin = SkinOverlay.getInstance();

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

    public ItemStack getItemStack(boolean isSavedAsItemStack) {
        if (isSavedAsItemStack) {
            return (ItemStack) mainPlugin.getConfig().get(getPath(), getDefaultValue());
        } else {
            if (mainPlugin.getConfig().get(getPath()) == null) {
                return (ItemStack) getDefaultValue();
            }
            ItemBuilder itemBuilder = new ItemBuilder(
                    notNull("Material", Objects.requireNonNull(XMaterial.valueOf(mainPlugin.getConfig().getString(getPath() + ".item")).parseMaterial())))
                    .amount(mainPlugin.getConfig().getInt(getPath() + ".amount"))
                    .title(mainPlugin.getConfig().getString(getPath() + ".title"))
                    .lores(mainPlugin.getConfig().getStringList(getPath() + ".lores"))
                    .showAllAttributes(
                            mainPlugin.getConfig().getBoolean(getPath() + ".show all attributes"))
                    .glow(mainPlugin.getConfig().getBoolean(getPath() + ".glow"));
            return itemBuilder.build();
        }
    }

    /**
     * Converts and return a String List of color codes to a List of Color classes that represent the colors.
     *
     * @return a List of Color classes that represent the colors.
     */
    public @NotNull List<Color> getColors() {
        List<Color> colors = Lists.newArrayList();
        for (String stringColor : getStringList()) {
            colors.add(Color.from(stringColor));
        }

        return colors;
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
