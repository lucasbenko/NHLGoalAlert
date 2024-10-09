package com.lucasbenko.NHLGoalAlert;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    public void setTeamRGB() {
        TeamColours teamColour = TeamColours.valueOf(awayTeam.getName().toUpperCase().replace(" ", "_"));
        awayTeam.setPrimaryRGB(teamColour.getPrimaryRGB());
        awayTeam.setSecondaryRGB(teamColour.getSecondaryRGB());

        teamColour = TeamColours.valueOf(homeTeam.getName().toUpperCase().replace(" ", "_"));
        homeTeam.setPrimaryRGB(teamColour.getPrimaryRGB());
        homeTeam.setSecondaryRGB(teamColour.getSecondaryRGB());
    }

    public void refreshScore() {
        LocalDate currentDate = LocalDate.now();
        String formattedCurrentDate = currentDate.toString();

        try {
            URL url = new URL("https://api-web.nhle.com/v1/score/" + formattedCurrentDate);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }

            StringBuilder informationString = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNext()) {
                informationString.append(scanner.nextLine());
            }
            scanner.close();

            JsonObject jsonObject = JsonParser.parseString(informationString.toString()).getAsJsonObject();
            JsonArray gamesArray = jsonObject.getAsJsonArray("games");

            for (JsonElement gameElem : gamesArray) {
                JsonObject game = gameElem.getAsJsonObject();
                String gameID = game.get("id").getAsString();

                if (gameID.equals(this.gameId)) {
                    int lastHomeScore = this.homeScore;
                    int lastAwayScore = this.awayScore;

                    // Update home and away scores
                    JsonObject homeTeamJson = game.getAsJsonObject("homeTeam");
                    JsonObject awayTeamJson = game.getAsJsonObject("awayTeam");

                    if (homeTeamJson.has("score")) {
                        this.homeScore = homeTeamJson.get("score").getAsInt();
                    }

                    if (awayTeamJson.has("score")) {
                        this.awayScore = awayTeamJson.get("score").getAsInt();
                    }

                    if (homeTeam.getName().equals(this.favouriteTeam) && (this.homeScore > lastHomeScore)) {
                        TimeUnit.SECONDS.sleep(Main.GOAL_DELAY);
                        System.out.println(homeTeam.getName() + " Goal! Current score: " + homeTeam.getName() + " " + this.homeScore + "-" + this.awayScore + " " + awayTeam.getName());

                        Effect.toggleDevice(Main.MAC_ADDRESS_LIGHT, Main.GOVEE_MODEL, 1);
                        Effect.toggleDevice(Main.MAC_ADDRESS_PLUG, Main.GOVEE_MODEL_PLUG, 1);
                        new Thread(() -> Effect.flashLights(homeTeam)).start();
                        new Thread(() -> Effect.PlayHorn(homeTeam)).start();
                        TimeUnit.SECONDS.sleep(Main.TIME_FLASHING);
                        Effect.toggleDevice(Main.MAC_ADDRESS_PLUG, Main.GOVEE_MODEL_PLUG, 0);
                        Effect.toggleDevice(Main.MAC_ADDRESS_LIGHT, Main.GOVEE_MODEL, 0);

                    } else if (awayTeam.getName().equals(this.favouriteTeam) && (this.awayScore > lastAwayScore)) {
                        TimeUnit.SECONDS.sleep(Main.GOAL_DELAY);
                        System.out.println(awayTeam.getName() + " Goal! Current score: " + homeTeam.getName() + " " + this.homeScore + "-" + this.awayScore + " " + awayTeam.getName());

                        Effect.toggleDevice(Main.MAC_ADDRESS_LIGHT, Main.GOVEE_MODEL, 1);
                        new Thread(() -> Effect.toggleDevice(Main.MAC_ADDRESS_PLUG, Main.GOVEE_MODEL_PLUG, 1));
                        new Thread(() -> Effect.PlayHorn(awayTeam)).start();
                        new Thread(() -> Effect.flashLights(awayTeam)).start();
                        TimeUnit.SECONDS.sleep(Main.TIME_FLASHING);
                        new Thread(() -> Effect.toggleDevice(Main.MAC_ADDRESS_PLUG, Main.GOVEE_MODEL_PLUG, 0));
                        Effect.toggleDevice(Main.MAC_ADDRESS_LIGHT, Main.GOVEE_MODEL, 0);
                    }

                    // Update game state
                    this.gameState = game.get("gameState").getAsString();
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}