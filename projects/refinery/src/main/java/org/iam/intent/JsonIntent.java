package org.iam.intent;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.google.common.collect.ImmutableSet;

import java.nio.file.Path;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.iam.utils.Parameter;

import java.util.Date;

/**
 * Converts a set of MCPIntent objects into a JSON structure for output.
 * <p>
 * Ignores keys with null or empty values during serialization.
 * Provides methods for formatting, processing, and writing findings to a JSON file.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
@SuppressWarnings("unchecked")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JsonIntent {
    @JsonProperty("InputFilePath")
    private final String inputFilePath;

    @JsonProperty("GeneratedTime")
    private final String generatedTime;

    @JsonProperty("Finding")
    private final List<JsonIntent.JsonFinding> findings;

    /**
     * Constructs a JsonIntent object.
     *
     * @param inputFilePath the path of the input file
     * @param findingsSet   a set of MCPIntent findings
     */
    public JsonIntent(String inputFilePath, HashSet<MCPIntent> findingsSet) {
        this.inputFilePath = inputFilePath;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.generatedTime = sdf.format(new Date());
        this.findings = new ArrayList<>();
        for (MCPIntent finding : findingsSet) {
            this.findings.add(processFinding(finding));
        }
    }

    /**
     * Processes a single MCPIntent finding into a JsonFinding.
     *
     * @param finding the MCPIntent finding
     * @return the processed JsonFinding
     */
    private JsonFinding processFinding(MCPIntent finding) {
        Map<String, List<String>> principalMap = new HashMap<>();
        Map<String, List<String>> conditionMap = new HashMap<>();
        List<String> actions = null;
        List<String> resources = null;

        if (finding.getDomainValues() != null) {
            for (Map.Entry<String, Object> entry : finding.getDomainValues().entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                List<String> valueAsList = getValueAsList(value);

                List<String> processedList = new ArrayList<>();
                for (String str : valueAsList) {
                    str = replaceDots(str);
                    processedList.add(str);
                }

                if (key.startsWith("Principal.")) {
                    String subKey = key.split("\\.", 2)[1];
                    principalMap.put(subKey, processedList);
                } else if (!key.contains("Action") && !key.contains("Resource")) {
                    conditionMap.put(key, processedList);
                }
            }

            actions = getValueAsList(finding.getDomainValues().get("Action"));
            resources = getValueAsList(finding.getDomainValues().get("Resource"));

            if (actions != null) {
                List<String> processedActions = new ArrayList<>();
                for (String action : actions) {
                    action = replaceDots(action);
                    processedActions.add(action);
                }
                actions = processedActions;
            }
            if (resources != null) {
                List<String> processedResources = new ArrayList<>();
                for (String resource : resources) {
                    resource = replaceDots(resource);
                    processedResources.add(resource);
                }
                resources = processedResources;
            }
        }

        return new JsonIntent.JsonFinding(principalMap, actions, resources, conditionMap);
    }

    /**
     * Replaces dots in a string with appropriate characters for output formatting.
     *
     * @param str the input string
     * @return the processed string
     */
    private String replaceDots(String str) {
        if (str == null) {
            return null;
        }

        // Check if the string matches an IP prefix with subnet mask regex
        if (str.matches("^\\d{1,3}(\\.\\d{1,3}){0,3}(\\/\\d{1,2})?$")) {
            return str;
        }

        str = str.replaceAll("(?<!\\\\)\\.(?!\\*)", "?");
        str = str.replaceAll("(?<!\\\\)\\.\\*", "*");
        str = str.replace("\\.", ".");
        return str;
    }

    /**
     * Converts an object to a List of strings.
     *
     * @param value the object to convert
     * @return a List of strings or null
     */
    private List<String> getValueAsList(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof List) {
            return (List<String>) value;
        }
        if (value instanceof ImmutableSet) {
            ImmutableSet<?> immutableSet = (ImmutableSet<?>) value;
            List<String> result = new ArrayList<>();
            for (Object item : immutableSet) {
                result.add(item.toString());
            }
            return result;
        }
        if (value instanceof String valueStr) {
            return Collections.singletonList(valueStr);
        }
        return Collections.singletonList(value.toString());
    }

    /**
     * Represents a single finding in JSON format.
     * Ignores keys with null or empty values during serialization.
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class JsonFinding {
        @JsonProperty("Principal")
        private final Map<String, List<String>> principal;

        @JsonProperty("Action")
        private final List<String> actions;

        @JsonProperty("Resource")
        private final List<String> resources;

        @JsonProperty("Condition")
        private final Map<String, List<String>> condition;

        /**
         * Constructs a JsonFinding object.
         *
         * @param principal the principal information
         * @param actions   the action information
         * @param resources the resource information
         * @param condition the condition information
         */
        public JsonFinding(Map<String, List<String>> principal, List<String> actions, List<String> resources, Map<String, List<String>> condition) {
            this.principal = principal;
            this.actions = actions;
            this.resources = resources;
            this.condition = condition;
        }
    }

    /**
     * Writes the JsonIntent object to a JSON file.
     *
     * @param jsonFindings   the JsonIntent object to write
     * @param outputFilePath the output file path
     */
    public static void printToFile(JsonIntent jsonFindings, Path outputFilePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        DefaultIndenter indenter = new DefaultIndenter("\t", DefaultIndenter.SYS_LF);
        printer.indentArraysWith(indenter);
        printer.indentObjectsWith(indenter);

        ObjectWriter writer = objectMapper.writer(printer);

        try {
            File outputFile = outputFilePath.toFile();
            writer.writeValue(outputFile, jsonFindings);
//            Parameter.LOGGER.config("successfully written findings");
        } catch (IOException e) {
            Parameter.LOGGER.severe("an error occurred while writing to the file: " + e.getMessage());
        }
    }
}