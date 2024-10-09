package com.lucasbenko.NHLGoalAlert;

public class Team {
    private String name;
    private String teamPrimaryRGB;
    private String teamSecondaryRGB;

    private int primaryRed;
    private int primaryGreen;
    private int primaryBlue;

    private int secondaryRed;
    private int secondaryGreen;
    private int secondaryBlue;

    private long effectID;

    public Team(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEffectID(long effect){
        effectID = Effect.getTeamEffect(name);
    }

    public long getEffectID(){
        return effectID;
    }

    public void setPrimaryRGB(RGB rgb){
        primaryRed = RGB.getRed();
        primaryBlue = RGB.getBlue();
        primaryGreen = RGB.getGreen();
    }

    public void setSecondaryRGB(RGB rgb){
        secondaryRed = RGB.getRed();
        secondaryBlue = RGB.getBlue();
        secondaryGreen = RGB.getGreen();
    }

    public String getSecondaryRGB(){
        return teamSecondaryRGB;
    }

    // Getters for Primary RGB
    public int getPrimaryRed() {
        return primaryRed;
    }

    public int getPrimaryGreen() {
        return primaryGreen;
    }

    public int getPrimaryBlue() {
        return primaryBlue;
    }

    // Getters for Secondary RGB
    public int getSecondaryRed() {
        return secondaryRed;
    }

    public int getSecondaryGreen() {
        return secondaryGreen;
    }

    public int getSecondaryBlue() {
        return secondaryBlue;
    }


}