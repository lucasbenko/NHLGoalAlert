package com.lucasbenko.NHLGoalAlert;

public class RGB {
    private static int red;
    private static int green;
    private static int blue;

    public RGB(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public static int getRed() {
        return red;
    }

    public static int getGreen() {
        return green;
    }

    public static int getBlue() {
        return blue;
    }

}
