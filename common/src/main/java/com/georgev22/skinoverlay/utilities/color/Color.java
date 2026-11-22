package com.georgev22.skinoverlay.utilities.color;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a color with support for hexadecimal, RGB, and various formatting tags.
 */
@SuppressWarnings("UnnecessaryUnicodeEscape")
public class Color {

    private final String colorCode;
    private final int r;
    private final int g;
    private final int b;
    private static java.awt.Color javaColor;

    /**
     * Creates a {@link Color} instance from a hex string (with or without the # prefix).
     *
     * @param colorCode The hex color code.
     * @return A new {@link Color} instance.
     */
    @Contract("_ -> new")
    public static @NotNull Color from(String colorCode) {
        return new Color(colorCode);
    }

    /**
     * Creates a {@link Color} instance from RGB values.
     *
     * @param r Red component (0–255).
     * @param g Green component (0–255).
     * @param b Blue component (0–255).
     * @return A new {@link Color} instance.
     */
    public static @NotNull Color from(int r, int g, int b) {
        javaColor = new java.awt.Color(r, g, b);
        return from(Integer.toHexString(javaColor.getRGB()).substring(2));
    }

    /**
     * Constructs a {@link Color} from a hex code.
     *
     * @param colorCode Hexadecimal color code (e.g., #ff5733 or ff5733).
     */
    private Color(@NotNull String colorCode) {
        this.colorCode = colorCode.replace("#", "");
        javaColor = new java.awt.Color(Integer.parseInt(this.colorCode, 16));
        this.r = javaColor.getRed();
        this.g = javaColor.getGreen();
        this.b = javaColor.getBlue();
    }

    /**
     * @return The hexadecimal color code (without the # prefix).
     */
    public String getColorCode() {
        return this.colorCode;
    }

    /**
     * @return The red component of the color.
     */
    public int getRed() {
        return this.r;
    }

    /**
     * @return The green component of the color.
     */
    public int getGreen() {
        return this.g;
    }

    /**
     * @return The blue component of the color.
     */
    public int getBlue() {
        return this.b;
    }

    /**
     * @return A {@link java.awt.Color} representation of this color.
     */
    public java.awt.Color getJavaColor() {
        return javaColor;
    }

    /**
     * @return A darker version of this color.
     */
    public @NotNull Color darker() {
        return Color.from(javaColor.darker().getRed(), javaColor.darker().getGreen(), javaColor.darker().getBlue());
    }

    /**
     * @return A brighter version of this color.
     */
    public @NotNull Color brighter() {
        return Color.from(javaColor.brighter().getRed(), javaColor.brighter().getGreen(), javaColor.brighter().getBlue());
    }

    /**
     * Gets the color code formatted as a legacy Minecraft text color code or as the closest fallback.
     *
     * @param ver     Whether to return the full §x-style hex format (Minecraft 1.16+).
     * @param closest A fallback color code to return if ver is false.
     * @return The formatted color code.
     */
    public String getAppliedTag(boolean ver, String closest) {
        return ver
                ? "\u00A7x" + Arrays.stream(this.colorCode.split(""))
                .map((paramString) -> "\u00A7" + paramString)
                .collect(Collectors.joining())
                : closest;
    }

    /**
     * @return The color with a leading '#' (e.g., #ff5733).
     */
    public String toHex() {
        return "#" + this.colorCode;
    }

    /**
     * @return The color formatted in MiniMessage tag format (e.g., &lt;color:#ff5733&gt;).
     */
    public String getMiniMessageTag() {
        return "<color:#" + this.colorCode + ">";
    }

    /**
     * Wraps text in a MiniMessage color tag using this color.
     *
     * @param text The text to wrap.
     * @return The colorized MiniMessage string.
     */
    public String toMiniMessage(String text) {
        return this.getMiniMessageTag() + text + "</color>";
    }

    /**
     * Calculates the RGB difference between two {@link Color} instances.
     *
     * @param color1 The first color.
     * @param color2 The second color.
     * @return The sum of absolute RGB differences.
     */
    public static int difference(@NotNull Color color1, @NotNull Color color2) {
        return Math.abs(color1.r - color2.r)
                + Math.abs(color1.g - color2.g)
                + Math.abs(color1.b - color2.b);
    }

    /**
     * @return A string representation using the Minecraft §x hex format.
     */
    @Override
    public String toString() {
        return this.getAppliedTag(true, "");
    }

    /**
     * Compares this color to another for equality based on RGB and hex code.
     *
     * @param o The object to compare.
     * @return True if equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Color color)) return false;
        return r == color.r && g == color.g && b == color.b && Objects.equals(colorCode, color.colorCode);
    }

    /**
     * @return A hash code based on RGB and hex code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(colorCode, r, g, b);
    }
}
