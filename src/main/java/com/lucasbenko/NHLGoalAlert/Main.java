package com.lucasbenko.NHLGoalAlert;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Main {
    public static int GOAL_DELAY; // Change the delay for effects after API receives goal alert
    public static int FLASH_INTERVAL;
    public static int TIME_FLASHING;
    public static String GOVEE_API_KEY;
    public static String MAC_ADDRESS_LIGHT;
    public static String MAC_ADDRESS_PLUG;
    public static String MAC_ADDRESS_LIGHT_2;
    public static String GOVEE_MODEL_2;
    public static String GOVEE_MODEL;
    public static String GOVEE_MODEL_PLUG;
    private static TeamNames FAVOURITE_TEAM;

    public static String message;

    public static void initialize(TeamNames selectedTeam){
        readProps();

        FAVOURITE_TEAM = selectedTeam;

        GUI.printToConsole("Initializing for team: " + FAVOURITE_TEAM.getTeamName());

        GameService gameService = new GameService(FAVOURITE_TEAM.getTeamName());
        Game game = gameService.getGameInfo();

        if ((game != null) && !game.getGameState().equals("FINAL")) {
            long minutesDifference = ChronoUnit.MINUTES.between(ZonedDateTime.now(), game.getStartTime());
            try {
                GameMonitor.waitForGameStart(minutesDifference, game);
                GameMonitor.start(game.getHomeTeam(), game.getAwayTeam(), game.getGameId());

                while (true) {
                    GameMonitor.refreshScore(game);
                    TimeUnit.SECONDS.sleep(1);
                    if (game.getGameState().equals("FINAL")){
                        System.out.println("Tonight's game has concluded");
                        GUI.printToConsole("Tonight's game has concluded");
                        waitUntilMidnight();
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No game found for today: " + LocalDate.now());
            GUI.printToConsole("No game found for today: " + LocalDate.now());
            waitUntilMidnight();
        }
    }
    public static void waitUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long secondsUntilMidnight = ChronoUnit.SECONDS.between(now, midnight);

        Object lock = new Object();

        synchronized (lock) {
            try {
                System.out.println("Waiting until midnight local time to check again");
                GUI.printToConsole("Waiting until midnight local time to check again");
                if (secondsUntilMidnight + 10 < -10) {
                    initialize(FAVOURITE_TEAM);
                }
                lock.wait((secondsUntilMidnight + 10) * 1000);
                initialize(FAVOURITE_TEAM);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void readProps(){
        Properties prop = new Properties();
        String configName = "app.config";
        try (FileInputStream fis = new FileInputStream(configName)) {
            prop.load(fis);
        } catch (IOException ex) {
            System.out.println("app.config not found.");
        }

        GOAL_DELAY = Integer.parseInt(prop.getProperty("app.GOAL_DELAY"));
        GOVEE_API_KEY = prop.getProperty("app.GOVEE_API_KEY");
        TIME_FLASHING = Integer.parseInt(prop.getProperty("app.TIME_FLASHING"));
        FLASH_INTERVAL = Integer.parseInt(prop.getProperty("app.FLASH_INTERVAL"));

        //Light 1
        GOVEE_MODEL = prop.getProperty("app.GOVEE_MODEL_LIGHT");
        MAC_ADDRESS_LIGHT = prop.getProperty("app.MAC_ADDRESS_LIGHT");

        //Light 2
        GOVEE_MODEL_2 = prop.getProperty("app.GOVEE_MODEL_LIGHT_2");
        MAC_ADDRESS_LIGHT_2 = prop.getProperty("app.MAC_ADDRESS_LIGHT_2");

        // Smart Plug
        MAC_ADDRESS_PLUG = prop.getProperty("app.MAC_ADDRESS_PLUG");
        GOVEE_MODEL_PLUG = prop.getProperty("app.GOVEE_MODEL_PLUG");
    }
}
