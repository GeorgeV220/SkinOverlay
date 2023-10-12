package com.georgev22.skinoverlay.utilities;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.*;

@Getter
public class SerializableBufferedImage implements Serializable {
    private transient BufferedImage bufferedImage;

    public SerializableBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    @Serial
    private void writeObject(@NotNull ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[] pixels = new int[width * height];
        bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);
        out.writeInt(width);
        out.writeInt(height);
        out.writeObject(pixels);
    }

    @Serial
    private void readObject(@NotNull ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int width = in.readInt();
        int height = in.readInt();
        int[] pixels = (int[]) in.readObject();
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);
    }

}
