package org.iam.policy.grammer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Jackson deserializer for lists of strings, with regex formatting.
 * <p>
 * Converts JSON string or array of strings to a list, formatting each string
 * for regex matching ('.' to '\\.', '*' to '.*', '?' to '.').
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public class StringDeserializer extends JsonDeserializer<List<String>> {

    /**
     * Deserializes a JSON node into a list of formatted strings.
     *
     * @param jp      the JSON parser
     * @param context the deserialization context
     * @return a list of formatted strings
     * @throws IOException if an I/O error occurs
     */
    @Override
    public List<String> deserialize(JsonParser jp, DeserializationContext context) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        List<String> resources = new ArrayList<>();

        if (node.isArray()) {
            for (JsonNode resourceNode : node) {
                resources.add(formatRegex(resourceNode.asText()));
            }
        } else {
            resources.add(formatRegex(node.asText()));
        }
        return resources;
    }

    /**
     * Formats a string for regex matching by escaping '.' and converting '*' and '?'.
     *
     * @param source the input string
     * @return the formatted regex string
     */
    private String formatRegex(String source) {
        return source.replace(".", "\\.").replace("*", ".*").replace("?", ".");
    }
}
