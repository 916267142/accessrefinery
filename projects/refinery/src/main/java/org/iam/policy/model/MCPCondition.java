package org.iam.policy.model;

import com.google.common.collect.ImmutableSet;

import org.iam.model.MCPVar;
import org.iam.policy.grammer.Condition;
import org.iam.utils.Parameter;
import org.mcp.core.MCPBitVector;
import org.mcp.core.MCPFactory;
import org.mcp.variables.statics.LabelType;
import org.batfish.datamodel.Prefix;

import java.util.List;
import java.util.Map;

/**
 * MCPCondition extends Condition and implements MCPVar for symbolic encoding.
 * <p>
 * Represents a condition that can be encoded as an MCPBitVector for symbolic reasoning.
 * Provides methods to initialize the symbolic factory, compute the symbolic node,
 * and handle various string and prefix operators.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public class MCPCondition extends Condition implements MCPVar {
    /**
     * MCPFactory for symbolic encoding (shared by all MCPCondition instances).
     */
    static private MCPFactory _mcpFactory;

    /**
     * Symbolic encoding node for this condition.
     */
    private MCPBitVector mcpNode;

    /**
     * Constructs an MCPCondition by copying from another Condition.
     *
     * @param other the Condition to copy
     */
    public MCPCondition(Condition other) {
        super(other);
    }

    /**
     * Sets the MCPFactory for all MCPCondition instances.
     *
     * @param mcpFactory the MCPFactory to use
     */
    public static void setMCPFactory(MCPFactory mcpFactory) {
        _mcpFactory = mcpFactory;
    }

    /**
     * Returns the MCPFactory used by all MCPCondition instances.
     *
     * @return the MCPFactory
     */
    public static MCPFactory getMCPFactory() {
        return _mcpFactory;
    }

    /**
     * Initializes the MCPFactory with domain variables based on this condition's key-value pairs.
     * Adds each domain variable to the factory with the appropriate static variable type.
     */
    @Override
    public void initialMCPFactory() {
        if (operator == null && keyToValues == null) {
            return;
        }
        if(Parameter.isSplitLabel)  {
            for(Map.Entry<String, List<String>> entry : keyToValues.entrySet()) {
                switch (operator) {
                    case IPADDRESS:
                        for(String value : entry.getValue()) {
                             _mcpFactory.addVar(entry.getKey(), LabelType.PREFIX, Prefix.parse(processPrefix(value)));
                        }
                        break;
                    case NOTIPADDRESS:
                        for(String value : entry.getValue()) {
                             _mcpFactory.addVar(entry.getKey(), LabelType.PREFIX, Prefix.parse(processPrefix(value)));
                        }
                        break;
                    default:
                        for(String value : entry.getValue()) {
                            _mcpFactory.addVar(entry.getKey(), LabelType.REGEXP, value);
                        }
                        break;
                }
            }
        } else {
            for(Map.Entry<String, List<String>> entry : keyToValues.entrySet()) {
                switch (operator) {
                    case IPADDRESS:
                        _mcpFactory.addVar(entry.getKey(), 
                            LabelType.PREFIX_SET, 
                            ImmutableSet.copyOf(entry.getValue().stream().map(MCPCondition::processPrefix).map(Prefix::parse).toList()));
                        break;
                    case NOTIPADDRESS:
                        _mcpFactory.addVar(entry.getKey(), 
                            LabelType.PREFIX_SET, 
                            ImmutableSet.copyOf(entry.getValue().stream().map(MCPCondition::processPrefix).map(Prefix::parse).toList()));
                        break;
                    default:
                         _mcpFactory.addVar(entry.getKey(), LabelType.REGEXP_SET, ImmutableSet.copyOf(entry.getValue()));
                        break;
                }
            }
        }

    }

    /**
     * Processes a prefix string, appending "/32" if not already present.
     *
     * @param prefix the input prefix string
     * @return the processed prefix string
     */
    public static String processPrefix(String prefix) {
        return !prefix.contains("/") ? prefix + "/32" : prefix;
    }

    /**
     * Computes and returns the symbolic encoding node for this condition.
     * Returns the cached value if already computed.
     *
     * @return the MCPBitVector node for this condition
     */
    @Override
    public MCPBitVector getMCPNodeCalculation() {
        if (mcpNode != null) {
            return mcpNode;
        }
        if (operator == null || keyToValues == null) {
            return mcpNode = _mcpFactory.getTrue();
        }

        // Here, we simply treat all operation are same for experiment reason.
        // But it could be easily treated more detailed.
        if (operator.equals(VarOperator.STRING_EQUALS) ||
                operator.equals(VarOperator.STRING_MATCH) ||
                operator.equals(VarOperator.STRING_Like) ||
                operator.equals(VarOperator.FOR_ALL_VALUES_STRING_EQUALS) ||
                operator.equals(VarOperator.FOR_ALL_VALUES_STRING_MATCH) ||
                operator.equals(VarOperator.FOR_ANY_VALUE_STRING_EQUALS) ||
                operator.equals(VarOperator.FOR_ANY_VALUE_STRING_MATCH) ||
                operator.equals(VarOperator.STRING_EQUALS_IF_EXISTS) ||
                operator.equals(VarOperator.STRING_MATCH_IF_EXISTS) ||
                operator.equals(VarOperator.ARN_LIKE)) {
            MCPBitVector curr = _mcpFactory.getTrue();
            for (Map.Entry<String, List<String>> keyToValue : keyToValues.entrySet()) {
                String key = keyToValue.getKey();
                List<String> values = keyToValue.getValue();
                MCPBitVector currDomain = _mcpFactory.getFalse();
                if(Parameter.isSplitLabel) {
                    for (String value : values) {
                        currDomain = currDomain.or(_mcpFactory.getVarFillOtherDomain(key, value));
                    }
                } else {
                    currDomain = _mcpFactory.getVarFillOtherDomain(key, ImmutableSet.copyOf(values));
                }
                curr = curr.and(currDomain);
            }
            return mcpNode = curr;
        }

        if (operator.equals(VarOperator.STRING_NOT_MATCH) ||
                operator.equals(VarOperator.STRING_NOT_EQUALS) ||
                operator.equals(VarOperator.STRING_NOT_Like) ||
                operator.equals(VarOperator.FOR_ALL_VALUES_STRING_NOT_EQUALS) ||
                operator.equals(VarOperator.FOR_ALL_VALUES_STRING_NOT_MATCH) ||
                operator.equals(VarOperator.FOR_ANY_VALUE_STRING_NOT_EQUALS) ||
                operator.equals(VarOperator.FOR_ANY_VALUE_STRING_NOT_MATCH) ||
                operator.equals(VarOperator.STRING_NOT_EQUALS_IF_EXISTS) ||
                operator.equals(VarOperator.STRING_NOT_MATCH_IF_EXISTS) ||
                operator.equals(VarOperator.ARN_NOT_LIKE)) {
            MCPBitVector curr = _mcpFactory.getTrue();
            for (Map.Entry<String, List<String>> keyToValue : keyToValues.entrySet()) {
                String key = keyToValue.getKey();
                List<String> values = keyToValue.getValue();
                MCPBitVector currDomain = _mcpFactory.getTrue();
                if(Parameter.isSplitLabel) {
                    for (String value : values) {
                        currDomain = currDomain.diff(_mcpFactory.getVarFillOtherDomain(key, value));
                    }
                } else {
                    currDomain = currDomain.diffWith(_mcpFactory.getVarFillOtherDomain(key, ImmutableSet.copyOf(values)));
                }
                curr = curr.and(currDomain);
            }
            return mcpNode = curr;
        }

        // Don't use Parameter.isSplitLabel = false, this situation is not considered.
        if (operator.equals(VarOperator.STRING_EQUALS_IGNORE_CASE) ||
                operator.equals(VarOperator.FOR_ALL_VALUES_STRING_EQUALS_IGNORE_CASE) ||
                operator.equals(VarOperator.FOR_ANY_VALUE_STRING_EQUALS_IGNORE_CASE)) {
            MCPBitVector curr = _mcpFactory.getFalse();
            for (Map.Entry<String, List<String>> keyToValue : keyToValues.entrySet()) {
                String key = keyToValue.getKey();
                List<String> values = keyToValue.getValue();
                MCPBitVector currDomain = _mcpFactory.getFalse();
                if(Parameter.isSplitLabel) {
                    for (String value : values) {
                        currDomain = currDomain.or(_mcpFactory.getVarFillOtherDomain(key, ignoreCase(value)));
                    }
                } else {
                    currDomain = _mcpFactory.getVarFillOtherDomain(key, ImmutableSet.copyOf(ImmutableSet.copyOf(values.stream().map(MCPCondition::ignoreCase).toList())));
                }
                curr = curr.and(currDomain);
            }
            return mcpNode = curr;
        }

        if (operator.equals(VarOperator.STRING_NOT_EQUALS_IGNORE_CASE) ||
                operator.equals(VarOperator.FOR_ALL_VALUES_STRING_NOT_EQUALS_IGNORE_CASE) ||
                operator.equals(VarOperator.FOR_ANY_VALUE_STRING_NOT_EQUALS_IGNORE_CASE)) {
            MCPBitVector curr = _mcpFactory.getTrue();
            for (Map.Entry<String, List<String>> keyToValue : keyToValues.entrySet()) {
                String key = keyToValue.getKey();
                List<String> values = keyToValue.getValue();
                MCPBitVector currDomain = _mcpFactory.getTrue();
                if(Parameter.isSplitLabel) {
                    for (String value : values) {
                        currDomain = currDomain.diff(_mcpFactory.getVarFillOtherDomain(key, ignoreCase(value)));
                    }
                } else {
                    currDomain = currDomain.diffWith(_mcpFactory.getVarFillOtherDomain(key, ImmutableSet.copyOf(values.stream().map(MCPCondition::ignoreCase).toList())));
                }
                curr = curr.and(currDomain);
            }
            return mcpNode = curr;
        } 

        if (operator.equals(VarOperator.IPADDRESS)) {
            MCPBitVector curr = _mcpFactory.getTrue();
            for (Map.Entry<String, List<String>> keyToValue : keyToValues.entrySet()) {
                String key = keyToValue.getKey();
                List<String> values = keyToValue.getValue();
                MCPBitVector currDomain = _mcpFactory.getFalse();
                if(Parameter.isSplitLabel){
                    for (String value : values) {
                        currDomain = currDomain.or(_mcpFactory.getVarFillOtherDomain(key, Prefix.parse(processPrefix(value))));
                    }
                } else {
                    currDomain = currDomain.or(_mcpFactory.getVarFillOtherDomain(key, 
                    ImmutableSet.copyOf(values.stream().map(MCPCondition::processPrefix).map(Prefix::parse).toList())));
                }
                curr = curr.and(currDomain);
            }
            return mcpNode = curr;
        }

        if (operator.equals(VarOperator.NOTIPADDRESS)) {
            MCPBitVector curr = _mcpFactory.getTrue();
            for (Map.Entry<String, List<String>> keyToValue : keyToValues.entrySet()) {
                String key = keyToValue.getKey();
                List<String> values = keyToValue.getValue();
                MCPBitVector currDomain = _mcpFactory.getTrue();
                if(Parameter.isSplitLabel){
                    for (String value : values) {
                        currDomain = currDomain.diff(_mcpFactory.getVarFillOtherDomain(key, Prefix.parse(processPrefix(value))));
                    }
                } else {
                    currDomain = currDomain.diffWith(_mcpFactory.getVarFillOtherDomain(key, 
                    ImmutableSet.copyOf(values.stream().map(MCPCondition::processPrefix).map(Prefix::parse).toList())));
                }
                curr = curr.and(currDomain);
            }
            return mcpNode = curr;
        }

        // Other operation could be easily achieved in here

        return mcpNode = _mcpFactory.getTrue();
    }

    /**
     * Returns the symbolic encoding node for this condition.
     *
     * @return the MCPBitVector node
     */
    public MCPBitVector getMCPNode() {
        return mcpNode;
    }

    /**
     * Converts a string to a case-insensitive regex pattern.
     *
     * @param str the input string
     * @return a regex string for case-insensitive matching
     */
    public static String ignoreCase(String str) {
        StringBuilder builder = new StringBuilder();
        str.chars().forEach(
                c -> {
                    if (c >= 'a' && c <= 'z')
                        builder
                                .append("(")
                                .append((char) c)
                                .append("|")
                                .append((char) Character.toUpperCase(c))
                                .append(")");
                    else if (c >= 'A' && c <= 'Z')
                        builder
                                .append("(")
                                .append((char) Character.toLowerCase(c))
                                .append("|")
                                .append((char) c)
                                .append(")");
                    else builder.append((char) c);
                });
        return builder.toString();
    }

    /**
     * Returns a string representation of this MCPCondition.
     *
     * @return a string representation
     */
    @Override
    public String toString() {
        return "MCPCondition{" +
                "operator=" + operator +
                ", keyToValues=" + keyToValues +
                '}' + "\n";
    }
}