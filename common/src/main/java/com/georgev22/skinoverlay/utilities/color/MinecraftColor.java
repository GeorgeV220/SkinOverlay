package com.georgev22.skinoverlay.utilities.color;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum MinecraftColor {

    DARK_RED("&4", "#AA0000"),
    RED("&c", "#FF5555"),
    GOLD("&6", "#FFAA00"),
    YELLOW("&e", "#FFFF55"),
    DARK_GREEN("&2", "#00AA00"),
    GREEN("&a", "#55FF55"),
    AQUA("&b", "#55FFFF"),
    DARK_AQUA("&3", "#00AAAA"),
    DARK_BLUE("&1", "#0000AA"),
    BLUE("&9", "#5555FF"),
    LIGHT_PURPLE("&d", "#FF55FF"),
    DARK_PURPLE("&5", "#AA00AA"),
    WHITE("&f", "#FFFFFF"),
    GRAY("&7", "#AAAAAA"),
    DARK_GRAY("&8", "#555555"),
    BLACK("&0", "#000000");

    private final String chatColor;
    private final Color color;

    public static MinecraftColor getClosest(Color color) {
        MinecraftColor minecraftColor = null;
        int i = 0;
        MinecraftColor[] minecraftColors = values();

        for (MinecraftColor minecraftColor1 : minecraftColors) {
            int j = Color.difference(color, minecraftColor1.getColor());
            if (minecraftColor == null || i > j) {
                i = j;
                minecraftColor = minecraftColor1;
            }
        }

        return minecraftColor;
    }

    MinecraftColor(String chatColor, String hexColor) {
        this.chatColor = chatColor;
        this.color = Color.from(hexColor);
    }

    public @NotNull String getName() {
        return this.name().toLowerCase();
    }

    public Color getColor() {
        return this.color;
    }

    public String getTag() {
        return this.chatColor;
    }

    @Contract(pure = true)
    public @NotNull String getAppliedTag() {
        return this.chatColor.replace("&", "\u00A7");
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return this.getAppliedTag();
    }
}
