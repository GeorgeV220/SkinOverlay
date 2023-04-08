package com.georgev22.skinoverlay.utilities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class SkinOptions implements Serializable {

    /**
     * The name of the skin.
     */
    private final String skinName;

    /**
     * The URL of the skin image.
     */
    private final String url;

    /**
     * Whether the skin has a cape.
     */
    private final boolean cape;

    /**
     * Whether the skin has a jacket.
     */
    private final boolean jacket;

    /**
     * Whether the skin has a left sleeve.
     */
    private final boolean left_sleeve;

    /**
     * Whether the skin has a right sleeve.
     */
    private final boolean right_sleeve;

    /**
     * Whether the skin has left pants.
     */
    private final boolean left_pants;

    /**
     * Whether the skin has right pants.
     */
    private final boolean right_pants;

    /**
     * Whether the skin has a hat.
     */
    private final boolean hat;

    /**
     * Creates a new SkinOptions instance with the given skin name.
     *
     * @param skinName the name of the skin
     */
    public SkinOptions(@NotNull String skinName) {
        this.skinName = skinName;
        this.url = null;
        this.cape = false;
        this.jacket = false;
        this.left_sleeve = false;
        this.right_sleeve = false;
        this.left_pants = false;
        this.right_pants = false;
        this.hat = false;
    }

    /**
     * Creates a new SkinOptions instance with the given URL and flags.
     *
     * @param url          the URL of the skin image
     * @param cape         whether the skin has a cape
     * @param jacket       whether the skin has a jacket
     * @param left_sleeve  whether the skin has a left sleeve
     * @param right_sleeve whether the skin has a right sleeve
     * @param left_pants   whether the skin has left pants
     * @param right_pants  whether the skin has right pants
     * @param hat          whether the skin has a hat
     */
    public SkinOptions(String url, boolean cape, boolean jacket, boolean left_sleeve, boolean right_sleeve, boolean left_pants, boolean right_pants, boolean hat) {
        this.skinName = "custom";
        this.url = url;
        this.cape = cape;
        this.jacket = jacket;
        this.left_sleeve = left_sleeve;
        this.right_sleeve = right_sleeve;
        this.left_pants = left_pants;
        this.right_pants = right_pants;
        this.hat = hat;
    }

    /**
     * Returns a byte that represents the flags for the skin.
     *
     * @return a byte that represents the flags for the skin
     */
    public byte getFlags() {
        return !skinName.equalsIgnoreCase("custom") ? skinName.equalsIgnoreCase("default") | skinName.equalsIgnoreCase("custom2") ?
                (byte) (Flags.CAPE.flag | Flags.JACKET.flag | Flags.LEFT_SLEEVE.flag | Flags.RIGHT_SLEEVE.flag | Flags.LEFT_PANTS.flag | Flags.RIGHT_PANTS.flag | Flags.HEAD.flag) :
                (byte) ((OptionsUtil.OVERLAY_CAPE.getBooleanValue(skinName) ? Flags.CAPE.flag : Flags.NOTHING.flag) |
                        (OptionsUtil.OVERLAY_JACKET.getBooleanValue(skinName) ? Flags.JACKET.flag : Flags.NOTHING.flag) |
                        (OptionsUtil.OVERLAY_LEFT_SLEEVE.getBooleanValue(skinName) ? Flags.LEFT_SLEEVE.flag : Flags.NOTHING.flag) |
                        (OptionsUtil.OVERLAY_RIGHT_SLEEVE.getBooleanValue(skinName) ? Flags.RIGHT_SLEEVE.flag : Flags.NOTHING.flag) |
                        (OptionsUtil.OVERLAY_LEFT_PANTS.getBooleanValue(skinName) ? Flags.LEFT_PANTS.flag : Flags.NOTHING.flag) |
                        (OptionsUtil.OVERLAY_RIGHT_PANTS.getBooleanValue(skinName) ? Flags.RIGHT_PANTS.flag : Flags.NOTHING.flag) |
                        (OptionsUtil.OVERLAY_HAT.getBooleanValue(skinName) ? Flags.HEAD.flag : Flags.NOTHING.flag)) : (byte) ((cape ? Flags.CAPE.flag : Flags.NOTHING.flag) |
                (jacket ? Flags.JACKET.flag : Flags.NOTHING.flag) |
                (left_sleeve ? Flags.LEFT_SLEEVE.flag : Flags.NOTHING.flag) |
                (right_sleeve ? Flags.RIGHT_SLEEVE.flag : Flags.NOTHING.flag) |
                (left_pants ? Flags.LEFT_PANTS.flag : Flags.NOTHING.flag) |
                (right_pants ? Flags.RIGHT_PANTS.flag : Flags.NOTHING.flag) |
                (hat ? Flags.HEAD.flag : Flags.NOTHING.flag));
    }

    /**
     * Returns the name of the skin.
     *
     * @return the name of the skin
     */
    public String getSkinName() {
        return skinName;
    }

    /**
     * Returns the URL of the skin image.
     *
     * @return the URL of the skin image
     */
    public String getUrl() {
        return url;
    }

    @AllArgsConstructor
    @Getter
    public enum Flags {
        /**
         * Represents the absence of any flag.
         */
        NOTHING((byte) 0x00),

        /**
         * Represents the presence of a cape flag.
         */
        CAPE((byte) 0x01),

        /**
         * Represents the presence of a jacket flag.
         */
        JACKET((byte) 0x02),

        /**
         * Represents the presence of a left sleeve flag.
         */
        LEFT_SLEEVE((byte) 0x04),

        /**
         * Represents the presence of a right sleeve flag.
         */
        RIGHT_SLEEVE((byte) 0x08),

        /**
         * Represents the presence of left pants flag.
         */
        LEFT_PANTS((byte) 0x10),
        /**
         * Represents the presence of right pants flag.
         */
        RIGHT_PANTS((byte) 0x20),
        /**
         * Represents the presence of head flag.
         */
        HEAD((byte) 0x40);

        private final byte flag;

    }
}