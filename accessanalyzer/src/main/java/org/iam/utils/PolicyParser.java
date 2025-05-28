package org.iam.utils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.iam.grammer.Policy;

/**
 * The PolicyParser class provides utility methods for parsing policy files and input streams
 * into Policy objects, converting Policy objects to JSON strings, and retrieving test files as input streams.
 * It uses the Jackson library for JSON processing.
 *
 * @since 2025-02-28
 */
public class PolicyParser {
    private static final Logger LOGGER = Logger.getLogger(PolicyParser.class.getName());

    /**
     * Parses a JSON file into a Policy object.
     *
     * @param filePath The path of the JSON file to be parsed.
     * @return A Policy object representing the parsed policy, or null if the file does not exist or an error occurs.
     */
    public static Policy parseFile(Path filePath) {
        File targetFile = filePath.toFile();
        if (!targetFile.exists()) {
            LOGGER.severe("File not found: " + filePath);
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        Policy policy = null;
        try {
            policy = mapper.readValue(targetFile, Policy.class);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error parsing file: " + filePath, e);
        }
        return policy;
    }

    /**
     * Parses an input stream containing JSON data into a Policy object.
     *
     * @param inputStream The input stream containing JSON data representing the policy.
     * @return A Policy object representing the parsed policy, or null if an error occurs.
     */
    public static Policy parseInput(InputStream inputStream) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        Policy policy = null;
        try {
            policy = mapper.readValue(inputStream, Policy.class);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error parsing input stream", e);
        }
        return policy;
    }

    /**
     * Converts a Policy object to a pretty - printed JSON string.
     *
     * @param policy The Policy object to be converted to a JSON string.
     * @return A pretty - printed JSON string representing the Policy object, or an empty string if an error occurs.
     */
    public static String toString(Policy policy) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(policy);
            return jsonString;
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Error converting policy to JSON string", e);
        }
        return jsonString;
    }

    /**
     * Retrieves a test file as an input stream.
     *
     * @param fileName The name of the test file to retrieve.
     * @return An input stream representing the test file.
     * @throws NullPointerException if the resource is not found.
     */
    public static InputStream getTestFile(String fileName) {
        ClassLoader classLoader = PolicyParser.class.getClassLoader();
        return Objects.requireNonNull(classLoader.getResourceAsStream(fileName),
                "Resource not found");
    }
}