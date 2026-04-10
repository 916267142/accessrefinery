package org.iam.utils;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for file and directory operations.
 * <p>
 * Provides methods for listing files, creating directories, and generating output file names.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public class FileUtil {
    /**
     * Returns a sorted list of file names in the given directory.
     *
     * @param directoryPath the directory path
     * @return a sorted list of file names
     */
    public static List<String> getFileNames(Path directoryPath) {
        List<String> fileNames = new ArrayList<>();
        File directory = directoryPath.toFile();
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileNames.add(file.getName());
                    }
                }
            }
        }
        Collections.sort(fileNames);
        return fileNames;
    }

    /**
     * Creates the directory if it does not exist.
     *
     * @param directoryPath the directory path
     * @return true if the directory exists or was created successfully, false otherwise
     */
    public static boolean createDirectoryIfNotExists(Path directoryPath) {
        File directory = directoryPath.toFile();
        try {
            if (!directory.exists()) {
                return directory.mkdirs();
            }
            return true;
        } catch (SecurityException e) {
            Parameter.LOGGER.severe("File create failed");
            return false;
        }
    }

    /**
     * Replaces the second last level of the input path with "results".
     *
     * @param inputFoldPath the input path
     * @return the new path with the second last level replaced
     */
    public static Path replaceSecondLastLevel(Path inputFoldPath) {
        int nameCount = inputFoldPath.getNameCount();

        if (nameCount < 2) {
            return inputFoldPath;
        }

        Path root = inputFoldPath.getRoot();
        Path newPath = root != null ? root : Paths.get("");

        for (int i = 0; i < nameCount - 2; i++) {
            newPath = newPath.resolve(inputFoldPath.getName(i));
        }

        newPath = newPath.resolve("result");

        newPath = newPath.resolve(inputFoldPath.getName(nameCount - 1));

        return newPath;
    }

    /**
     * Changes the file extension to .csv and appends "_time" to the base name.
     *
     * @param fileName the original file name
     * @return the processed file name with .csv extension and "_time" appended
     */
    public static String changeToCsvWithTime(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        String baseName = dotIndex != -1 ? fileName.substring(0, dotIndex) : fileName;
        return baseName + "_time.csv";
    }

    /**
     * Changes the file extension to .json and appends "_result" to the base name.
     *
     * @param fileName the original file name
     * @return the processed file name with .json extension and "_result" appended
     */
    public static String changeToJsonWithFindings(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        String baseName = dotIndex != -1 ? fileName.substring(0, dotIndex) : fileName;
        return baseName + "_result.json";
    }
    
    /**
     * Changes the file extension to .dot.
     *
     * @param fileName the original file name
     * @return the processed file name with .dot extension
     */
    public static String changeToDot(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        String baseName = dotIndex != -1 ? fileName.substring(0, dotIndex) : fileName;
        return baseName + ".dot";
    }
}
