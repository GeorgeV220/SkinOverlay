package com.georgev22.skinoverlay.handler.skin;

import lombok.Getter;

@Getter
public abstract class Section {
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    private final int width;
    private final int height;

    public Section(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        if (y1 > y2) {
            int temp = y1;
            y1 = y2;
            y2 = temp;
        }

        this.width = x2 - x1;
        this.height = y2 - y1;
    }

    @Override
    public String toString() {
        return "Section{" +
                "type=" + getClass().getSimpleName() +
                ", x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                '}';
    }

    public static class Head_Top extends Section {
        public Head_Top() {
            super(8, 0, 16, 8);
        }
    }

    public static class Head_Bottom extends Section {
        public Head_Bottom() {
            super(16, 0, 24, 8);
        }
    }

    public static class Head_Right extends Section {
        public Head_Right() {
            super(0, 8, 8, 16);
        }
    }

    public static class Head_Front extends Section {
        public Head_Front() {
            super(8, 8, 16, 16);
        }
    }

    public static class Head_Left extends Section {
        public Head_Left() {
            super(16, 8, 24, 16);
        }
    }

    public static class Head_Back extends Section {
        public Head_Back() {
            super(24, 8, 32, 16);
        }
    }

    public static class Hat_Top extends Section {
        public Hat_Top() {
            super(40, 0, 48, 8);
        }
    }

    public static class Hat_Bottom extends Section {
        public Hat_Bottom() {
            super(48, 0, 56, 8);
        }
    }

    public static class Hat_Right extends Section {
        public Hat_Right() {
            super(32, 8, 40, 16);
        }
    }

    public static class Hat_Front extends Section {
        public Hat_Front() {
            super(40, 8, 48, 16);
        }
    }

    public static class Hat_Left extends Section {
        public Hat_Left() {
            super(48, 8, 56, 16);
        }
    }

    public static class Hat_Back extends Section {
        public Hat_Back() {
            super(56, 8, 64, 16);
        }
    }

    public static class Right_Leg_Top extends Section {
        public Right_Leg_Top() {
            super(4, 16, 8, 20);
        }
    }

    public static class Right_Leg_Bottom extends Section {
        public Right_Leg_Bottom() {
            super(8, 16, 12, 20);
        }
    }

    public static class Right_Leg_Right extends Section {
        public Right_Leg_Right() {
            super(0, 20, 4, 32);
        }
    }

    public static class Right_Leg_Front extends Section {
        public Right_Leg_Front() {
            super(4, 20, 8, 32);
        }
    }

    public static class Right_Leg_Left extends Section {
        public Right_Leg_Left() {
            super(8, 20, 12, 32);
        }
    }

    public static class Right_Leg_Back extends Section {
        public Right_Leg_Back() {
            super(12, 20, 16, 32);
        }
    }

    public static class Torso_Top extends Section {
        public Torso_Top() {
            super(20, 16, 28, 20);
        }
    }

    public static class Torso_Bottom extends Section {
        public Torso_Bottom() {
            super(28, 16, 36, 20);
        }
    }

    public static class Torso_Right extends Section {
        public Torso_Right() {
            super(16, 20, 20, 32);
        }
    }

    public static class Torso_Front extends Section {
        public Torso_Front() {
            super(20, 20, 28, 32);
        }
    }

    public static class Torso_Left extends Section {
        public Torso_Left() {
            super(36, 20, 40, 32);
        }
    }

    public static class Torso_Back extends Section {
        public Torso_Back() {
            super(28, 20, 36, 32);
        }
    }

    public static class Right_Arm_Top extends Section {
        public Right_Arm_Top() {
            super(44, 16, 48, 20);
        }
    }

    public static class Right_Arm_Bottom extends Section {
        public Right_Arm_Bottom() {
            super(48, 16, 52, 20);
        }
    }

    public static class Right_Arm_Right extends Section {
        public Right_Arm_Right() {
            super(40, 20, 44, 32);
        }
    }

    public static class Right_Arm_Front extends Section {
        public Right_Arm_Front() {
            super(44, 20, 48, 32);
        }
    }

    public static class Right_Arm_Left extends Section {
        public Right_Arm_Left() {
            super(48, 20, 52, 32);
        }
    }

    public static class Right_Arm_Back extends Section {
        public Right_Arm_Back() {
            super(52, 20, 56, 32);
        }
    }

    public static class Left_Leg_Top extends Section {
        public Left_Leg_Top() {
            super(20, 48, 24, 52);
        }
    }

    public static class Left_Leg_Bottom extends Section {
        public Left_Leg_Bottom() {
            super(24, 48, 28, 52);
        }
    }

    public static class Left_Leg_Right extends Section {
        public Left_Leg_Right() {
            super(16, 52, 20, 64);
        }
    }

    public static class Left_Leg_Front extends Section {
        public Left_Leg_Front() {
            super(20, 52, 24, 64);
        }
    }

    public static class Left_Leg_Left extends Section {
        public Left_Leg_Left() {
            super(24, 52, 28, 64);
        }
    }

    public static class Left_Leg_Back extends Section {
        public Left_Leg_Back() {
            super(28, 52, 32, 64);
        }
    }

