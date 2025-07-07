package com.georgev22.skinoverlay.utilities.color;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class ColorCalculations {
    public static @NotNull List<Color> getColorsInBetween(@NotNull Color color, @NotNull Color color2, int n) {
        double n2 = (double) (color2.getRed() - color.getRed()) / (double) n;
        double n3 = (double) (color2.getGreen() - color.getGreen()) / (double) n;
        double n4 = (double) (color2.getBlue() - color.getBlue()) / (double) n;
        ArrayList<Color> list = new ArrayList<>();

        for (int i = 1; i <= n; ++i) {
            list.add(Color.from((int) Math.round((double) color.getRed() + n2 * (double) i), (int) Math.round((double) color.getGreen() + n3 * (double) i), (int) Math.round((double) color.getBlue() + n4 * (double) i)));
        }

        return list;
    }

    public static @NotNull Color interpolateColor(@NotNull Color color1, @NotNull Color color2, float ratio) {
        ratio = Math.max(0f, Math.min(1f, ratio)); // Clamp between 0 and 1

        int red = Math.round(color1.getRed() + ratio * (color2.getRed() - color1.getRed()));
        int green = Math.round(color1.getGreen() + ratio * (color2.getGreen() - color1.getGreen()));
        int blue = Math.round(color1.getBlue() + ratio * (color2.getBlue() - color1.getBlue()));

        return Color.from(red, green, blue);
    }
}
