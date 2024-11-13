package com.lucasbenko.NHLGoalAlert;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class GameService {
    private static String FAVOURITE_TEAM;

    public GameService(String team) {
        this.FAVOURITE_TEAM = team;
    }
    public Game getGameInfo() {
        LocalDate currentDate = LocalDate.now();
        String formattedCurrentDate = currentDate.toString();

        try {
            URL url = new URL("https://api-web.nhle.com/v1/score/" + formattedCurrentDate);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

            int responseCode = conn.getResponseCode();
            if (responseCode == 403) {
                System.out.println("403 Forbidden - Access is denied.");
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    System.out.println("Error details: " + errorResponse.toString());
                } catch (IOException e) {
                    System.out.println("Error reading response body: " + e.getMessage());
                }
                return null;
            } else if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }

            StringBuilder informationString = new StringBuilder();
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                while (scanner.hasNext()) {
                    informationString.append(scanner.nextLine());
                }
            }

            JsonObject jsonObject = JsonParser.parseString(informationString.toString()).getAsJsonObject();
            JsonArray gamesArray = jsonObject.getAsJsonArray("games");

            for (JsonElement gameElem : gamesArray) {
                JsonObject game = gameElem.getAsJsonObject();
                String gameDate = game.get("gameDate").getAsString();

                if (gameDate.equals(formattedCurrentDate)) {
                    String gameID = game.get("id").getAsString();
                    JsonObject homeTeamObj = game.getAsJsonObject("homeTeam").getAsJsonObject("name");
                    JsonObject awayTeamObj = game.getAsJsonObject("awayTeam").getAsJsonObject("name");

                    Team homeTeam = new Team(homeTeamObj.get("default").getAsString());
                    Team awayTeam = new Team(awayTeamObj.get("default").getAsString());

                    if (homeTeam.getName().equals(FAVOURITE_TEAM) || awayTeam.getName().equals(FAVOURITE_TEAM)) {
                        ZonedDateTime startTime = ZonedDateTime.parse(game.get("startTimeUTC").getAsString(), DateTimeFormatter.ISO_DATE_TIME);
                        ZonedDateTime currentTime = ZonedDateTime.now(ZoneOffset.UTC);
                        long minutesDifference = ChronoUnit.MINUTES.between(currentTime, startTime);

                        long homeEffectID = GoveeEffect.getTeamEffect(homeTeam.getName());
                        long awayEffectID = GoveeEffect.getTeamEffect(awayTeam.getName());

                        homeTeam.setEffectID(homeEffectID);
                        awayTeam.setEffectID(awayEffectID);

                        return new Game(gameID, homeTeam, awayTeam, FAVOURITE_TEAM, startTime);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}