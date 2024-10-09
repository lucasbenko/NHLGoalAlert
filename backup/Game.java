package com.lucasbenko.NHLGoalAlert;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Scanner;

public class Game {
    private String gameId;
    private Team homeTeam;
    private Team awayTeam;

    private String favouriteTeam;
    private ZonedDateTime startTime;
    private int homeScore;
    private int awayScore;

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

    public String getGameState(){
        return gameState;
    }

    public void refreshScore() {
        JSONParser jsonParser = new JSONParser();
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

            // Parse the JSON response
            JSONObject jsonObject = (JSONObject) jsonParser.parse(informationString.toString());
            JSONArray gamesArray = (JSONArray) jsonObject.get("games");

            for (Object gameObj : gamesArray) {
                JSONObject game = (JSONObject) gameObj;
                String gameID = String.valueOf(game.get("id"));

                if (gameID.equals(this.gameId)) {
                    int lastHomeScore = this.homeScore; // Store the last home score
                    int lastAwayScore = this.awayScore; // Store the last away score

                    Long homeScoreLong = (Long) ((JSONObject) game.get("homeTeam")).get("score");
                    if (homeScoreLong == null) {
                        // Skip this game if home score is null
                        continue;
                    } else {
                        this.homeScore = homeScoreLong.intValue(); // Update the current home score
                    }

                    Long awayScoreLong = (Long) ((JSONObject) game.get("awayTeam")).get("score");
                    if (awayScoreLong == null) {
                        // Skip this game if away score is null
                        continue;
                    } else {
                        this.awayScore = awayScoreLong.intValue(); // Update the current away score
                    }

                    // Check for a goal for the favorite team
                    if (homeTeam.getName().equals(this.favouriteTeam) && (this.homeScore > lastHomeScore)) {
                        System.out.println(homeTeam.getName() + " Goal! Current score: " + homeTeam.getName() + " " + this.homeScore + "-" + this.awayScore + " " + awayTeam.getName());
                        flashLights();
                    } else if (awayTeam.getName().equals(this.favouriteTeam) && (this.awayScore > lastAwayScore)) {
                        System.out.println(awayTeam.getName() + " Goal! Current score: " + homeTeam.getName() + " " + this.homeScore + "-" + this.awayScore + " " + awayTeam.getName());
                        flashLights();
                    }

                    gameState = (String) game.get("gameState");

                    break; // No need to continue checking other games
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void flashLights() {
        GoveeController gc = new GoveeController("827ffbf0-c075-4961-8721-84b88a3efa60");
        JSONObject response = gc.getConnectedDevices();
    }

}

