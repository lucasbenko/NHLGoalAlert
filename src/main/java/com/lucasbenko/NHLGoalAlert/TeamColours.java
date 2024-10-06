package com.lucasbenko.NHLGoalAlert;

public enum TeamColours {
    DUCKS(252, 76, 2, 185, 151, 91),           // Primary: Orange, Secondary: Black
    BRUINS(252, 181, 20, 0, 0, 0),        // Primary: Gold, Secondary: Black
    SABRES(0, 48, 135, 255, 184, 28),     // Primary: Navy Blue, Secondary: White
    FLAMES(210, 0, 28, 250, 175, 25),    // Primary: Red, Secondary: Yellow
    HURRICANES(206, 17, 38, 255, 255, 255), // Primary: Red, Secondary: White
    BLACKHAWKS(207, 10, 44, 255, 103, 27),     // Primary: Red, Secondary: Black
    AVALANCHE(111, 38, 61, 35, 97, 146),    // Primary: Burgundy, Secondary: Blue
    BLUE_JACKETS(0, 38, 84, 206, 17, 38), // Primary: Union Blue, Secondary: Red
    STARS(0, 104, 71, 143, 143, 140),     // Primary: Victory Green, Secondary: Silver
    RED_WINGS(206, 17, 38, 255, 255, 255), // Primary: Red, Secondary: White
    OILERS(4, 30, 66, 252, 76, 0),      // Primary: Blue, Secondary: Orange
    PANTHERS(4, 30, 66, 200, 16, 46),     // Primary: Navy Blue, Secondary: Red
    KINGS(17, 17, 17, 162, 170, 173),        // Primary: Black, Secondary: Silver
    WILD(2, 73, 48, 175, 35, 36),        // Primary: Forest Green, Secondary: Red
    CANADIENS(175, 30, 45, 25, 33, 104), // Primary: Red, Secondary: Blue
    PREDATORS(255, 184, 28, 4, 30, 66),   // Primary: Gold, Secondary: Navy Blue
    DEVILS(206, 17, 38, 0, 0, 0),         // Primary: Red, Secondary: Black
    ISLANDERS(0, 83, 155, 245, 125, 48),  // Primary: Royal Blue, Secondary: Orange
    RANGERS(0, 56, 168, 206, 17, 38),   // Primary: Blue, Secondary: Red
    SENATORS(218, 26, 50, 183, 146, 87),   // Primary: Red, Secondary: Gold
    FLYERS(247, 73, 2, 0, 0, 0),          // Primary: Orange, Secondary: Black
    PENGUINS(252, 181, 20, 207, 196, 147),      // Primary: Yellow, Secondary: Pastel Gold
    SHARKS(0, 109, 117, 234, 114, 0),         // Primary: Teal, Secondary: Orange
    KRAKEN(0, 22, 40, 153, 217, 217),     // Primary: Deep Sea Blue, Secondary: Ice Blue
    BLUES(0, 47, 135, 252, 181, 20),       // Primary: Blue, Secondary: Gold
    LIGHTNING(0, 40, 104, 255, 255, 255), // Primary: Blue, Secondary: White
    MAPLE_LEAFS(0, 32, 91, 255, 255, 255), // Primary: Blue, Secondary: White
    UTAH_HOCKEY_CLUB(113, 175, 229, 255, 255, 255),     // Primary: Salt Blue, Secondary: White
    CANUCKS(0, 32, 91, 10, 134, 61),      // Primary: Blue, Secondary: Green
    GOLDEN_KNIGHTS(185, 151, 91, 51, 63, 72), // Primary: Steel Grey, Secondary: Steel Grey
    CAPITALS(200, 16, 46, 4, 30, 66),     // Primary: Red, Secondary: Blue
    JETS(4, 30, 66, 0, 76, 151);       // Primary: Navy, Secondary: Blue

    private final int primaryRed;
    private final int primaryGreen;
    private final int primaryBlue;
    private final int secondaryRed;
    private final int secondaryGreen;
    private final int secondaryBlue;

    // Constructor
    TeamColours(int primaryRed, int primaryGreen, int primaryBlue,
                int secondaryRed, int secondaryGreen, int secondaryBlue) {
        this.primaryRed = primaryRed;
        this.primaryGreen = primaryGreen;
        this.primaryBlue = primaryBlue;
        this.secondaryRed = secondaryRed;
        this.secondaryGreen = secondaryGreen;
        this.secondaryBlue = secondaryBlue;
    }

    // Get primary RGB values as a string
    public RGB getPrimaryRGB() {
        RGB primaryRGB = new RGB(primaryRed, primaryGreen, primaryBlue);
        return primaryRGB;
    }

    // Get secondary RGB values as a string
    public RGB getSecondaryRGB() {
        RGB secondaryRGB = new RGB(secondaryRed, secondaryGreen, secondaryBlue);
        return secondaryRGB;
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