package com.lucasbenko.NHLGoalAlert;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GameMonitor {

    public static void start(Team homeTeam, Team awayTeam, String gameId) {
        System.out.println("Monitoring game: " + homeTeam.getName() + " vs " + awayTeam.getName());
        GUI.printToConsole("Monitoring game: " + homeTeam.getName() + " vs " + awayTeam.getName());
    }

    public static void waitForGameStart(long minutesDifference, Game currentGame) throws InterruptedException {
        boolean gameTime = false;
        Game game = currentGame;

        synchronized (game) {
            while (!gameTime) {

                ZonedDateTime startTime = game.getStartTime();
                ZonedDateTime now = ZonedDateTime.now();
                Duration duration = Duration.between(now, startTime);
                long sleepTimeMillis = duration.toMillis();

                if (sleepTimeMillis > 0 ) {
                    System.out.println("It's game day!");
                    GUI.printToConsole("It's game day!");
                    System.out.println("Waiting " + duration.toHours() + " hours " + duration.toMinutes() % 60 + " minutes for game start.");
                    GUI.printToConsole("Waiting " + duration.toHours() + " hours " + duration.toMinutes() % 60 + " minutes for game start.");
                }else{
                    System.out.println("It's game time!");
                    GUI.printToConsole("It's game time!");
                }

                if (sleepTimeMillis <= 0) {
                    break;
                }

                try {
                    game.wait(sleepTimeMillis);
                    gameTime = true;
                } catch (InterruptedException e) {
                    System.out.println("Waiting process was interrupted!");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public static void refreshScore(Game game) {
        Team homeTeam = game.getHomeTeam();
        Team awayTeam = game.getAwayTeam();
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
                return;
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
                JsonObject gameobj = gameElem.getAsJsonObject();
                String gameID = gameobj.get("id").getAsString();

                if (gameID.equals(game.getGameId())) {
                    int lastHomeScore = game.getHomeScore();
                    int lastAwayScore = game.getAwayScore();

                    JsonObject homeTeamJson = gameobj.getAsJsonObject("homeTeam");
                    JsonObject awayTeamJson = gameobj.getAsJsonObject("awayTeam");

                    if (homeTeamJson.has("score")) {
                        game.setHomeScore(homeTeamJson.get("score").getAsInt());
                    }

                    if (awayTeamJson.has("score")) {
                        game.setAwayScore(awayTeamJson.get("score").getAsInt());
                    }

                    if (homeTeam.getName().equals(game.getFavouriteTeam()) && (game.getHomeScore() > lastHomeScore)) {
                        TimeUnit.SECONDS.sleep(Main.GOAL_DELAY);
                        GUI.printToConsole(homeTeam.getName() + " Goal! " + homeTeam.getName() + " " + game.getHomeScore() + "-" + game.getAwayScore() + " " + awayTeam.getName());
                        System.out.println(homeTeam.getName() + " Goal! " + homeTeam.getName() + " " + game.getHomeScore() + "-" + game.getAwayScore() + " " + awayTeam.getName());

                        GoveeEffect.toggleDevice(Main.MAC_ADDRESS_PLUG, Main.GOVEE_MODEL_PLUG, 1);
                        new Thread(() -> GoveeEffect.flashLights(homeTeam)).start();
                        new Thread(() -> GoveeEffect.PlayHorn(homeTeam, 0)).start(); // 1 = away team 0 = home team
                        TimeUnit.SECONDS.sleep(Main.TIME_FLASHING);
                        GoveeEffect.toggleDevice(Main.MAC_ADDRESS_PLUG, Main.GOVEE_MODEL_PLUG, 0);
                        GoveeEffect.toggleDevice(Main.MAC_ADDRESS_LIGHT, Main.GOVEE_MODEL, 0);
                    } else if (awayTeam.getName().equals(game.getFavouriteTeam()) && (game.getAwayScore() > lastAwayScore)) {
                        TimeUnit.SECONDS.sleep(Main.GOAL_DELAY);
                        GUI.printToConsole(awayTeam.getName() + " Goal! " + awayTeam.getName() + " " + game.getAwayScore() + "-" + game.getHomeScore() + " " + homeTeam.getName());
                        System.out.println(awayTeam.getName() + " Goal! " + awayTeam.getName() + " " + game.getAwayScore() + "-" + game.getHomeScore() + " " + homeTeam.getName());

                        GoveeEffect.toggleDevice(Main.MAC_ADDRESS_PLUG, Main.GOVEE_MODEL_PLUG, 1);
                        new Thread(() -> GoveeEffect.flashLights(awayTeam)).start();
                        new Thread(() -> GoveeEffect.PlayHorn(awayTeam, 1)).start(); // 1 = away team 0 = home team
                        TimeUnit.SECONDS.sleep(Main.TIME_FLASHING);
                        GoveeEffect.toggleDevice(Main.MAC_ADDRESS_PLUG, Main.GOVEE_MODEL_PLUG, 0);
                        GoveeEffect.toggleDevice(Main.MAC_ADDRESS_LIGHT, Main.GOVEE_MODEL, 0);
                    }

                    game.setGameState(gameobj.get("gameState").getAsString()); // Get game state

                    if (homeTeam.getName().equals(game.getFavouriteTeam()) && (game.getHomeScore() > game.getAwayScore()) && game.getGameState().equalsIgnoreCase("FINAL")) {
                        TimeUnit.SECONDS.sleep(Main.GOAL_DELAY);
                        GUI.printToConsole(homeTeam.getName() + " Win!");
                        System.out.println(homeTeam.getName() + " Win!");

                        GoveeEffect.toggleDevice(Main.MAC_ADDRESS_PLUG, Main.GOVEE_MODEL_PLUG, 1);
                        new Thread(() -> GoveeEffect.flashLights(homeTeam)).start();
                        new Thread(() -> GoveeEffect.PlayHorn(homeTeam, 0)).start();
                        TimeUnit.SECONDS.sleep(Main.TIME_FLASHING);
                        GoveeEffect.toggleDevice(Main.MAC_ADDRESS_PLUG, Main.GOVEE_MODEL_PLUG, 0);
                        GoveeEffect.toggleDevice(Main.MAC_ADDRESS_LIGHT, Main.GOVEE_MODEL, 0);
                    } else if (awayTeam.getName().equals(game.getFavouriteTeam()) && (game.getAwayScore() > game.getHomeScore()) && game.getGameState().equalsIgnoreCase("FINAL")) {
                        TimeUnit.SECONDS.sleep(Main.GOAL_DELAY);
                        GUI.printToConsole(awayTeam.getName() + " Win!");
                        System.out.println(awayTeam.getName() + " Win!");

                        GoveeEffect.toggleDevice(Main.MAC_ADDRESS_PLUG, Main.GOVEE_MODEL_PLUG, 1);
                        new Thread(() -> GoveeEffect.flashLights(awayTeam)).start();
                        new Thread(() -> GoveeEffect.PlayHorn(awayTeam, 1)).start();
                        TimeUnit.SECONDS.sleep(Main.TIME_FLASHING);
                        GoveeEffect.toggleDevice(Main.MAC_ADDRESS_PLUG, Main.GOVEE_MODEL_PLUG, 0);
                        GoveeEffect.toggleDevice(Main.MAC_ADDRESS_LIGHT, Main.GOVEE_MODEL, 0);
                    }

                    break;
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {

        }
    }


    public static void testAlert(Team t){
        t.setEffectID(GoveeEffect.getTeamEffect(t.getName()));
        Random random = new Random();
        int randomNumberHome = random.nextInt(4) + 1;
        int randomNumberAway = random.nextInt(4) + 1;
        GUI.printToConsole(t.getName() + " Goal! " + t.getName() + " " + randomNumberHome + "-" + randomNumberAway + " " + TeamNames.getRandomTeam().getTeamName());

        GoveeEffect.toggleDevice(Main.MAC_ADDRESS_LIGHT, Main.GOVEE_MODEL, 1);
        GoveeEffect.toggleDevice(Main.MAC_ADDRESS_PLUG, Main.GOVEE_MODEL_PLUG, 1);
        new Thread(() -> GoveeEffect.flashLights(t)).start();
        new Thread(() -> GoveeEffect.PlayHorn(t, 1)).start();
        try {
            TimeUnit.SECONDS.sleep(Main.TIME_FLASHING);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        GoveeEffect.toggleDevice(Main.MAC_ADDRESS_PLUG, Main.GOVEE_MODEL_PLUG, 0);
        GoveeEffect.toggleDevice(Main.MAC_ADDRESS_LIGHT, Main.GOVEE_MODEL, 0);
    }
}
