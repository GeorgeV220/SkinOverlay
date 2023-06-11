package com.georgev22.skinoverlay.handler;

import com.georgev22.library.utilities.EntityManager.Entity;
import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Base64;
import java.util.UUID;

@ApiStatus.NonExtendable
public class Skin extends Entity implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    private SProperty property;
    private SkinOptions skinOptions;

    public Skin(UUID uuid) {
        super(uuid);
        addCustomData("entity_id", uuid.toString());
        this.skinOptions = new SkinOptions();
    }

    public Skin(UUID uuid, SProperty sProperty) {
        super(uuid);
        addCustomData("entity_id", uuid.toString());
        addCustomData("property", this.property = sProperty);
        addCustomData("skinOptions", this.skinOptions = new SkinOptions());
    }

    public Skin(UUID uuid, SProperty sProperty, String skinName) {
        super(uuid);
        addCustomData("entity_id", uuid.toString());
        addCustomData("property", this.property = sProperty);
        addCustomData("skinOptions", this.skinOptions = new SkinOptions(skinName));
    }

    public Skin(UUID uuid, SProperty sProperty, SkinOptions skinOptions) {
        super(uuid);
        addCustomData("entity_id", uuid.toString());
        addCustomData("property", this.property = sProperty);
        addCustomData("skinOptions", this.skinOptions = skinOptions);
    }

    public @Nullable SProperty skinProperty() {
        return getCustomData("property") != null ? getCustomData("property") : property;
    }

    public SkinOptions skinOptions() {
        return getCustomData("skinOptions") != null ? getCustomData("skinOptions") : skinOptions;
    }

    public void setSkinOptions(SkinOptions skinOptions) {
        addCustomData("skinOptions", this.skinOptions = skinOptions);
    }

    public void setProperty(SProperty property) {
        addCustomData("property", this.property = property);
    }

    public @Nullable String skinURL() {
        return JsonParser.parseString(new String(Base64.getDecoder().decode(property.value())))
                .getAsJsonObject()
                .getAsJsonObject("textures")
                .getAsJsonObject("SKIN")
                .get("url")
                .getAsString();
    }

    @Override
    public String toString() {
        return "Skin{" +
                "property=" + property +
                ", skinOptions=" + skinOptions +
                ", skinURL=" + skinURL() +
                '}';
    }
}
