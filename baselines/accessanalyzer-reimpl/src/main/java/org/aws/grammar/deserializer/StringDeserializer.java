package org.aws.grammar.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringDeserializer extends JsonDeserializer<Set<String>> {

    @Override
    public Set<String> deserialize(JsonParser jp, DeserializationContext context) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        Set<String> resources = new HashSet<>();

        if (node.isArray()) {
            for (JsonNode resourceNode : node) {
                resources.add(resourceNode.asText());
            }
        } else {
            resources.add(node.asText());
        }
        return resources;
    }
}