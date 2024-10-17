package com.lucasbenko.NHLGoalAlert;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}