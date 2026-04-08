package org.aws.utils;

import org.aws.config.Parameter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtil {
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

    public static Path replaceThirdLastLevel(Path inputFoldPath) {
        int nameCount = inputFoldPath.getNameCount();

        if (nameCount < 3) {
            return inputFoldPath;
        }

        Path root = inputFoldPath.getRoot();
        Path newPath = root != null ? root : Paths.get("");

        for (int i = 0; i < nameCount - 3; i++) {
            newPath = newPath.resolve(inputFoldPath.getName(i));
        }

        newPath = newPath.resolve("result");

        newPath = newPath.resolve(inputFoldPath.getName(nameCount - 2));
        newPath = newPath.resolve(inputFoldPath.getName(nameCount - 1));

        return newPath;
    }

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
     * Change the file extension to .csv and append "_time" to the base name.
     *
     * @param fileName The original file name.
     * @return The processed file name with .csv extension and "_time" appended.
     */
    public static String changeToCsvWithTime(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        String baseName = dotIndex != -1 ? fileName.substring(0, dotIndex) : fileName;
        baseName += switch (Parameter.getActiveSolver()) {
            case Z3 -> "_z3";
            case CVC5 -> "_cvc5";
        };
        return baseName + "_time.csv";
    }

    /**
     * Change the file extension to .json and append "_findings" to the base name.
     *
     * @param fileName The original file name.
     * @return The processed file name with .json extension and "_findings" appended.
     */
    public static String changeToJsonWithFindings(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        String baseName = dotIndex != -1 ? fileName.substring(0, dotIndex) : fileName;
        baseName += switch (Parameter.getActiveSolver()) {
            case Z3 -> "_z3";
            case CVC5 -> "_cvc5";
        };
        return baseName + "_findings.json";
    }

    /**
     * Change the file extension to .dot.
     *
     * @param fileName The original file name.
     * @return The processed file name with .dot extension.
     */
    public static String changeToDot(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        String baseName = dotIndex != -1 ? fileName.substring(0, dotIndex) : fileName;
        return baseName + ".dot";
    }
}
