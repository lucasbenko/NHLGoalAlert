package com.lucasbenko.NHLGoalAlert;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Game {
    private String gameId;
    private Team homeTeam;
    private Team awayTeam;
    private String favouriteTeam;
    private ZonedDateTime startTime;
    private int homeScore;
    private int awayScore;
    private long effectID;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private String gameState;

    public Game(String gameId, Team homeTeam, Team awayTeam, String favouriteTeam, ZonedDateTime startTime) {
        this.gameId = gameId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.favouriteTeam = favouriteTeam;
        this.startTime = startTime;
        this.homeScore = 0;
        this.awayScore = 0;
        this.gameState = "";
        setTeamRGB();
    }

    public String getGameId() {
        return gameId;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public String getGameState() {
        return gameState;
    }

    public String getFavouriteTeam(){
        return favouriteTeam;
    }

    public int getHomeScore(){
        return homeScore;
    }

    public int getAwayScore(){
        return awayScore;
    }

    public void setGameState(String s){
        gameState = s;
    }

    public void setHomeScore(int homeScore){
        this.homeScore = homeScore;
    }

    public void setAwayScore(int awayScore){
        this.awayScore = awayScore;
    }

    public void setTeamRGB() {
        TeamColours teamColour = TeamColours.valueOf(awayTeam.getName().toUpperCase().replace(" ", "_"));
        awayTeam.setPrimaryRGB(teamColour.getPrimaryRGB());
        awayTeam.setSecondaryRGB(teamColour.getSecondaryRGB());

        teamColour = TeamColours.valueOf(homeTeam.getName().toUpperCase().replace(" ", "_"));
        homeTeam.setPrimaryRGB(teamColour.getPrimaryRGB());
        homeTeam.setSecondaryRGB(teamColour.getSecondaryRGB());
    }
}