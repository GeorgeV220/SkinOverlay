package com.georgev22.skinoverlay.utilities;

import com.georgev22.skinoverlay.SkinOverlay;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


public class SkinOverlays {

    public static byte getFlags(@NotNull String skinName) {
        SkinOverlay.getInstance().getLogger().info(OptionsUtil.OVERLAY_JACKET.getBooleanValue(skinName) + " " + String.format(OptionsUtil.OVERLAY_JACKET.getPath(), skinName));
        return skinName.equalsIgnoreCase("default") ?
                (byte) (Overlays.CAPE.flag | Overlays.JACKET.flag | Overlays.LEFT_SLEEVE.flag | Overlays.RIGHT_SLEEVE.flag | Overlays.LEFT_PANTS.flag | Overlays.RIGHT_PANTS.flag | Overlays.HEAD.flag) :
                (byte) ((OptionsUtil.OVERLAY_CAPE.getBooleanValue(skinName) ? Overlays.CAPE.flag : Overlays.NOTHING.flag) |
                        (OptionsUtil.OVERLAY_JACKET.getBooleanValue(skinName) ? Overlays.JACKET.flag : Overlays.NOTHING.flag) |
                        (OptionsUtil.OVERLAY_LEFT_SLEEVE.getBooleanValue(skinName) ? Overlays.LEFT_SLEEVE.flag : Overlays.NOTHING.flag) |
                        (OptionsUtil.OVERLAY_RIGHT_SLEEVE.getBooleanValue(skinName) ? Overlays.RIGHT_SLEEVE.flag : Overlays.NOTHING.flag) |
                        (OptionsUtil.OVERLAY_LEFT_PANTS.getBooleanValue(skinName) ? Overlays.LEFT_PANTS.flag : Overlays.NOTHING.flag) |
                        (OptionsUtil.OVERLAY_RIGHT_PANTS.getBooleanValue(skinName) ? Overlays.RIGHT_PANTS.flag : Overlays.NOTHING.flag) |
                        (OptionsUtil.OVERLAY_HAT.getBooleanValue(skinName) ? Overlays.HEAD.flag : Overlays.NOTHING.flag));
    }

    @AllArgsConstructor
    @Getter
    public enum Overlays {
        NOTHING((byte) 0x00),
        CAPE((byte) 0x01),
        JACKET((byte) 0x02),
        LEFT_SLEEVE((byte) 0x04),
        RIGHT_SLEEVE((byte) 0x08),
        LEFT_PANTS((byte) 0x10),
        RIGHT_PANTS((byte) 0x20),
        HEAD((byte) 0x40);

        private final byte flag;

    }
}