package com.lucasbenko.NHLGoalAlert;

import java.time.Duration;
import java.time.ZonedDateTime;

public class GameMonitor {

    public static void start(Team homeTeam, Team awayTeam, String gameId) {
        System.out.println("Monitoring game: " + homeTeam.getName() + " vs " + awayTeam.getName());
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
                    System.out.println("Waiting " + duration.toHours() + " hours " + duration.toMinutes() % 60 + " minutes for game start.");
                }else{
                    System.out.println("It's game time!");
                }

                if (sleepTimeMillis <= 0) {
                    break;
                }

                game.wait(sleepTimeMillis);
                gameTime = true;
            }
        }
    }
}
