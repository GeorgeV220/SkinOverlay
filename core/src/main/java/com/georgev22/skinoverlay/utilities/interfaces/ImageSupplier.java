package com.georgev22.skinoverlay.utilities.interfaces;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A functional interface that supplies an image.
 */
@FunctionalInterface
public interface ImageSupplier {
    /**
     * Gets an image.
     *
     * @return the image supplied by this function
     * @throws IOException if an I/O error occurs while getting the image
     */
    BufferedImage get() throws IOException;
}
