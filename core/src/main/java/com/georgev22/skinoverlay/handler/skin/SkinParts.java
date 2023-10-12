package com.georgev22.skinoverlay.handler.skin;

import com.georgev22.skinoverlay.handler.skin.Section.*;
import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
public class SkinParts implements Serializable {
    private volatile SerializableBufferedImage fullSkin;

    private String skinName;

    private final Map<String, Part> parts;

    public SkinParts() {
        this(null, null);
    }

    public SkinParts(SerializableBufferedImage fullSkin) {
        this(fullSkin, null);
    }

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

    public void createParts() {
        if (fullSkin == null) {
            return;
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

    private @NotNull Part createPart(@NotNull Section section) {
        int x1 = section.getX1();
        int y1 = section.getY1();
        int x2 = section.getX2();
        int y2 = section.getY2();

        if (y1 > y2) {
            int temp = y1;
            y1 = y2;
            y2 = temp;
        }

        int width = x2 - x1;
        int height = y2 - y1;

        BufferedImage partImage = fullSkin.getBufferedImage().getSubimage(x1, y1, width, height);
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
        return new Part(partName, new SerializableBufferedImage(partImage), x1, y1, width, height, isAreaTransparent);
    }

    public Part getPart(String partName) {
        return parts.get(partName);
    }

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

    @Override
    public String toString() {
        return "SkinParts{" +
                "fullSkin=" + fullSkin +
                ", parts=" + parts +
                '}';
    }
}
