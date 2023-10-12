package com.georgev22.skinoverlay.handler.skin;

import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import lombok.Getter;

import java.awt.*;
import java.awt.image.BufferedImage;

@Getter
public class MinecraftSkinRenderer {
    private SerializableBufferedImage fullSkinImage;

    private final Part[] parts;

    public MinecraftSkinRenderer(Part... parts) {
        this.parts = parts;
    }

    public void createFullSkinImage() {
        fullSkinImage = new SerializableBufferedImage(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));

        Graphics g = fullSkinImage.getBufferedImage().getGraphics();

        for (Part part : parts) {
            g.drawImage(part.image().getBufferedImage(), part.x(), part.y(), part.width(), part.height(), null);
        }

        g.dispose();
    }

}
