package org.iam.policy.grammer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.*;

/**
 * Custom deserializer for a list of Condition objects from JSON.
 * <p>
 * This class parses a JSON structure where each field is a condition operator,
 * and its value is a mapping from keys to string or array of strings.
 * It also handles wildcard and regex formatting for non-IP operators.
 * </p>
 */
public class ConditionDeserializer extends JsonDeserializer<List<Condition>> {

    /**
     * Deserializes JSON into a list of Condition objects.
     *
     * @param jp      the JSON parser
     * @param context the deserialization context
     * @return a list of Condition objects
     * @throws IOException if an I/O error occurs
     */
    @Override
    public List<Condition> deserialize(JsonParser jp, DeserializationContext context)
            throws IOException {
        // Parse the root JSON node
        JsonNode node = jp.getCodec().readTree(jp);
        List<Condition> conditions = new ArrayList<>();
        Iterator<Map.Entry<String, JsonNode>> operators = node.fields();

        // Iterate over each operator in the JSON
        while (operators.hasNext()) {
            Map.Entry<String, JsonNode> entry = operators.next();
            String operator = entry.getKey();
            JsonNode kvMaps = entry.getValue();

            Iterator<Map.Entry<String, JsonNode>> kvFields = kvMaps.fields();

            HashMap<String, List<String>> keyToValues = new HashMap<>();

            // Iterate over each key-value mapping for the operator
            while (kvFields.hasNext()) {
                Map.Entry<String, JsonNode> kvMap = kvFields.next();
                String key = kvMap.getKey();
                JsonNode values = kvMap.getValue();
                List<String> valueList = new ArrayList<>();
                // IP address operators do not require wildcard/regex formatting
                boolean isNotReplace = operator.equalsIgnoreCase("IpAddress") || operator.equalsIgnoreCase("NotIpAddress");
                if (!values.isArray()) {
                    valueList.add(formatRegex(values.asText(), isNotReplace));
                } else {
                    for (JsonNode value : values) {
                        valueList.add(formatRegex(value.asText(), isNotReplace));
                    }
                }
                keyToValues.put(key, valueList);
            }
            // Create and add the Condition object
            Condition condition = new Condition(Condition.VarOperator.fromString(operator), keyToValues);
            conditions.add(condition);
        }
        return conditions;
    }

    /**
     * Formats a string for regex matching, unless the operator is for IP addresses.
     *
     * @param source       the original string
     * @param isNotReplace true if formatting should be skipped (for IP operators)
     * @return the formatted string
     */
    private String formatRegex(String source, boolean isNotReplace) {
        if (isNotReplace) {
            return source;
        } else {
            // Replace '.' with '\.', '*' with '.*', and '?' with '.'
            return source.replace(".", "\\.").replace("*", ".*").replace("?", ".");
        }
    }
}
