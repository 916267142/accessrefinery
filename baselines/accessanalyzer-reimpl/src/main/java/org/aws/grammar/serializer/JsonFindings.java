package org.aws.grammar.serializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.aws.grammar.Condition;
import org.aws.grammar.Principal;
import org.aws.grammar.Finding;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Converts a set of Finding objects into a JSON structure.
 * Ignores keys with null or empty values during serialization.
 *
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JsonFindings {
    @JsonProperty("Findings")
    private final Set<JsonFindings.JsonFinding> findings;

    public JsonFindings(Set<Finding> findingSet) {
        this.findings = new HashSet<>();
        for (Finding finding : findingSet) {
            this.findings.add(processFinding(finding));
        }
    }

    public JsonFindings(Finding finding) {
        this.findings = new HashSet<>();
        this.findings.add(processFinding(finding));
    }

    private JsonFinding processFinding(Finding finding) {
        Map<String, Set<String>> principalMap = new HashMap<>();
        Map<String, Set<String>> conditionMap = new HashMap<>();
        Set<String> actions;
        Set<String> resources;

        if (finding.getPrincipal() != null) {
            for (Principal principal : finding.getPrincipal()) {
                String key = principal.getDomainName();
                Set<String> values = principal.getValues();
                principalMap.put(key, values);
            }
        }
        actions = finding.getAction();
        resources = finding.getResource();

        if (finding.getCondition() != null) {
            for (Condition condition : finding.getCondition()) {
                conditionMap.putAll(condition.getKeyToValues());
            }
        }

        return new JsonFindings.JsonFinding(principalMap, actions, resources, conditionMap);
    }
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class JsonFinding {
        @JsonProperty("Principal")
        private final Map<String, Set<String>> principal;

        @JsonProperty("Action")
        private final Set<String> actions;

        @JsonProperty("Resource")
        private final Set<String> resources;

        @JsonProperty("Condition")
        private final Map<String, Set<String>> condition;

        public JsonFinding(Map<String, Set<String>> principal,
                           Set<String> actions,
                           Set<String> resources,
                           Map<String, Set<String>> condition) {
            this.principal = principal;
            this.actions = actions;
            this.resources = resources;
            this.condition = condition;
        }
    }

    /**
     * Prints the JsonFindings object to a JSON file.
     *
     * @param jsonFindings   The JsonFindings object to print.
     * @param outputFilePath The path of the output JSON file.
     */
    public static void printToFile(JsonFindings jsonFindings, Path outputFilePath) {
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
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }
}