    public static class Left_Arm_Top extends Section {
        public Left_Arm_Top() {
            super(36, 48, 40, 52);
        }
    }

    public static class Left_Arm_Bottom extends Section {
        public Left_Arm_Bottom() {
            super(40, 48, 44, 52);
        }
    }

    public static class Left_Arm_Right extends Section {
        public Left_Arm_Right() {
            super(32, 52, 36, 64);
        }
    }

    public static class Left_Arm_Front extends Section {
        public Left_Arm_Front() {
            super(36, 52, 40, 64);
        }
    }

    public static class Left_Arm_Left extends Section {
        public Left_Arm_Left() {
            super(40, 52, 44, 64);
        }
    }

    public static class Left_Arm_Back extends Section {
        public Left_Arm_Back() {
            super(44, 52, 48, 64);
        }
    }

    public static class Right_Pants_Leg_Top extends Section {
        public Right_Pants_Leg_Top() {
            super(4, 32, 8, 36);
        }
    }

    public static class Right_Pants_Leg_Bottom extends Section {
        public Right_Pants_Leg_Bottom() {
            super(8, 32, 12, 36);
        }
    }

    public static class Right_Pants_Leg_Right extends Section {
        public Right_Pants_Leg_Right() {
            super(0, 36, 4, 48);
        }
    }

    public static class Right_Pants_Leg_Front extends Section {
        public Right_Pants_Leg_Front() {
            super(4, 36, 8, 48);
        }
    }

    public static class Right_Pants_Leg_Left extends Section {
        public Right_Pants_Leg_Left() {
            super(8, 36, 12, 48);
        }
    }

    public static class Right_Pants_Leg_Back extends Section {
        public Right_Pants_Leg_Back() {
            super(12, 36, 16, 48);
        }
    }

    public static class Jacket_Top extends Section {
        public Jacket_Top() {
            super(20, 32, 28, 36);
        }
    }

    public static class Jacket_Bottom extends Section {
        public Jacket_Bottom() {
            super(28, 32, 36, 36);
        }
    }

    public static class Jacket_Right extends Section {
        public Jacket_Right() {
            super(16, 36, 20, 48);
        }
    }

    public static class Jacket_Front extends Section {
        public Jacket_Front() {
            super(20, 36, 28, 48);
        }
    }

    public static class Jacket_Left extends Section {
        public Jacket_Left() {
            super(36, 36, 40, 48);
        }
    }

    public static class Jacket_Back extends Section {
        public Jacket_Back() {
            super(28, 36, 36, 48);
        }
    }

    public static class Right_Sleeve_Top extends Section {
        public Right_Sleeve_Top() {
            super(44, 32, 48, 36);
        }
    }

    public static class Right_Sleeve_Bottom extends Section {
        public Right_Sleeve_Bottom() {
            super(48, 32, 52, 36);
        }
    }

    public static class Right_Sleeve_Right extends Section {
        public Right_Sleeve_Right() {
            super(40, 36, 44, 48);
        }
    }

    public static class Right_Sleeve_Front extends Section {
        public Right_Sleeve_Front() {
            super(44, 36, 48, 48);
        }
    }

    public static class Right_Sleeve_Left extends Section {
        public Right_Sleeve_Left() {
            super(48, 36, 52, 48);
        }
    }

    public static class Right_Sleeve_Back extends Section {
        public Right_Sleeve_Back() {
            super(52, 36, 56, 48);
        }
    }

    public static class Left_Pants_Leg_Top extends Section {
        public Left_Pants_Leg_Top() {
            super(4, 48, 8, 52);
        }
    }

    public static class Left_Pants_Leg_Bottom extends Section {
        public Left_Pants_Leg_Bottom() {
            super(8, 48, 12, 52);
        }
    }

    public static class Left_Pants_Leg_Right extends Section {
        public Left_Pants_Leg_Right() {
            super(0, 52, 4, 64);
        }
    }

    public static class Left_Pants_Leg_Front extends Section {
        public Left_Pants_Leg_Front() {
            super(4, 52, 8, 64);
        }
    }

    public static class Left_Pants_Leg_Left extends Section {
        public Left_Pants_Leg_Left() {
            super(8, 52, 12, 64);
        }
    }

    public static class Left_Pants_Leg_Back extends Section {
        public Left_Pants_Leg_Back() {
            super(12, 52, 16, 64);
        }
    }

    public static class Left_Sleeve_Top extends Section {
        public Left_Sleeve_Top() {
            super(52, 48, 56, 52);
        }
    }

    public static class Left_Sleeve_Bottom extends Section {
        public Left_Sleeve_Bottom() {
            super(56, 48, 60, 52);
        }
    }

    public static class Left_Sleeve_Right extends Section {
        public Left_Sleeve_Right() {
            super(48, 52, 52, 64);
        }
    }

    public static class Left_Sleeve_Front extends Section {
        public Left_Sleeve_Front() {
            super(52, 52, 56, 64);
        }
    }

    public static class Left_Sleeve_Left extends Section {
        public Left_Sleeve_Left() {
            super(56, 52, 60, 64);
        }
    }

    public static class Left_Sleeve_Back extends Section {
        public Left_Sleeve_Back() {
            super(60, 52, 64, 64);
        }
    }

}
