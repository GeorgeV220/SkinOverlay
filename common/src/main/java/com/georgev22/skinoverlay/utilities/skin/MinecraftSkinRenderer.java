package com.georgev22.skinoverlay.utilities.skin;

import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Renders a full Minecraft skin by compositing multiple {@link Part} images onto a single 64x64 canvas.
 */
public class MinecraftSkinRenderer {

    /**
     * The final rendered full skin image.
     */
    private SerializableBufferedImage fullSkinImage;

    /**
     * The array of {@link Part} objects to render.
     */
    private final Part[] parts;

    /**
     * Constructs a new MinecraftSkinRenderer with the specified parts.
     *
     * @param parts The parts to render onto the skin.
     */
    public MinecraftSkinRenderer(Part... parts) {
        this.parts = parts;
    }

    /**
     * Creates the full skin image by drawing each part onto a new 64x64 {@link BufferedImage}.
     * <p>
     * The resulting image can be retrieved using {@link #getFullSkinImage()}.
     * </p>
     */
    public void createFullSkinImage() {
        fullSkinImage = new SerializableBufferedImage(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));

        Graphics g = fullSkinImage.getBufferedImage().getGraphics();

        for (Part part : parts) {
            g.drawImage(part.image().getBufferedImage(), part.x(), part.y(), part.width(), part.height(), null);
        }

        g.dispose();
    }

    /**
     * Returns the rendered full skin image.
     *
     * @return The full skin image as a {@link SerializableBufferedImage}.
     */
    public SerializableBufferedImage getFullSkinImage() {
        return fullSkinImage;
    }

    /**
     * Returns the parts used for rendering this skin.
     *
     * @return An array of {@link Part} objects.
     */
    public Part[] getParts() {
        return parts;
    }
}
