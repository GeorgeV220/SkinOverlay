package com.georgev22.skinoverlay.utilities.skin;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.maps.HashObjectMap;
import com.georgev22.skinoverlay.maps.ObjectMap;
import com.georgev22.skinoverlay.maps.UnmodifiableObjectMap;
import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Map;

/**
 * Represents different sections and parts of a player's skin.
 */
public class SkinParts {

    private static final SerializableBufferedImage steveSkin;

    static {
        try {
            @SuppressWarnings("deprecation")
            URL url = new URL("https://s.namemc.com/i/12b92a9206470fe2.png");
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            try (InputStream stream = url.openStream()) {
                byte[] buffer = new byte[4096];

                while (true) {
                    int bytesRead = stream.read(buffer);
                    if (bytesRead < 0) {
                        break;
                    }
                    output.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                steveSkin = null;
                throw new RuntimeException(e);
            }
            steveSkin = new SerializableBufferedImage(ImageIO.read(new ByteArrayInputStream(output.toByteArray())));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SerializableBufferedImage fullSkin;

    private String skinName;

    private final ObjectMap<String, Part> parts;

    /**
     * Default constructor for SkinParts using the Steve skin.
     */
    public SkinParts() {
        this(steveSkin, "Steve");
    }

    /**
     * Constructor for SkinParts with a specified full skin and skin name.
     *
     * @param fullSkin The full skin image.
     * @param skinName The name of the skin.
     */
    public SkinParts(SerializableBufferedImage fullSkin, String skinName) {
        this.parts = new HashObjectMap<>();
        if (fullSkin != null) {
            this.fullSkin = fullSkin;
        }
        if (skinName != null) {
            this.skinName = skinName;
        }
        createParts();
    }

    /**
     * Creates different parts based on the full skin image.
     */
    public void createParts() {
        if (fullSkin == null) {
            return;
        }
        if (isOldSkin()) {
            SerializableBufferedImage fullSkin = this.fullSkin;
            this.fullSkin = convertSkin(fullSkin);
        }
        parts.put("Head_Top", createPart(Section.SectionType.Head_Top.getSection()));
        parts.put("Head_Bottom", createPart(Section.SectionType.Head_Bottom.getSection()));
        parts.put("Head_Right", createPart(Section.SectionType.Head_Right.getSection()));
        parts.put("Head_Front", createPart(Section.SectionType.Head_Front.getSection()));
        parts.put("Head_Left", createPart(Section.SectionType.Head_Left.getSection()));
        parts.put("Head_Back", createPart(Section.SectionType.Head_Back.getSection()));
        parts.put("Hat_Top", createPart(Section.SectionType.Hat_Top.getSection()));
        parts.put("Hat_Bottom", createPart(Section.SectionType.Hat_Bottom.getSection()));
        parts.put("Hat_Right", createPart(Section.SectionType.Hat_Right.getSection()));
        parts.put("Hat_Front", createPart(Section.SectionType.Hat_Front.getSection()));
        parts.put("Hat_Left", createPart(Section.SectionType.Hat_Left.getSection()));
        parts.put("Hat_Back", createPart(Section.SectionType.Hat_Back.getSection()));
        parts.put("Right_Leg_Top", createPart(Section.SectionType.Right_Leg_Top.getSection()));
        parts.put("Right_Leg_Bottom", createPart(Section.SectionType.Right_Leg_Bottom.getSection()));
        parts.put("Right_Leg_Right", createPart(Section.SectionType.Right_Leg_Right.getSection()));
        parts.put("Right_Leg_Front", createPart(Section.SectionType.Right_Leg_Front.getSection()));
        parts.put("Right_Leg_Left", createPart(Section.SectionType.Right_Leg_Left.getSection()));
        parts.put("Right_Leg_Back", createPart(Section.SectionType.Right_Leg_Back.getSection()));
        parts.put("Torso_Top", createPart(Section.SectionType.Torso_Top.getSection()));
        parts.put("Torso_Bottom", createPart(Section.SectionType.Torso_Bottom.getSection()));
        parts.put("Torso_Right", createPart(Section.SectionType.Torso_Right.getSection()));
        parts.put("Torso_Front", createPart(Section.SectionType.Torso_Front.getSection()));
        parts.put("Torso_Left", createPart(Section.SectionType.Torso_Left.getSection()));
        parts.put("Torso_Back", createPart(Section.SectionType.Torso_Back.getSection()));
        parts.put("Right_Arm_Top", createPart(Section.SectionType.Right_Arm_Top.getSection()));
        parts.put("Right_Arm_Bottom", createPart(Section.SectionType.Right_Arm_Bottom.getSection()));
        parts.put("Right_Arm_Right", createPart(Section.SectionType.Right_Arm_Right.getSection()));
        parts.put("Right_Arm_Front", createPart(Section.SectionType.Right_Arm_Front.getSection()));
        parts.put("Right_Arm_Left", createPart(Section.SectionType.Right_Arm_Left.getSection()));
        parts.put("Right_Arm_Back", createPart(Section.SectionType.Right_Arm_Back.getSection()));
        parts.put("Left_Leg_Top", createPart(Section.SectionType.Left_Leg_Top.getSection()));
        parts.put("Left_Leg_Bottom", createPart(Section.SectionType.Left_Leg_Bottom.getSection()));
        parts.put("Left_Leg_Right", createPart(Section.SectionType.Left_Leg_Right.getSection()));
        parts.put("Left_Leg_Front", createPart(Section.SectionType.Left_Leg_Front.getSection()));
        parts.put("Left_Leg_Left", createPart(Section.SectionType.Left_Leg_Left.getSection()));
        parts.put("Left_Leg_Back", createPart(Section.SectionType.Left_Leg_Back.getSection()));
        parts.put("Left_Arm_Top", createPart(Section.SectionType.Left_Arm_Top.getSection()));
        parts.put("Left_Arm_Bottom", createPart(Section.SectionType.Left_Arm_Bottom.getSection()));
        parts.put("Left_Arm_Right", createPart(Section.SectionType.Left_Arm_Right.getSection()));
        parts.put("Left_Arm_Front", createPart(Section.SectionType.Left_Arm_Front.getSection()));
        parts.put("Left_Arm_Left", createPart(Section.SectionType.Left_Arm_Left.getSection()));
        parts.put("Left_Arm_Back", createPart(Section.SectionType.Left_Arm_Back.getSection()));
        parts.put("Right_Pants_Leg_Top", createPart(Section.SectionType.Right_Pants_Leg_Top.getSection()));
        parts.put("Right_Pants_Leg_Bottom", createPart(Section.SectionType.Right_Pants_Leg_Bottom.getSection()));
        parts.put("Right_Pants_Leg_Right", createPart(Section.SectionType.Right_Pants_Leg_Right.getSection()));
        parts.put("Right_Pants_Leg_Front", createPart(Section.SectionType.Right_Pants_Leg_Front.getSection()));
        parts.put("Right_Pants_Leg_Left", createPart(Section.SectionType.Right_Pants_Leg_Left.getSection()));
        parts.put("Right_Pants_Leg_Back", createPart(Section.SectionType.Right_Pants_Leg_Back.getSection()));
        parts.put("Jacket_Top", createPart(Section.SectionType.Jacket_Top.getSection()));
        parts.put("Jacket_Bottom", createPart(Section.SectionType.Jacket_Bottom.getSection()));
        parts.put("Jacket_Right", createPart(Section.SectionType.Jacket_Right.getSection()));
        parts.put("Jacket_Front", createPart(Section.SectionType.Jacket_Front.getSection()));
        parts.put("Jacket_Left", createPart(Section.SectionType.Jacket_Left.getSection()));
        parts.put("Jacket_Back", createPart(Section.SectionType.Jacket_Back.getSection()));
        parts.put("Right_Sleeve_Top", createPart(Section.SectionType.Right_Sleeve_Top.getSection()));
        parts.put("Right_Sleeve_Bottom", createPart(Section.SectionType.Right_Sleeve_Bottom.getSection()));
        parts.put("Right_Sleeve_Right", createPart(Section.SectionType.Right_Sleeve_Right.getSection()));
        parts.put("Right_Sleeve_Front", createPart(Section.SectionType.Right_Sleeve_Front.getSection()));
        parts.put("Right_Sleeve_Left", createPart(Section.SectionType.Right_Sleeve_Left.getSection()));
        parts.put("Right_Sleeve_Back", createPart(Section.SectionType.Right_Sleeve_Back.getSection()));
        parts.put("Left_Pants_Leg_Top", createPart(Section.SectionType.Left_Pants_Leg_Top.getSection()));
        parts.put("Left_Pants_Leg_Bottom", createPart(Section.SectionType.Left_Pants_Leg_Bottom.getSection()));
        parts.put("Left_Pants_Leg_Right", createPart(Section.SectionType.Left_Pants_Leg_Right.getSection()));
        parts.put("Left_Pants_Leg_Front", createPart(Section.SectionType.Left_Pants_Leg_Front.getSection()));
        parts.put("Left_Pants_Leg_Left", createPart(Section.SectionType.Left_Pants_Leg_Left.getSection()));
        parts.put("Left_Pants_Leg_Back", createPart(Section.SectionType.Left_Pants_Leg_Back.getSection()));
        parts.put("Left_Sleeve_Top", createPart(Section.SectionType.Left_Sleeve_Top.getSection()));
        parts.put("Left_Sleeve_Bottom", createPart(Section.SectionType.Left_Sleeve_Bottom.getSection()));
        parts.put("Left_Sleeve_Right", createPart(Section.SectionType.Left_Sleeve_Right.getSection()));
        parts.put("Left_Sleeve_Front", createPart(Section.SectionType.Left_Sleeve_Front.getSection()));
        parts.put("Left_Sleeve_Left", createPart(Section.SectionType.Left_Sleeve_Left.getSection()));
        parts.put("Left_Sleeve_Back", createPart(Section.SectionType.Left_Sleeve_Back.getSection()));
    }

    /**
     * Creates a part image based on the specified section.
     *
     * @param section The section for which the part image is created.
     * @return A Part object representing the created part.
     */
    private @NotNull Part createPart(@NotNull Section section) {
        int x = section.x1();
        int y = section.y1();
        int width = section.width();
        int height = section.height();

        BufferedImage partImage = fullSkin.getBufferedImage().getSubimage(x, y, width, height);
        int[] pixelData = partImage.getRGB(0, 0, width, height, null, 0, width);

        boolean isAreaTransparent = true;
        for (int pixel : pixelData) {
            int alpha = (pixel >> 24) & 0xFF;
            if (alpha > 0) {
                isAreaTransparent = false;
                break;
            }
        }

        String partName = section.getClass().getSimpleName();
        return new Part(partName, new SerializableBufferedImage(partImage), x, y, width, height, isAreaTransparent);
    }

    /**
     * Retrieves a specific part by name.
     *
     * @param partName The name of the part.
     * @return The Part object corresponding to the specified name.
     */
    public Part getPart(String partName) {
        return parts.get(partName);
    }

    /**
     * Saves images of all parts to the specified output directory.
     *
     * @param outputDirectory The directory to save the part images.
     */
    public void savePartImages(File outputDirectory) {
        for (Part part : parts.values()) {
            String fileName = part.name() + ".png";
            try {
                ImageIO.write(part.image().getBufferedImage(), "png", new File(outputDirectory, fileName));
            } catch (IOException e) {
                System.out.println("Failed to save part image: " + fileName);
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Checks if the skin is an old format (64x32).
     *
     * @return True if the skin is in the old format, false otherwise.
     */
    public boolean isOldSkin() {
        BufferedImage image = this.fullSkin.getBufferedImage();
        return image.getWidth() == 64 && image.getHeight() == 32;
    }

    /**
     * Retrieves the full skin image.
     *
     * @return The full skin image.
     */
    public SerializableBufferedImage getFullSkin() {
        return fullSkin;
    }

    /**
     * Converts an old format skin to the new format (64x64).
     *
     * @param original The original skin image in old format.
     * @return The converted skin image in new format.
     */
    public SerializableBufferedImage convertSkin(@NotNull SerializableBufferedImage original) {
        BufferedImage orig = original.getBufferedImage();
        BufferedImage newImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);

        // Copy top half of the original image
        newImage.getGraphics().drawImage(orig, 0, 0, 64, 32, null);

        // Copy left leg from the original image
        BufferedImage leftLeg = orig.getSubimage(0, 16, 16, 16);
        newImage.getGraphics().drawImage(leftLeg, 16, 48, null);

        // Copy right leg from the original image
        BufferedImage rightLeg = orig.getSubimage(40, 16, 16, 16);
        newImage.getGraphics().drawImage(rightLeg, 32, 48, null);

        return new SerializableBufferedImage(newImage);
    }

    /**
     * Retrieves the name of the skin.
     *
     * @return The name of the skin.
     */
    public String getSkinName() {
        return skinName;
    }

    /**
     * Retrieves the parts of the skin.
     *
     * @return A map containing the parts of the skin.
     */
    public UnmodifiableObjectMap<String, Part> getParts() {
        return new UnmodifiableObjectMap<>(parts);
    }

    /**
     * Serializes the SkinParts object to a map for YAML serialization.
     *
     * @return A map containing the serialized data.
     */
    public @NotNull Map<String, Object> serialize() {
        return Map.of("bufferedImage", this.fullSkin, "skinName", this.skinName);
    }

    /**
     * Deserializes the YAML-compatible map to a SkinParts object.
     *
     * @param map The YAML-compatible map containing skin information.
     * @return The deserialized SkinParts object.
     */
    public static @NotNull SkinParts deserialize(@NotNull Map<String, Object> map) {
        return new SkinParts((SerializableBufferedImage) map.get("bufferedImage"), (String) map.get("skinName"));
    }


    /**
     * Serializes the SkinParts object to a JSON string.
     *
     * @return The serialized JSON string.
     */
    public String toJson() {
        return SkinOverlay.getInstance().getGson().toJson(this);
    }

    /**
     * Deserializes a JSON string to a SkinParts object.
     *
     * @param json The JSON string to deserialize.
     * @return The deserialized SkinParts object.
     */
    public static SkinParts fromJson(String json) {
        return SkinOverlay.getInstance().getGson().fromJson(json, SkinParts.class);
    }

    /**
     * Returns a string representation of the SkinParts object.
     *
     * @return A string representation of the SkinParts object.
     */
    @Override
    public String toString() {
        return "SkinParts{" +
                "fullSkin=" + fullSkin +
                ", parts=" + parts +
                '}';
    }
}