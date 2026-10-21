package com.georgev22.skinoverlay.utilities.skin;

import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Converts Minecraft skin textures between the classic (Steve)
 * and slim (Alex) player models.
 * <p>
 * Supports standard 64x64 Minecraft skin textures, including
 * the base skin and the outer armor/clothing layer.
 */
public final class SkinConverter {

    private static final int SKIN_WIDTH = 64;
    private static final int SKIN_HEIGHT = 64;

    private SkinConverter() {
        throw new UnsupportedOperationException(
                "This class cannot be instantiated"
        );
    }

    /**
     * Converts an old format skin to the new format (64x64).
     *
     * @param original The original skin image in old format.
     * @return The converted skin image in new format.
     */
    public static SerializableBufferedImage convertSkin(@NotNull SerializableBufferedImage original) {
        BufferedImage orig = original.getBufferedImage();
        BufferedImage newImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);

        // Copy top half of the original image
        newImage.getGraphics().drawImage(orig, 0, 0, 64, 32, null);

        // Copy left leg from the original image
        BufferedImage leftLeg = orig.getSubimage(0, 16, 16, 16);
        newImage.getGraphics().drawImage(leftLeg, 16, 48, null);

        // Copy right leg from the original image
        BufferedImage rightLeg = orig.getSubimage(40, 16, 16, 16);
        newImage.getGraphics().drawImage(rightLeg, 32, 48, null);

