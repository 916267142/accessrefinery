package org.aws.grammar.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aws.grammar.Condition;
import org.aws.grammar.Finding;
import org.aws.grammar.Principal;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FindingDeserializer extends JsonDeserializer<Finding> {

    @Override
    public Finding deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);
        ObjectMapper mapper = (ObjectMapper) codec;

        Finding finding = new Finding();

        if (node.has("Action")) {
            finding.setAction(mapper.convertValue(node.get("Action"), new TypeReference<Set<String>>() {}));
        }

        if (node.has("Resource")) {
            finding.setResource(mapper.convertValue(node.get("Resource"), new TypeReference<Set<String>>() {}));
        }

        if (node.has("Principal")) {
            JsonNode principalNode = node.get("Principal");
            Set<Principal> principals = new HashSet<>();
            if (principalNode.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> fields = principalNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    String domain = field.getKey();
                    Set<String> values = mapper.convertValue(field.getValue(), new TypeReference<Set<String>>() {});
                    principals.add(new Principal(domain, values));
                }
            }
            finding.setPrincipal(principals);
        }

        if (node.has("Condition")) {
            JsonNode conditionNode = node.get("Condition");
            Set<Condition> conditions = new HashSet<>();
            if (conditionNode.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> fields = conditionNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    String key = field.getKey();
                    Set<String> values = mapper.convertValue(field.getValue(), new TypeReference<Set<String>>() {});

                    Map<String, Set<String>> map = new HashMap<>();
                    map.put(key, values);

                    Condition.VarOperator operator = Condition.VarOperator.STRING_MATCH;
                    if (!values.isEmpty()) {
                        String firstValue = values.iterator().next();
                        if (isCidr(firstValue)) {
                            operator = Condition.VarOperator.IP_ADDRESS;
                        }
                    }

                    conditions.add(new Condition(operator, map));
                }
            }
            finding.setCondition(conditions);
        }

        return finding;
    }

    private boolean isCidr(String value) {
        if (value == null) return false;
        // Basic Check for IPv4 and IPv4 CIDR (e.g., 192.168.1.1 or 192.168.1.0/24)
        return value.matches("^\\d{1,3}(\\.\\d{1,3}){3}(/\\d+)?$");
    }
}
