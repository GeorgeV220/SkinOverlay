package com.georgev22.skinoverlay.handler.skin;

import com.georgev22.library.yaml.serialization.ConfigurationSerializable;
import com.georgev22.library.yaml.serialization.SerializableAs;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.handler.skin.Section.*;
import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents different sections and parts of a player's skin.
 */
@Getter
@SerializableAs("SkinParts")
public class SkinParts implements ConfigurationSerializable {

    private static final SerializableBufferedImage steveSkin;

    static {
        try {
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

    private final Map<String, Part> parts;

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
        this.parts = new HashMap<>();
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
        parts.put("Head_Top", createPart(new Head_Top()));
        parts.put("Head_Bottom", createPart(new Head_Bottom()));
        parts.put("Head_Right", createPart(new Head_Right()));
        parts.put("Head_Front", createPart(new Head_Front()));
        parts.put("Head_Left", createPart(new Head_Left()));
        parts.put("Head_Back", createPart(new Head_Back()));
        parts.put("Hat_Top", createPart(new Hat_Top()));
        parts.put("Hat_Bottom", createPart(new Hat_Bottom()));
        parts.put("Hat_Right", createPart(new Hat_Right()));
        parts.put("Hat_Front", createPart(new Hat_Front()));
        parts.put("Hat_Left", createPart(new Hat_Left()));
        parts.put("Hat_Back", createPart(new Hat_Back()));
        parts.put("Right_Leg_Top", createPart(new Right_Leg_Top()));
        parts.put("Right_Leg_Bottom", createPart(new Right_Leg_Bottom()));
        parts.put("Right_Leg_Right", createPart(new Right_Leg_Right()));
        parts.put("Right_Leg_Front", createPart(new Right_Leg_Front()));
        parts.put("Right_Leg_Left", createPart(new Right_Leg_Left()));
        parts.put("Right_Leg_Back", createPart(new Right_Leg_Back()));
        parts.put("Torso_Top", createPart(new Torso_Top()));
        parts.put("Torso_Bottom", createPart(new Torso_Bottom()));
        parts.put("Torso_Right", createPart(new Torso_Right()));
        parts.put("Torso_Front", createPart(new Torso_Front()));
        parts.put("Torso_Left", createPart(new Torso_Left()));
        parts.put("Torso_Back", createPart(new Torso_Back()));
        parts.put("Right_Arm_Top", createPart(new Right_Arm_Top()));
        parts.put("Right_Arm_Bottom", createPart(new Right_Arm_Bottom()));
        parts.put("Right_Arm_Right", createPart(new Right_Arm_Right()));
        parts.put("Right_Arm_Front", createPart(new Right_Arm_Front()));
        parts.put("Right_Arm_Left", createPart(new Right_Arm_Left()));
        parts.put("Right_Arm_Back", createPart(new Right_Arm_Back()));
        parts.put("Left_Leg_Top", createPart(new Left_Leg_Top()));
        parts.put("Left_Leg_Bottom", createPart(new Left_Leg_Bottom()));
        parts.put("Left_Leg_Right", createPart(new Left_Leg_Right()));
        parts.put("Left_Leg_Front", createPart(new Left_Leg_Front()));
        parts.put("Left_Leg_Left", createPart(new Left_Leg_Left()));
        parts.put("Left_Leg_Back", createPart(new Left_Leg_Back()));
        parts.put("Left_Arm_Top", createPart(new Left_Arm_Top()));
        parts.put("Left_Arm_Bottom", createPart(new Left_Arm_Bottom()));
        parts.put("Left_Arm_Right", createPart(new Left_Arm_Right()));
        parts.put("Left_Arm_Front", createPart(new Left_Arm_Front()));
        parts.put("Left_Arm_Left", createPart(new Left_Arm_Left()));
        parts.put("Left_Arm_Back", createPart(new Left_Arm_Back()));
        parts.put("Right_Pants_Leg_Top", createPart(new Right_Pants_Leg_Top()));
        parts.put("Right_Pants_Leg_Bottom", createPart(new Right_Pants_Leg_Bottom()));
        parts.put("Right_Pants_Leg_Right", createPart(new Right_Pants_Leg_Right()));
        parts.put("Right_Pants_Leg_Front", createPart(new Right_Pants_Leg_Front()));
        parts.put("Right_Pants_Leg_Left", createPart(new Right_Pants_Leg_Left()));
        parts.put("Right_Pants_Leg_Back", createPart(new Right_Pants_Leg_Back()));
        parts.put("Jacket_Top", createPart(new Jacket_Top()));
        parts.put("Jacket_Bottom", createPart(new Jacket_Bottom()));
        parts.put("Jacket_Right", createPart(new Jacket_Right()));
        parts.put("Jacket_Front", createPart(new Jacket_Front()));
        parts.put("Jacket_Left", createPart(new Jacket_Left()));
        parts.put("Jacket_Back", createPart(new Jacket_Back()));
        parts.put("Right_Sleeve_Top", createPart(new Right_Sleeve_Top()));
        parts.put("Right_Sleeve_Bottom", createPart(new Right_Sleeve_Bottom()));
        parts.put("Right_Sleeve_Right", createPart(new Right_Sleeve_Right()));
        parts.put("Right_Sleeve_Front", createPart(new Right_Sleeve_Front()));
        parts.put("Right_Sleeve_Left", createPart(new Right_Sleeve_Left()));
        parts.put("Right_Sleeve_Back", createPart(new Right_Sleeve_Back()));
        parts.put("Left_Pants_Leg_Top", createPart(new Left_Pants_Leg_Top()));
        parts.put("Left_Pants_Leg_Bottom", createPart(new Left_Pants_Leg_Bottom()));
        parts.put("Left_Pants_Leg_Right", createPart(new Left_Pants_Leg_Right()));
        parts.put("Left_Pants_Leg_Front", createPart(new Left_Pants_Leg_Front()));
        parts.put("Left_Pants_Leg_Left", createPart(new Left_Pants_Leg_Left()));
        parts.put("Left_Pants_Leg_Back", createPart(new Left_Pants_Leg_Back()));
        parts.put("Left_Sleeve_Top", createPart(new Left_Sleeve_Top()));
        parts.put("Left_Sleeve_Bottom", createPart(new Left_Sleeve_Bottom()));
        parts.put("Left_Sleeve_Right", createPart(new Left_Sleeve_Right()));
        parts.put("Left_Sleeve_Front", createPart(new Left_Sleeve_Front()));
        parts.put("Left_Sleeve_Left", createPart(new Left_Sleeve_Left()));
        parts.put("Left_Sleeve_Back", createPart(new Left_Sleeve_Back()));
    }

    /**
     * Creates a part image based on the specified section.
     *
     * @param section The section for which the part image is created.
     * @return A Part object representing the created part.
     */
    private @NotNull Part createPart(@NotNull Section section) {
        int x = section.getX1();
        int y = section.getY1();
        int width = section.getWidth();
        int height = section.getHeight();

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
     * Serializes the SkinParts object to a map for YAML serialization.
     *
     * @return A map containing the serialized data.
     */
    @Override
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
