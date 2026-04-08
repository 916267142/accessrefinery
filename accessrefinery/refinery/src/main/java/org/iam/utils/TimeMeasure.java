package org.iam.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.TreeMap;
import java.util.Map;

/**
 * Utility class for measuring and recording multiple time intervals.
 * It measures time and stores the results in a LinkedHashMap.
 * The class can write operation names and elapsed times to a file,
 * differentiating between single-round total time and all-rounds total time.
 */
public class TimeMeasure {
    /**
     * A LinkedHashMap to store operation names and their corresponding elapsed times.
     */
    private final Map<String, Double> timeRecords;
    /**
     * The total elapsed time of all measurement rounds.
     */
    private double totalTime;
    /**
     * The total elapsed time of the current measurement round.
     */
    private double singleRoundTotalTime;
    /**
     * The start time of the current measurement.
     */
    private double startTime;
    /**
     * DecimalFormat instance to format time values to two decimal places.
     */
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    /**
     * A string representing an 8-space tab for formatting output.
     */
    private static final String TAB = "        ";

    /**
     * Constructs a new TimeMeasure object.
     * Initializes the timeRecords map, totalTime, singleRoundTotalTime, and startTime.
     */
    public TimeMeasure() {
        this.timeRecords = new TreeMap<>();
        this.totalTime = 0;
        this.singleRoundTotalTime = 0;
        this.startTime = 0;
    }

    /**
     * Starts measuring time for a specific operation.
     * Records the current system time as the start time.
     */
    public void startMeasurement() {
        startTime = System.nanoTime();
    }

    /**
     * Stops measuring time for a specific operation and records the elapsed time.
     *
     * @param operationName The name or meaning of the operation.
     */
    public void stopMeasurement(String operationName) {
        double endTime = System.nanoTime();
        double elapsedTime = (endTime - startTime) / 1_000_000.0;

        elapsedTime = Double.parseDouble(DECIMAL_FORMAT.format(elapsedTime));
        timeRecords.put(operationName, elapsedTime);
        singleRoundTotalTime += elapsedTime;
        totalTime += elapsedTime;
        startMeasurement();
    }

    /**
     * Gets the map of time records.
     *
     * @return A LinkedHashMap containing operation names and their corresponding elapsed times in milliseconds.
     */
    public Map<String, Double> getTimeRecords() {
        return timeRecords;
    }

    /**
     * Gets the total elapsed time of all operations across all rounds.
     *
     * @return The total elapsed time in milliseconds, formatted to two decimal places.
     */
    public double getTotalTime() {
        return Double.parseDouble(DECIMAL_FORMAT.format(totalTime));
    }

    /**
     * Gets the total elapsed time of the current round of operations.
     *
     * @return The single-round total elapsed time in milliseconds, formatted to two decimal places.
     */
    public double getSingleRoundTotalTime() {
        return Double.parseDouble(DECIMAL_FORMAT.format(singleRoundTotalTime));
    }

    /**
     * Writes the keys (operation names) of the TimeMeasure's time records to a file,
     * overwriting the existing content.
     *
     * @param filePath    The path of the file to write to.
     * @param timeMeasure The TimeMeasure instance containing the time records.
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    public static void writeKeysToFile(String filePath, TimeMeasure timeMeasure) throws IOException {
        Map<String, Double> records = timeMeasure.getTimeRecords();
        int[] maxWidths = calculateMaxWidths(records, timeMeasure.getTotalTime(), timeMeasure.getSingleRoundTotalTime());

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            StringBuilder keysLine = new StringBuilder();
            int index = 0;
            for (String key : records.keySet()) {
                if (index > 0) {
                    keysLine.append(TAB);
                }
                keysLine.append(padString(key, maxWidths[index]));
                index++;
            }
            keysLine.append(TAB).append(padString("SingleRound", maxWidths[index]));
            keysLine.append(TAB).append(padString("AllRounds", maxWidths[index + 1]));
            writer.println(keysLine);
        }
    }

    /**
     * Appends the values (elapsed times) of the TimeMeasure's time records to a file.
     * Also appends the single-round total time and all-rounds total time.
     *
     * @param filePath    The path of the file to append to.
     * @param timeMeasure The TimeMeasure instance containing the time records.
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    public static void appendValuesToFile(String filePath, TimeMeasure timeMeasure) throws IOException {
        Map<String, Double> records = timeMeasure.getTimeRecords();
        int[] maxWidths = calculateMaxWidths(records, timeMeasure.getTotalTime(), timeMeasure.getSingleRoundTotalTime());

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            StringBuilder valuesLine = new StringBuilder();
            int index = 0;
            for (Map.Entry<String, Double> entry : records.entrySet()) {
                if (index > 0) {
                    valuesLine.append(TAB);
                }
                String valueStr = DECIMAL_FORMAT.format(entry.getValue());
                valuesLine.append(padString(valueStr, maxWidths[index]));
                index++;
            }
            String singleRoundTotalTimeStr = DECIMAL_FORMAT.format(timeMeasure.getSingleRoundTotalTime());
            valuesLine.append(TAB).append(padString(singleRoundTotalTimeStr, maxWidths[index]));
            String totalTimeStr = DECIMAL_FORMAT.format(timeMeasure.getTotalTime());
            valuesLine.append(TAB).append(padString(totalTimeStr, maxWidths[index + 1]));
            writer.println(valuesLine);
            // Reset the single-round total time and time records for the next round
            timeMeasure.singleRoundTotalTime = 0;
            timeMeasure.timeRecords.replaceAll((k, v) -> 0.0);
        }
    }

    /**
     * Calculates the maximum width for each column based on keys, values, single-round total time, and all-rounds total time.
     *
     * @param records              The time records map.
     * @param totalTime            The total elapsed time of all rounds.
     * @param singleRoundTotalTime The total elapsed time of the current round.
     * @return An array of maximum widths for each column.
     */
    private static int[] calculateMaxWidths(Map<String, Double> records, double totalTime, double singleRoundTotalTime) {
        int[] maxWidths = new int[records.size() + 2];
        int index = 0;
        for (Map.Entry<String, Double> entry : records.entrySet()) {
            String key = entry.getKey();
            String valueStr = DECIMAL_FORMAT.format(entry.getValue());
            maxWidths[index] = Math.max(key.length(), valueStr.length());
            index++;
        }
        maxWidths[index] = Math.max("SingleRound".length(), DECIMAL_FORMAT.format(singleRoundTotalTime).length());
        maxWidths[index + 1] = Math.max("AllRounds".length(), DECIMAL_FORMAT.format(totalTime).length());
        return maxWidths;
    }

    /**
     * Pads a string to a specified width with spaces.
     *
     * @param str    The string to pad.
     * @param width  The desired width.
     * @return The padded string.
     */
    private static String padString(String str, int width) {
        StringBuilder padded = new StringBuilder(str);
        while (padded.length() < width) {
            padded.append(" ");
        }
        return padded.toString();
    }
}