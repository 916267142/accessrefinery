package org.iam.policy.grammer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Custom deserializer for a list of Principal objects from JSON.
 * <p>
 * This class parses a JSON structure where each field represents a principal type,
 * and its value is either a string or an array of strings. Wildcard and regex formatting
 * is applied to each value.
 * </p>
 */
public class PrincipalDeserializer extends JsonDeserializer<List<Principal>> {

    /**
     * Deserializes JSON into a list of Principal objects.
     *
     * @param jp      the JSON parser
     * @param context the deserialization context
     * @return a list of Principal objects
     * @throws IOException if an I/O error occurs or the JSON is invalid
     */
    @Override
    public List<Principal> deserialize(JsonParser jp, DeserializationContext context)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        List<Principal> principals = new ArrayList<>();
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String prpKey = field.getKey();
            List<String> values = new ArrayList<>();

            JsonNode arrayNode = field.getValue();

            if (arrayNode.isArray()) {
                for (JsonNode valueNode : arrayNode) {
                    values.add(formatRegex(valueNode.asText()));
                }
            } else if (arrayNode.isTextual()) {
                values.add(formatRegex(arrayNode.asText()));
            } else {
                throw new IOException("Invalid principal value: " + field.getValue());
            }

            principals.add(new Principal(prpKey, values));
        }
        return principals;
    }

    /**
     * Formats a string for regex matching by escaping dots and converting wildcards.
     *
     * @param source the original string
     * @return the formatted string
     */
    private String formatRegex(String source) {
        return source.replace(".", "\\.").replace("*", ".*").replace("?", ".");
    }
}
