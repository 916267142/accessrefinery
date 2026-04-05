package org.iam.grammar.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.iam.grammar.Condition;

import java.io.IOException;
import java.util.*;

public class ConditionDeserializer extends JsonDeserializer<Set<Condition>> {

    @Override
    public Set<Condition> deserialize(JsonParser jp, DeserializationContext context)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        Set<Condition> conditions = new HashSet<>();
        Iterator<Map.Entry<String, JsonNode>> operators = node.fields();

        while (operators.hasNext()) {
            Map.Entry<String, JsonNode> entry = operators.next();
            String operator = entry.getKey();
            JsonNode kvMaps = entry.getValue();

            Iterator<Map.Entry<String, JsonNode>> kvFields = kvMaps.fields();

            HashMap<String, Set<String>> keyToValues = new HashMap<>();

            while (kvFields.hasNext()) {
                Map.Entry<String, JsonNode> kvMap = kvFields.next();
                String key = kvMap.getKey();
                JsonNode values = kvMap.getValue();
                Set<String> valueSet = new HashSet<>();

                if (!values.isArray()) {
                    valueSet.add(values.asText());
                } else {
                    for (JsonNode value : values) {
                        valueSet.add(value.asText());
                    }
                }
                keyToValues.put(key, valueSet);
            }
            Condition condition = new Condition(Condition.VarOperator.fromString(operator), keyToValues);
            conditions.add(condition);
        }
        return conditions;
    }
}