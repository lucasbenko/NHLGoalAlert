package com.lucasbenko.NHLGoalAlert;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GameService {
    private static String FAVOURITE_TEAM;

    public GameService(String team){
        this.FAVOURITE_TEAM = team;
    }

    public Game getGameInfo() {
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

            JSONObject jsonObject = (JSONObject) jsonParser.parse(informationString.toString());
            JSONArray gamesArray = (JSONArray) jsonObject.get("games");

            for (Object gameObj : gamesArray) {
                JSONObject game = (JSONObject) gameObj;
                String gameDate = (String) game.get("gameDate");

                if (gameDate.equals(formattedCurrentDate)) {
                    String gameID = String.valueOf(game.get("id"));
                    Team homeTeam = new Team(((JSONObject) ((JSONObject) game.get("homeTeam")).get("name")).get("default").toString());
                    Team awayTeam = new Team(((JSONObject) ((JSONObject) game.get("awayTeam")).get("name")).get("default").toString());

                    if (homeTeam.getName().equals(FAVOURITE_TEAM) || awayTeam.getName().equals(FAVOURITE_TEAM)) {
                        ZonedDateTime startTime = ZonedDateTime.parse((String) game.get("startTimeUTC"), DateTimeFormatter.ISO_DATE_TIME);
                        ZonedDateTime currentTime = ZonedDateTime.now(ZoneOffset.UTC);
                        long minutesDifference = ChronoUnit.MINUTES.between(currentTime, startTime);

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

