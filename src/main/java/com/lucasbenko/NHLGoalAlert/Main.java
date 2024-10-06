package com.lucasbenko.NHLGoalAlert;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    public static int GOAL_DELAY; // Change the delay for effects after API receives goal alert

    public static int FLASH_INTERVAL;
    public static int TIME_FLASHING;
    public static String GOVEE_API_KEY;
    public static String MAC_ADDRESS;
    public static String GOVEE_MODEL;
    private static TeamNames FAVOURITE_TEAM; // Initialize variable for favourite team
    public static void main(String[] args) {
        boolean valid = false;
        while(!valid){
            System.out.println("Please choose your favourite team:");
            Scanner input = new Scanner(System.in);
            int i = 0;
            TeamNames[] choices = new TeamNames[33];
            for (TeamNames name : TeamNames.values()){
                i++;
                choices[i] = name;
                System.out.println(i + ".\t" + name.getTeamName().replace("_", " "));
            }

            int choice;

            try {
                choice = Integer.parseInt(input.nextLine());
                if (choice > 32 || choice < 1){
                    throw new Exception();
                }
                valid = true;
                FAVOURITE_TEAM = choices[choice];
                System.out.println("Your Team: " + FAVOURITE_TEAM.getTeamName());
            } catch (Exception e) {
;               System.out.println("Please enter a valid number.");
            }

        }
        initialize();
    }

    public static void initialize(){
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
        GOVEE_MODEL = prop.getProperty("app.GOVEE_MODEL");
        MAC_ADDRESS = prop.getProperty("app.MAC_ADDRESS");


        GameService gameService = new GameService(FAVOURITE_TEAM.getTeamName());
        Game game = gameService.getGameInfo();

        if ((game != null) && !game.getGameState().equals("FINAL")) {
            long minutesDifference = ChronoUnit.MINUTES.between(ZonedDateTime.now(), game.getStartTime());
            try {
                GameMonitor.waitForGameStart(minutesDifference, game);
                GameMonitor.start(game.getHomeTeam(), game.getAwayTeam(), game.getGameId());

                while (true) {
                    game.refreshScore();
                    TimeUnit.SECONDS.sleep(1); // Adjust the interval as needed
                    if (game.getGameState().equals("FINAL")){
                        System.out.println("Tonight's game has concluded.");
                        waitUntilMidnight();
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No game found for today: " + LocalDate.now());
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
                System.out.println("Waiting until midnight local time to check again " + secondsUntilMidnight);
                if (secondsUntilMidnight + 10 < -10) {
                    initialize();
                }
                lock.wait((secondsUntilMidnight + 10) * 1000);
                initialize();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
