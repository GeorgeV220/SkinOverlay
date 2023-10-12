package com.georgev22.skinoverlay.handler;

import com.georgev22.library.utilities.EntityManager.Entity;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.handler.skin.SkinParts;
import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;

@ApiStatus.NonExtendable
public class Skin extends Entity implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    private SProperty property;
    private SkinParts skinParts;

    public Skin(UUID uuid) {
        super(uuid);
        addCustomData("entity_id", uuid.toString());
        this.skinParts = new SkinParts();
    }

    public Skin(UUID uuid, SProperty sProperty, String skinName) {
        super(uuid);
        addCustomData("entity_id", uuid.toString());
        addCustomData("property", this.property = sProperty);
        try {
            addCustomData("skinParts", this.skinParts = new SkinParts(
                    new SerializableBufferedImage(SkinOverlay.getInstance().getSkinHandler().getSkinImage(sProperty)),
                    skinName
            ));
        } catch (IOException e) {
            SkinOverlay.getInstance().getLogger().log(Level.SEVERE, "Could not load skin " + skinName, e);
            addCustomData("skinParts", this.skinParts = new SkinParts(null, skinName));
        }
    }

    public Skin(UUID uuid, SProperty sProperty, SkinParts skinParts) {
        super(uuid);
        addCustomData("entity_id", uuid.toString());
        addCustomData("property", this.property = sProperty);
        addCustomData("skinParts", this.skinParts = skinParts);
    }

    public @Nullable SProperty skinProperty() {
        return getCustomData("property") != null ? getCustomData("property") : property;
    }

    public SkinParts skinParts() {
        return getCustomData("skinParts") != null ? getCustomData("skinParts") : skinParts;
    }

    public void setSkinParts(SkinParts skinParts) {
        addCustomData("skinParts", this.skinParts = skinParts);
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
                ", skinParts=" + skinParts +
                ", skinURL=" + skinURL() +
                '}';
    }
}
