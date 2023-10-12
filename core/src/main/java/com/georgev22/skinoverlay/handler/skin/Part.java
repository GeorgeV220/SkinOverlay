package com.georgev22.skinoverlay.handler.skin;

import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;

import java.io.Serializable;

public record Part(String name, SerializableBufferedImage image, int x, int y, int width, int height,
                   boolean isEmpty) implements Serializable {

    @Override
    public String toString() {
        return "Part{" +
                "name='" + name + '\'' +
                ", image=" + image +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", isEmpty=" + isEmpty +
                '}';
    }

}
