package org.iam.grammer.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.iam.grammer.Principal;

import java.io.IOException;
import java.util.*;

public class PrincipalDeserializer extends JsonDeserializer<Set<Principal>> {

    @Override
    public Set<Principal> deserialize(JsonParser jp, DeserializationContext context)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        Set<Principal> principals = new HashSet<>();
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String prpKey = field.getKey();
            Set<String> values = new HashSet<>();

            JsonNode arrayNode = field.getValue();

            if (arrayNode.isArray()) {
                for (JsonNode valueNode : arrayNode) {
                    values.add(valueNode.asText());
                }
            } else if (arrayNode.isTextual()) {
                values.add(arrayNode.asText());
            } else {
                throw new IOException("Invalid principal value: " + field.getValue());
            }

            principals.add(new Principal(prpKey, values));
        }
        return principals;
    }
}