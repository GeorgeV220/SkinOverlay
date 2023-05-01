package com.georgev22.skinoverlay.handler;

import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Base64;

public class Skin implements Serializable {

    /**
     * Converts a Base64-encoded string to a {@link Skin} object.
     *
     * @param bytes the Base64-encoded string to convert
     * @return the Skin object
     */
    public static Skin getSkin(@NotNull String bytes) {
        return (Skin) Utilities.getObject(bytes);
    }

    /**
     * Converts a {@link Skin} object to a Base64-encoded string.
     *
     * @param skin the Skin object to convert
     * @return the Base64-encoded string
     */
    public static String skinToBytes(Skin skin) {
        return Utilities.objectToString(skin);
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private final SProperty property;
    private SkinOptions skinOptions = new SkinOptions("default");

    public Skin(SProperty sProperty) {
        this.property = sProperty;
    }

    public Skin(SProperty sProperty, String skinName) {
        this.property = sProperty;
        this.skinOptions = new SkinOptions(skinName);
    }

    public Skin(SProperty sProperty, SkinOptions skinOptions) {
        this.property = sProperty;
        this.skinOptions = skinOptions;
    }

    public SProperty getProperty() {
        return property;
    }

    public SkinOptions getSkinOptions() {
        return skinOptions;
    }

    public void setSkinOptions(SkinOptions skinOptions) {
        this.skinOptions = skinOptions;
    }

    public @Nullable String getSkinURL() {
        return new JsonParser().parse(new String(Base64.getDecoder().decode(property.value())))
                .getAsJsonObject()
                .getAsJsonObject("textures")
                .getAsJsonObject("SKIN")
                .get("url")
                .getAsString();
    }
}
