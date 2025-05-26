package org.iam.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility class for writing strings to files.
 * <p>
 * Provides a static method to write content to a specified file path.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public class Printer {
    /**
     * Writes the given string content to the specified file path.
     *
     * @param content  the string content to write
     * @param filePath the file path to write to
     */
    public static void writeStringToFile(String content, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
