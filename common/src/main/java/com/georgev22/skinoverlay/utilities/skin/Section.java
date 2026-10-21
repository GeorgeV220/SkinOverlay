package com.georgev22.skinoverlay.utilities.skin;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents a rectangular section of a skin texture.
 *
 * @param x1     the starting X coordinate
 * @param y1     the starting Y coordinate
 * @param x2     the ending X coordinate
 * @param y2     the ending Y coordinate
 * @param width  the width of the section
 * @param height the height of the section
 */
public record Section(
        int x1,
        int y1,
        int x2,
        int y2,
        int width,
        int height
) {

    public Section(int x1, int y1, int x2, int y2) {
        this(x1, y1, x2, y2, x2 - x1, y2 - y1);
    }

    /**
     * Defines skin texture sections for both Minecraft player models.
     * <p>
     * Non-arm sections are identical between models. Arm and sleeve
     * sections use their respective model-specific UV coordinates.
     */
    public enum SectionType {

        // Head
        Head_Top(8, 0, 16, 8),
        Head_Bottom(16, 0, 24, 8),
        Head_Right(0, 8, 8, 16),
        Head_Front(8, 8, 16, 16),
        Head_Left(16, 8, 24, 16),
        Head_Back(24, 8, 32, 16),

        // Hat
        Hat_Top(40, 0, 48, 8),
        Hat_Bottom(48, 0, 56, 8),
        Hat_Right(32, 8, 40, 16),
        Hat_Front(40, 8, 48, 16),
        Hat_Left(48, 8, 56, 16),
        Hat_Back(56, 8, 64, 16),

        // Right Leg
        Right_Leg_Top(4, 16, 8, 20),
        Right_Leg_Bottom(8, 16, 12, 20),
        Right_Leg_Right(0, 20, 4, 32),
        Right_Leg_Front(4, 20, 8, 32),
        Right_Leg_Left(8, 20, 12, 32),
        Right_Leg_Back(12, 20, 16, 32),

        // Torso
        Torso_Top(20, 16, 28, 20),
        Torso_Bottom(28, 16, 36, 20),
        Torso_Right(16, 20, 20, 32),
        Torso_Front(20, 20, 28, 32),
        Torso_Left(36, 20, 40, 32),
        Torso_Back(28, 20, 36, 32),

        // Right Arm
        Right_Arm_Top(
                44, 16, 48, 20,
                44, 16, 47, 20
        ),
        Right_Arm_Bottom(
                48, 16, 52, 20,
                47, 16, 50, 20
        ),
        Right_Arm_Right(
                40, 20, 44, 32,
                40, 20, 44, 32
        ),
        Right_Arm_Front(
                44, 20, 48, 32,
                44, 20, 47, 32
        ),
        Right_Arm_Left(
                48, 20, 52, 32,
                47, 20, 51, 32
        ),
        Right_Arm_Back(
                52, 20, 56, 32,
                51, 20, 54, 32
        ),

        // Left Leg
        Left_Leg_Top(20, 48, 24, 52),
        Left_Leg_Bottom(24, 48, 28, 52),
        Left_Leg_Right(16, 52, 20, 64),
        Left_Leg_Front(20, 52, 24, 64),
        Left_Leg_Left(24, 52, 28, 64),
        Left_Leg_Back(28, 52, 32, 64),

        // Left Arm
        Left_Arm_Top(
                36, 48, 40, 52,
                36, 48, 39, 52
        ),
        Left_Arm_Bottom(
                40, 48, 44, 52,
                39, 48, 42, 52
        ),
        Left_Arm_Right(
                32, 52, 36, 64,
                32, 52, 36, 64
        ),
        Left_Arm_Front(
                36, 52, 40, 64,
                36, 52, 39, 64
        ),
        Left_Arm_Left(
                40, 52, 44, 64,
                39, 52, 43, 64
        ),
        Left_Arm_Back(
                44, 52, 48, 64,
                43, 52, 46, 64
        ),

        // Right Pants Leg
        Right_Pants_Leg_Top(4, 32, 8, 36),
        Right_Pants_Leg_Bottom(8, 32, 12, 36),
        Right_Pants_Leg_Right(0, 36, 4, 48),
        Right_Pants_Leg_Front(4, 36, 8, 48),
        Right_Pants_Leg_Left(8, 36, 12, 48),
        Right_Pants_Leg_Back(12, 36, 16, 48),

        // Jacket
        Jacket_Top(20, 32, 28, 36),
        Jacket_Bottom(28, 32, 36, 36),
        Jacket_Right(16, 36, 20, 48),
        Jacket_Front(20, 36, 28, 48),
        Jacket_Left(36, 36, 40, 48),
        Jacket_Back(28, 36, 36, 48),

        // Right Sleeve
        Right_Sleeve_Top(
                44, 32, 48, 36,
                44, 32, 47, 36
        ),
        Right_Sleeve_Bottom(
                48, 32, 52, 36,
                47, 32, 50, 36
        ),
        Right_Sleeve_Right(
                40, 36, 44, 48,
                40, 36, 44, 48
        ),
        Right_Sleeve_Front(
                44, 36, 48, 48,
                44, 36, 47, 48
        ),
        Right_Sleeve_Left(
                48, 36, 52, 48,
                47, 36, 51, 48
        ),
        Right_Sleeve_Back(
                52, 36, 56, 48,
                51, 36, 54, 48
        ),

        // Left Pants Leg
        Left_Pants_Leg_Top(4, 48, 8, 52),
        Left_Pants_Leg_Bottom(8, 48, 12, 52),
        Left_Pants_Leg_Right(0, 52, 4, 64),
        Left_Pants_Leg_Front(4, 52, 8, 64),
        Left_Pants_Leg_Left(8, 52, 12, 64),
        Left_Pants_Leg_Back(12, 52, 16, 64),

        // Left Sleeve
        Left_Sleeve_Top(
                52, 48, 56, 52,
                52, 48, 55, 52
        ),
        Left_Sleeve_Bottom(
                56, 48, 60, 52,
                55, 48, 58, 52
        ),
        Left_Sleeve_Right(
                48, 52, 52, 64,
                48, 52, 52, 64
        ),
        Left_Sleeve_Front(
                52, 52, 56, 64,
                52, 52, 55, 64
        ),
        Left_Sleeve_Left(
                56, 52, 60, 64,
                55, 52, 59, 64
        ),
        Left_Sleeve_Back(
                60, 52, 64, 64,
                59, 52, 62, 64
        ),
        ;

        private final Section classic;
        private final Section slim;

        SectionType(int x1, int y1, int x2, int y2) {
            this(x1, y1, x2, y2, x1, y1, x2, y2);
        }

        SectionType(
                int classicX1,
                int classicY1,
                int classicX2,
                int classicY2,
                int slimX1,
                int slimY1,
                int slimX2,
                int slimY2
        ) {
            this.classic = new Section(
                    classicX1, classicY1, classicX2, classicY2
            );
            this.slim = new Section(
                    slimX1, slimY1, slimX2, slimY2
            );
        }

        /**
         * Gets the classic section for backwards compatibility.
         *
         * @return the classic section
         */
        @Deprecated
        public @NotNull Section getSection() {
            return classic;
        }

        /**
         * Gets the section for the specified player model.
         *
         * @param model the player model
         * @return the model-specific section
         */
        public @NotNull Section getSection(@NotNull Model model) {
            return switch (model) {
                case CLASSIC -> classic;
                case SLIM -> slim;
            };
        }

        public static @NotNull Optional<SectionType> getByName(
                String name
        ) {
            return Arrays.stream(values())
                    .filter(type -> type.name().equalsIgnoreCase(name))
                    .findFirst();
        }

        public static @NotNull SectionType[] filterByPrefix(
                String prefix
        ) {
            return Arrays.stream(values())
                    .filter(type -> type.name().startsWith(prefix))
                    .toArray(SectionType[]::new);
        }
    }
}