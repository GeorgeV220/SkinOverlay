package com.georgev22.skinoverlay.utilities.interfaces;

import java.awt.Image;
import java.io.IOException;

@FunctionalInterface
public interface ImageSupplier {
    Image get() throws IOException;
}
