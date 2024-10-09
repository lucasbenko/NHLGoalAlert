package com.lucasbenko.NHLGoalAlert;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    private static String FAVOURITE_TEAM = "Golden Knights";
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        //System.out.println("Enter your favourite team name (e.g Canucks): ");
        //FAVOURITE_TEAM = input.nextLine();

        initialize();
    }

    public static void initialize(){
        GameService gameService = new GameService(FAVOURITE_TEAM);
        Game game = gameService.getGameInfo();

        if ((game != null) && !game.getGameState().equals("FINAL")) {
            long minutesDifference = ChronoUnit.MINUTES.between(ZonedDateTime.now(), game.getStartTime());
            try {
                GameMonitor.waitForGameStart(minutesDifference);
                GameMonitor.start(game.getHomeTeam(), game.getAwayTeam(), game.getGameId());

                while (true) {
                    game.refreshScore();
                    TimeUnit.SECONDS.sleep(3); // Adjust the interval as needed
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
    public static void waitUntilMidnight(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        long secondsUntilMidnight = ChronoUnit.SECONDS.between(now, midnight);

        try {
            System.out.println(secondsUntilMidnight);
            Thread.sleep((secondsUntilMidnight + 10) * 1000);
            initialize();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
