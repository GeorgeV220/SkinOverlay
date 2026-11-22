package com.georgev22.skinoverlay.utilities.skin;

import com.georgev22.skinoverlay.utilities.config.OverlayOptionsUtil;
import org.bspfsystems.yamlconfiguration.file.FileConfiguration;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Filters skin parts and renders overlay artwork using the same options as player skin generation.
 * <p>
 * Only outer skin parts are controlled by configuration switches. Base skin parts are always
 * included, and missing outer-part switches default to {@code false}.
 * </p>
 */
public final class OverlayRenderer {
    private OverlayRenderer() {
    }

    /**
     * Determines whether a skin part is enabled for an overlay image or the player's base skin.
     *
     * @param part          extracted skin part to inspect
     * @param configuration overlay configuration supplying part switches
     * @param player        {@code true} to use {@code Options.player}; {@code false} to use {@code Options.overlay}
     * @return {@code true} for base skin parts or enabled outer parts
     */
    public static boolean includes(Part part, FileConfiguration configuration, boolean player) {
        String prefix = player ? "PARTS_PLAYER_" : "PARTS_OVERLAY_";
        String name = part.name();
        String option = null;
        if (name.startsWith("Hat")) option = "HAT";
        else if (name.startsWith("Jacket")) option = "JACKET";
        else if (name.startsWith("Left_Sleeve")) option = "LEFT_SLEEVE";
        else if (name.startsWith("Right_Sleeve")) option = "RIGHT_SLEEVE";
        else if (name.startsWith("Left_Pants")) option = "LEFT_PANTS";
        else if (name.startsWith("Right_Pants")) option = "RIGHT_PANTS";
        return option == null || OverlayOptionsUtil.valueOf(prefix + option).getBooleanValue(configuration);
    }

    /**
     * Selects already extracted skin parts using the relevant configuration switches.
     *
     * @param skin          skin wrapper whose parts have been created for the desired model
     * @param configuration overlay configuration supplying part switches
     * @param player        {@code true} to filter player parts; {@code false} to filter overlay parts
     * @return an immutable list of included parts, retaining their original image references
     */
    public static List<Part> parts(SkinParts skin, FileConfiguration configuration, boolean player) {
        return skin.getParts().values().stream()
                .filter(part -> includes(part, configuration, player))
                .toList();
    }

    /**
     * Renders one overlay's enabled artwork onto a transparent 64x64 image.
     * <p>
     * Legacy images are converted during part extraction. Parts are local to this invocation,
     * so rendering does not mutate the overlay's cached skin wrapper or include a player's skin.
     * Layer ordering is applied by the registry when it combines these rendered images.
     * </p>
     *
     * @param overlay overlay whose model, image and part switches should be used
     * @return a new ARGB image containing the filtered overlay artwork
     */
    public static BufferedImage render(Overlay overlay) {
        // Use local parts: the same cached overlay may be composed concurrently for different players.
        SkinParts skin = new SkinParts(overlay.getBufferedImage(), overlay.key());
        skin.createParts(overlay.model());
        BufferedImage result = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();
        try {
            for (Part part : parts(skin, overlay.configurationFile().getFileConfiguration(), false)) {
                graphics.drawImage(part.image().getBufferedImage(), part.x(), part.y(), null);
            }
        } finally {
            graphics.dispose();
        }
        return result;
    }
}
