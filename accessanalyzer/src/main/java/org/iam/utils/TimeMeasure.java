package org.iam.utils;

import org.iam.config.Parameter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TimeMeasure {
    /**
     * The class records the time of every round.
     */
    private long wholeTime = 0;

    private static class RoundTime {
        long singleRound;
        long allRound;

        RoundTime(long singleRound, long allRound) {
            this.singleRound = singleRound;
            this.allRound = allRound;
        }
    }

    private final List<RoundTime> rounds = new ArrayList<>();
    private long totalTime = 0;

    public void setWholeTime(long wholeTime) {
        this.wholeTime = wholeTime;
    }

    public void addRound(long singleRound) {
        totalTime += singleRound;
        rounds.add(new RoundTime(singleRound, totalTime));
    }

    public void writeToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            switch (Parameter.getActiveLogic()) {
                case STRATIFIED -> {
                    writer.println("SingleRound,AllRound,WholeTime");
                    for (RoundTime round : rounds) {
                        String line = String.format("%.4f,%.4f,%.4f", round.singleRound/1e9, round.allRound/1e9, wholeTime/1e9);
                        writer.println(line);
                    }
                }
                case ENUMERATED -> {
                    writer.println("WholeTime");
                    writer.println(String.format("%.4f", wholeTime/1e9));
                }
            }
        }
    }
}
