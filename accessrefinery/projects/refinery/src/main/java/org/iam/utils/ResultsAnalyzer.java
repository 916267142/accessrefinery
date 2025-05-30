package org.iam.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * ResultsAnalyzer collects and computes statistics for batch analysis runs.
 * <p>
 * Tracks timing, round counts, and result sizes for mining and reducing steps.
 * Provides methods to reset statistics, add timing data, calculate averages,
 * and write summary results to files.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public class ResultsAnalyzer {
    // the start time;
    double startTime;
    // the number of solving round for single policy file;
    int fileRound;

    // Mining candidates intents (IMiner)
    Set<Double> MCILabelsTime;
    Set<Double> MCIOperationsTime;
    Integer MCISolvingRound;
    // Reducing Redundant Intents (IReducer)
    Set<Double> RRIOperationsTime;
    Set<Double> RRIILPSolvingTime;

    int MCISolvingRoundAverage;
    int numberStatement;
    int numberIntentsMCI;
    int numberIntentsRRI;
    double TotalTimeAverage;
    double MCILabelsTimeAverage;
    double MCIOperationsTimeAverage;
    double RRIOperationsTimeAverage;
    double RRIILPSolvingTimeAverage;

    /**
     * Constructs a new ResultsAnalyzer and initializes all statistics.
     */
    public ResultsAnalyzer() {
        this.fileRound = 0;
        this.MCISolvingRound = 0;
        this.MCILabelsTime = new TreeSet<Double>();
        this.MCIOperationsTime = new TreeSet<Double>();
        this.RRIOperationsTime = new TreeSet<Double>();
        this.RRIILPSolvingTime = new TreeSet<Double>();
    }

    /**
     * Resets all statistics for a new analysis run.
     */
    public void initializes() {
        this.fileRound = 0;
        this.MCISolvingRound = 0;
        this.MCILabelsTime.clear();
        this.MCIOperationsTime.clear();
        this.RRIOperationsTime.clear();
        this.RRIILPSolvingTime.clear();
    }

    /**
     * Increments the file round counter.
     */
    public void addRound() {
        this.fileRound++;
    }

    /**
     * Increments the solving round counter for IMiner.
     */
    public void addMCISolvingRound() {
        this.MCISolvingRound++;
    }

    /**
     * Starts a new timing measurement.
     */
    public void startMeasurement() {
        startTime = System.nanoTime();
    }

    /**
     * Adds elapsed time to IMiner label timing and restarts measurement.
     */
    public void addMCILabelsTime() {
        double elapsedTime = (System.nanoTime() - startTime) / 1_000_000.0;
        this.MCILabelsTime.add(elapsedTime);
        startMeasurement();
    }

    /**
     * Adds elapsed time to IMiner operation timing and restarts measurement.
     */
    public void addMCIOperationsTime() {
        double elapsedTime = (System.nanoTime() - startTime) / 1_000_000.0;
        this.MCIOperationsTime.add(elapsedTime);
        startMeasurement();
    }

    /**
     * Adds elapsed time to IReducer operation timing and restarts measurement.
     */
    public void addRRIOperationsTime() {
        double elapsedTime = (System.nanoTime() - startTime) / 1_000_000.0;
        this.RRIOperationsTime.add(elapsedTime);
        startMeasurement();
    }

    /**
     * Adds elapsed time to IReducer ILP solving timing and restarts measurement.
     */
    public void addRRIILPSolvingTime() {
        double elapsedTime = (System.nanoTime() - startTime) / 1_000_000.0;
        this.RRIILPSolvingTime.add(elapsedTime);
        startMeasurement();
    }

    /**
     * Sets the number of statements in the analyzed file.
     */
    public void setNumberStatement(int numberStatement) {
        this.numberStatement = numberStatement;
    }

    /**
     * Sets the number of IMiner intents.
     */
    public void setNumberIntentsMCI(int numberIntentsMCI) {
        this.numberIntentsMCI = numberIntentsMCI;
    }

    /**
     * Sets the number of IReducer intents.
     */
    public void setNumberIntentsRRI(int numberIntentsRRI) {
        this.numberIntentsRRI = numberIntentsRRI;
    }

    /**
     * Sets the average number of solving rounds for IMiner.
     */
    public void setMCISolvingRoundAverage(int round) {
        this.MCISolvingRoundAverage = round;
    }

    /**
     * Calculates averages for all tracked statistics.
     */
    public void calculateAverage() {
        if(this.fileRound <= 2) {
            this.MCILabelsTimeAverage = calculateAverage(this.MCILabelsTime);
            this.MCIOperationsTimeAverage = calculateAverage(this.MCIOperationsTime);
            this.RRIOperationsTimeAverage = calculateAverage(this.RRIOperationsTime);
            this.RRIILPSolvingTimeAverage = calculateAverage(this.RRIILPSolvingTime);
        } else {
            this.MCILabelsTimeAverage = calculateAverageWithoutExtremes(this.MCILabelsTime);
            this.MCIOperationsTimeAverage = calculateAverageWithoutExtremes(this.MCIOperationsTime);
            this.RRIOperationsTimeAverage = calculateAverageWithoutExtremes(this.RRIOperationsTime);
            this.RRIILPSolvingTimeAverage = calculateAverageWithoutExtremes(this.RRIILPSolvingTime);
        }

        this.TotalTimeAverage = (this.MCILabelsTimeAverage + this.MCIOperationsTimeAverage + this.RRIOperationsTimeAverage + this.RRIILPSolvingTimeAverage);
    }

    // private double calculateMedian(Set<Double> times) {
    //     if (times == null || times.isEmpty()) {
    //         return 0.0;
    //     }
    //     // Convert the set to a sorted list
    //     List<Double> sortedTimes = new ArrayList<>(times);
    //     Collections.sort(sortedTimes);
    //     int size = sortedTimes.size();
    //     if (size % 2 == 0) {
    //         // If even, return the average of the two middle elements
    //         return (sortedTimes.get(size / 2 - 1) + sortedTimes.get(size / 2)) / 2.0;
    //     } else {
    //         // If odd, return the middle element
    //         return sortedTimes.get(size / 2);
    //     }
    // }


    /**
     * Calculates the average of a set of times, excluding the minimum and maximum.
     *
     * @param times the set of times
     * @return the average without extremes, or 0 if not enough data
     */
    private double calculateAverageWithoutExtremes(Set<Double> times) {
    if (times == null || times.size() <= 2 || times.isEmpty()) {
        // If there are not enough elements to remove max and min, return 0 or handle appropriately
        return 0.0;
    }

    // Convert the set to a sorted list
    List<Double> sortedTimes = new ArrayList<>(times);

    // Remove the minimum and maximum values
    sortedTimes.remove(Collections.min(sortedTimes));
    sortedTimes.remove(Collections.max(sortedTimes));

    // Calculate the average of the remaining values
    double sum = 0.0;
    for (double time : sortedTimes) {
        sum += time;
    }

    return sum / sortedTimes.size();
    }

    /**
     * Calculates the average of a set of times.
     *
     * @param times the set of times
     * @return the average, or 0 if empty
     */
    private double calculateAverage(Set<Double> times) {
        if (times == null || times.isEmpty()) {
            return 0.0;
        }
    
        // Convert the set to a sorted list
        List<Double> sortedTimes = new ArrayList<>(times);
    
        // Calculate the average of the remaining values
        double sum = 0.0;
        for (double time : sortedTimes) {
            sum += time;
        }
    
        return sum / sortedTimes.size();
    }

    /**
     * Writes the header line to the summary output file.
     *
     * @param filePath the output file path
     */
    public void writeHeaderToFile(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            writer.println("NumberStatement\tNumberMCI\tNumberRRI\tMCISolvingRoundAverage\tTotalTimeAverage\tMCILabelsTimeAverage\tMCIOperationsTimeAverage\tRRIOperationsTimeAverage\tRRIILPSolvingTimeAverage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Appends the current averages to the summary output file.
     *
     * @param filePath the output file path
     */
    public void writeAveragesToFile(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.printf("%d\t%d\t%d\t%d\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f%n",
                    numberStatement,
                    numberIntentsMCI,
                    numberIntentsRRI,
                    MCISolvingRoundAverage,
                    TotalTimeAverage,
                    MCILabelsTimeAverage,
                    MCIOperationsTimeAverage,
                    RRIOperationsTimeAverage,
                    RRIILPSolvingTimeAverage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
