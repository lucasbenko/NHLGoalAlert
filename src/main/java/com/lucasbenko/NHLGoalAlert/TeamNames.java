package com.lucasbenko.NHLGoalAlert;

public enum TeamNames {
    DUCKS("Ducks"),
    BRUINS("Bruins"),
    SABRES("Sabres"),
    FLAMES("Flames"),
    HURRICANES("Hurricanes"),
    BLACKHAWKS("Blackhawks"),
    AVALANCHE("Avalanche"),
    BLUE_JACKETS("Blue Jackets"),
    STARS("Stars"),
    RED_WINGS("Red Wings"),
    OILERS("Oilers"),
    PANTHERS("Panthers"),
    KINGS("Kings"),
    WILD("Wild"),
    CANADIENS("Canadiens"),
    PREDATORS("Predators"),
    DEVILS("Devils"),
    ISLANDERS("Islanders"),
    RANGERS("Rangers"),
    SENATORS("Senators"),
    FLYERS("Flyers"),
    PENGUINS("Penguins"),
    SHARKS("Sharks"),
    KRAKEN("Kraken"),
    BLUES("Blues"),
    LIGHTNING("Lightning"),
    MAPLE_LEAFS("Maple Leafs"),
    UTAH("Utah Hockey Club"),
    CANUCKS("Canucks"),
    GOLDEN_KNIGHTS("Golden Knights"),
    CAPITALS("Capitals"),
    JETS("Jets");

    private final String teamName;

    // Constructor
    TeamNames(String teamName) {
        this.teamName = teamName;
    }

    // Getter for teamName
    public String getTeamName() {
        return teamName;
    }
}

