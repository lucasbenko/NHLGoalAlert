package com.lucasbenko.NHLGoalAlert;

import java.util.concurrent.TimeUnit;

public class GameMonitor {

    public static void start(Team homeTeam, Team awayTeam, String gameId) {
        System.out.println("Monitoring game: " + gameId);
        // Implement any monitoring logic here
    }

    public static void waitForGameStart(long minutesDifference) throws InterruptedException {
        boolean oneHourAway = false;

        while (!oneHourAway) {
            if (minutesDifference > 0 && minutesDifference < 60) {
                System.out.println("Start time is less than an hour away from the current time.");
                oneHourAway = true;
            } else {
                System.out.println("Start time is not less than an hour away from the current time. Waiting one hour to check again.");
                //TimeUnit.MINUTES.sleep(60);

                //FOR TESTING
                oneHourAway = true;
            }
        }
    }
}
