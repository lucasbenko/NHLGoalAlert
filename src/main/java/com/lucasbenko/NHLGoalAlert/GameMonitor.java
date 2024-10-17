package com.lucasbenko.NHLGoalAlert;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
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
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }

            StringBuilder informationString = new StringBuilder();

            try {
                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext()) {
                    informationString.append(scanner.nextLine());
                }
                scanner.close();

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

                            GoveeEffect.toggleDevice(Main.MAC_ADDRESS_LIGHT, Main.GOVEE_MODEL, 1);
                            GoveeEffect.toggleDevice(Main.MAC_ADDRESS_PLUG, Main.GOVEE_MODEL_PLUG, 1);
                            new Thread(() -> GoveeEffect.flashLights(homeTeam)).start();
                            new Thread(() -> GoveeEffect.PlayHorn(homeTeam)).start();
                            TimeUnit.SECONDS.sleep(Main.TIME_FLASHING);
                            GoveeEffect.toggleDevice(Main.MAC_ADDRESS_PLUG, Main.GOVEE_MODEL_PLUG, 0);
                            GoveeEffect.toggleDevice(Main.MAC_ADDRESS_LIGHT, Main.GOVEE_MODEL, 0);

                        } else if (awayTeam.getName().equals(game.getFavouriteTeam()) && (game.getAwayScore() > lastAwayScore)) {
                            TimeUnit.SECONDS.sleep(Main.GOAL_DELAY);
                            GUI.printToConsole(awayTeam.getName() + " Goal! " + awayTeam.getName() + " " + game.getAwayScore() + "-" + game.getHomeScore() + " " + homeTeam.getName());
                            System.out.println(awayTeam.getName() + " Goal! " + awayTeam.getName() + " " + game.getAwayScore() + "-" + game.getHomeScore() + " " + homeTeam.getName());

                            GoveeEffect.toggleDevice(Main.MAC_ADDRESS_LIGHT, Main.GOVEE_MODEL, 1);
                            new Thread(() -> GoveeEffect.toggleDevice(Main.MAC_ADDRESS_PLUG, Main.GOVEE_MODEL_PLUG, 1));
                            new Thread(() -> GoveeEffect.PlayHorn(awayTeam)).start();
                            new Thread(() -> GoveeEffect.flashLights(awayTeam)).start();
                            TimeUnit.SECONDS.sleep(Main.TIME_FLASHING);
                            new Thread(() -> GoveeEffect.toggleDevice(Main.MAC_ADDRESS_PLUG, Main.GOVEE_MODEL_PLUG, 0));
                            GoveeEffect.toggleDevice(Main.MAC_ADDRESS_LIGHT, Main.GOVEE_MODEL, 0);
                        }

                        game.setGameState(gameobj.get("gameState").getAsString());
                        break;
                    }
                }
            }catch (ConnectException e) {
                System.out.println("Connection timed out.");
            }

        } catch (Exception e) {
            e.printStackTrace();
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
        new Thread(() -> GoveeEffect.PlayHorn(t)).start();
        try {
            TimeUnit.SECONDS.sleep(Main.TIME_FLASHING);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        GoveeEffect.toggleDevice(Main.MAC_ADDRESS_PLUG, Main.GOVEE_MODEL_PLUG, 0);
        GoveeEffect.toggleDevice(Main.MAC_ADDRESS_LIGHT, Main.GOVEE_MODEL, 0);
    }
}
