package com.georgev22.skinoverlay.utilities.skin;

import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import com.georgev22.skinoverlay.utilities.config.SkinConfigurationFile;

import java.util.Objects;

public final class Overlay {
    private final String key;
    private SkinConfigurationFile configurationFile;
    private final Model model;
    private SerializableBufferedImage bufferedImage;
    private SkinParts skinParts;

    public Overlay(String key, Model model, SkinConfigurationFile configurationFile, SerializableBufferedImage bufferedImage) {
        this.key = key;
        this.model = model;
        this.configurationFile = configurationFile;
        this.bufferedImage = bufferedImage;
    }


    public String key() {
        return key;
    }

    public SkinConfigurationFile configurationFile() {
        return configurationFile;
    }

    public Model model() {
        return model;
    }

    public SerializableBufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setConfigurationFile(SkinConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    public void setBufferedImage(SerializableBufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public SkinParts getSkinParts() {
        if (skinParts == null) {
            if (bufferedImage == null) throw new IllegalArgumentException("BufferedImage cannot be null");
            skinParts = new SkinParts(bufferedImage, key);
        }

        return skinParts;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Overlay) obj;
        return Objects.equals(this.key, that.key) &&
                Objects.equals(this.configurationFile, that.configurationFile) &&
                Objects.equals(this.model, that.model) &&
                Objects.equals(this.bufferedImage, that.bufferedImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, configurationFile, model, bufferedImage);
    }

    @Override
    public String toString() {
        return "Overlay[" +
                "name=" + key + ", " +
                "configurationFile=" + configurationFile + ", " +
                "classic=" + model + ", " +
                "slim=" + bufferedImage + ']';
    }


}