        return new SerializableBufferedImage(newImage);
    }

    /**
     * Converts a slim (Alex) skin into a classic (Steve) skin.
     * <p>
     * The three-pixel-wide arm faces are expanded to four pixels.
     * The additional pixels are duplicated using nearest-neighbor
     * sampling to preserve the pixel-art appearance.
     *
     * @param skin the original 64x64 slim skin
     * @return a new classic skin texture
     * @throws NullPointerException     if skin is null
     * @throws IllegalArgumentException if the texture is not 64x64
     */
    public static SerializableBufferedImage toClassic(SerializableBufferedImage skin) {
        return convert(skin, Model.SLIM, Model.CLASSIC);
    }

    /**
     * Converts a classic (Steve) skin into a slim (Alex) skin.
     * <p>
     * The four-pixel-wide arm faces are reduced to three pixels.
     * Some pixel information is necessarily lost in this conversion.
     *
     * @param skin the original 64x64 classic skin
     * @return a new slim skin texture
     * @throws NullPointerException     if skin is null
     * @throws IllegalArgumentException if the texture is not 64x64
     */
    public static SerializableBufferedImage toSlim(SerializableBufferedImage skin) {
        return convert(skin, Model.CLASSIC, Model.SLIM);
    }

    /**
     * Converts a skin between the specified player models.
     *
     * @param skin        the original skin texture
     * @param sourceModel the original player model
     * @param targetModel the desired player model
     * @return the converted skin texture
     */
    public static SerializableBufferedImage convert(
            SerializableBufferedImage skin,
            Model sourceModel,
            Model targetModel
    ) {
        Objects.requireNonNull(skin, "skin");
        Objects.requireNonNull(sourceModel, "sourceModel");
        Objects.requireNonNull(targetModel, "targetModel");

        if (skin.getBufferedImage().getWidth() != SKIN_WIDTH
                || skin.getBufferedImage().getHeight() != SKIN_HEIGHT) {
            throw new IllegalArgumentException(
                    "Expected a 64x64 skin, got "
                            + skin.getBufferedImage().getWidth() + "x" + skin.getBufferedImage().getHeight()
            );
        }

        BufferedImage result = copy(skin.getBufferedImage());

        if (sourceModel == targetModel) {
            return new SerializableBufferedImage(result);
        }

        /*
         * Keep the original texture separate from the destination.
         * The UV layouts overlap, so reading from the destination
         * while writing would corrupt source pixels.
         */
        BufferedImage source = copy(skin.getBufferedImage());

        // Right arm, base layer.
        convertArm(
                source, result,
                sourceModel, targetModel,
                Arm.RIGHT, Layer.BASE
        );

        // Right arm, outer layer (sleeve).
        convertArm(
                source, result,
                sourceModel, targetModel,
                Arm.RIGHT, Layer.OUTER
        );

        // Left arm, base layer.
        convertArm(
                source, result,
                sourceModel, targetModel,
                Arm.LEFT, Layer.BASE
        );

        // Left arm, outer layer (sleeve).
        convertArm(
                source, result,
                sourceModel, targetModel,
                Arm.LEFT, Layer.OUTER
        );

        return new SerializableBufferedImage(result);
    }

    private static void convertArm(
            BufferedImage source,
            BufferedImage destination,
            Model sourceModel,
            Model targetModel,
            Arm arm,
            Layer layer
    ) {
        ArmLayout sourceLayout = layout(sourceModel, arm, layer);
        ArmLayout targetLayout = layout(targetModel, arm, layer);

        /*
         * Clear the complete destination arm UV region first.
         * This prevents leftover pixels from the original layout
         * appearing in unused classic/slim texture coordinates.
         */
        clearRegion(
                destination,
                targetLayout.regionX,
                targetLayout.regionY,
                targetLayout.regionWidth,
                targetLayout.regionHeight
        );

        // The four vertical faces.
        copyScaled(
                source, destination,
                sourceLayout.side1X, sourceLayout.verticalY,
                sourceLayout.width, sourceLayout.verticalHeight,
                targetLayout.side1X, targetLayout.verticalY,
                targetLayout.width, targetLayout.verticalHeight
        );

        copyScaled(
                source, destination,
                sourceLayout.frontX, sourceLayout.verticalY,
                sourceLayout.width, sourceLayout.verticalHeight,
                targetLayout.frontX, targetLayout.verticalY,
                targetLayout.width, targetLayout.verticalHeight
        );

        copyScaled(
                source, destination,
                sourceLayout.side2X, sourceLayout.verticalY,
                sourceLayout.width, sourceLayout.verticalHeight,
                targetLayout.side2X, targetLayout.verticalY,
                targetLayout.width, targetLayout.verticalHeight
        );

        copyScaled(
                source, destination,
                sourceLayout.backX, sourceLayout.verticalY,
                sourceLayout.width, sourceLayout.verticalHeight,
                targetLayout.backX, targetLayout.verticalY,
                targetLayout.width, targetLayout.verticalHeight
        );

        // Top face.
        copyScaled(
                source, destination,
                sourceLayout.topX, sourceLayout.topY,
                sourceLayout.width, sourceLayout.capHeight,
                targetLayout.topX, targetLayout.topY,
                targetLayout.width, targetLayout.capHeight
        );

        // Bottom face.
        copyScaled(
                source, destination,
                sourceLayout.bottomX, sourceLayout.topY,
                sourceLayout.width, sourceLayout.capHeight,
                targetLayout.bottomX, targetLayout.topY,
                targetLayout.width, targetLayout.capHeight
        );
    }

    /**
     * Returns the UV layout for an arm and skin layer.
     * <p>
     * Minecraft arm texture layout:
     * <p>
     * Classic: four pixels per face.
     * Slim:    three pixels per face.
     * <p>
     * The horizontal face order is:
     * side 1, front, side 2, back.
     * <p>
     * Top and bottom faces occupy the first four rows of
     * the arm region, while vertical faces occupy the next
     * twelve rows.
     */
    private static ArmLayout layout(
            Model model,
            Arm arm,
            Layer layer
    ) {
        int width = model == Model.CLASSIC ? 4 : 3;

        final int regionX;
        final int regionY;
        final int topX;
        final int bottomX;
        final int verticalY;

        if (arm == Arm.RIGHT && layer == Layer.BASE) {
            regionX = 40;
            regionY = 16;
            topX = model == Model.CLASSIC ? 44 : 43;
            bottomX = model == Model.CLASSIC ? 48 : 46;
            verticalY = 20;
        } else if (arm == Arm.RIGHT && layer == Layer.OUTER) {
            regionX = 40;
            regionY = 32;
            topX = model == Model.CLASSIC ? 44 : 43;
            bottomX = model == Model.CLASSIC ? 48 : 46;
            verticalY = 36;
        } else if (arm == Arm.LEFT && layer == Layer.BASE) {
            regionX = 32;
            regionY = 48;
            topX = model == Model.CLASSIC ? 36 : 35;
            bottomX = model == Model.CLASSIC ? 40 : 38;
            verticalY = 52;
        } else {
            // Left arm outer layer.
            regionX = 48;
            regionY = 48;
            topX = model == Model.CLASSIC ? 52 : 51;
            bottomX = model == Model.CLASSIC ? 56 : 54;
            verticalY = 52;
        }

        //noinspection UnnecessaryLocalVariable
        int side1X = regionX;
        int frontX = regionX + width;
        int side2X = regionX + width * 2;
        int backX = regionX + width * 3;

        return new ArmLayout(
                regionX,
                regionY,
                width * 4,
                16,
                width,
                4,
                regionY,
                verticalY,
                12,
                side1X,
                frontX,
                side2X,
                backX,
                topX,
                bottomX
        );
    }

    /**
     * Copies a rectangular region while scaling its width and height.
     * <p>
     * Nearest-neighbor sampling preserves hard pixel edges and
     * supports both expanding and shrinking arm textures.
     */
    private static void copyScaled(
            BufferedImage source,
            BufferedImage destination,
            int sourceX,
            int sourceY,
            int sourceWidth,
            int sourceHeight,
            int destinationX,
            int destinationY,
            int destinationWidth,
            int destinationHeight
    ) {
        for (int y = 0; y < destinationHeight; y++) {
            int sourcePixelY = sourceY
                    + (int) ((long) y * sourceHeight / destinationHeight);

            for (int x = 0; x < destinationWidth; x++) {
                int sourcePixelX = sourceX
                        + (int) ((long) x * sourceWidth / destinationWidth);

                destination.setRGB(
                        destinationX + x,
                        destinationY + y,
                        source.getRGB(sourcePixelX, sourcePixelY)
                );
            }
        }
    }

    private static void clearRegion(
            BufferedImage image,
            int x,
            int y,
            int width,
            int height
    ) {
        int transparent = 0x00000000;

        for (int row = y; row < y + height; row++) {
            for (int column = x; column < x + width; column++) {
                image.setRGB(column, row, transparent);
            }
        }
    }

    private static BufferedImage copy(BufferedImage source) {
        BufferedImage destination = new BufferedImage(
                source.getWidth(),
                source.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                destination.setRGB(x, y, source.getRGB(x, y));
            }
        }

        return destination;
    }

    private enum Arm {
        RIGHT,
        LEFT
    }

    private enum Layer {
        BASE,
        OUTER
    }

    private record ArmLayout(
            int regionX,
            int regionY,
            int regionWidth,
            int regionHeight,
            int width,
            int capHeight,
            int topY,
            int verticalY,
            int verticalHeight,
            int side1X,
            int frontX,
            int side2X,
            int backX,
            int topX,
            int bottomX
    ) {
    }
}