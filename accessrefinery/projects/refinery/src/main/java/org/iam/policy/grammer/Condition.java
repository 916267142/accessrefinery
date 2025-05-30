package org.iam.policy.grammer;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Condition represents a condition block with an operator and key-value pairs.
 * <p>
 * Used for expressing constraints in policies, where each condition has an operator
 * and a mapping from keys to lists of string values.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public class Condition {
    /**
     * The operator for this condition.
     */
    protected VarOperator operator;

    /**
     * Mapping from condition keys to their list of values.
     */
    protected HashMap<String, List<String>> keyToValues;

    /**
     * Copy constructor.
     *
     * @param other the Condition to copy
     */
    public Condition(Condition other) {
        this.operator = other.operator;
        this.keyToValues = other.keyToValues;
    }

    /**
     * Constructs a Condition with the given operator and key-value mapping.
     *
     * @param operator the operator
     * @param keyToValues the key-value mapping
     */
    public Condition(VarOperator operator, HashMap<String, List<String>> keyToValues) {
        this.operator = operator;
        this.keyToValues = keyToValues;
    }

    public VarOperator getOperator() {
        return operator;
    }

    public void setOperator(VarOperator operator) {
        this.operator = operator;
    }

    public HashMap<String, List<String>> getKeyToValues() {
        return keyToValues;
    }

    public void setKeyToValues(HashMap<String, List<String>> keyToValues) {
        this.keyToValues = keyToValues;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Condition condition = (Condition) o;
        return operator == condition.operator && Objects.equals(keyToValues, condition.keyToValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, keyToValues);
    }

    /**
     * Enum for supported condition operators.
     * <p>
     * This enum defines the set of operators that can be used in policy conditions.
     * Operators specify how key-value pairs are evaluated, including string equality,
     * pattern matching, case-insensitive checks, ARN and IP address comparisons, and more.
     * </p>
     */
    public enum VarOperator implements StringComparableEnum {
        FOR_ALL_VALUES_STRING_EQUALS("ForAllValues:StringEquals"),
        FOR_ALL_VALUES_STRING_NOT_EQUALS("ForAllValues:StringNotEquals"),
        FOR_ALL_VALUES_STRING_EQUALS_IGNORE_CASE("ForAllValues:StringEqualsIgnoreCase"),
        FOR_ALL_VALUES_STRING_NOT_EQUALS_IGNORE_CASE("ForAllValues:StringNotEqualsIgnoreCase"),
        FOR_ALL_VALUES_STRING_MATCH("ForAllValues:StringMatch"),
        FOR_ALL_VALUES_STRING_NOT_MATCH("ForAllValues:StringNotMatch"),
        FOR_ALL_VALUES_STRING_Like("ForAllValues:StringLike"),
        FOR_ALL_VALUES_STRING_NOT_Like("ForAllValues:StringNotLike"),

        FOR_ANY_VALUE_STRING_EQUALS("ForAnyValue:StringEquals"),
        FOR_ANY_VALUE_STRING_NOT_EQUALS("ForAnyValue:StringNotEquals"),
        FOR_ANY_VALUE_STRING_EQUALS_IGNORE_CASE("ForAnyValue:StringEqualsIgnoreCase"),
        FOR_ANY_VALUE_STRING_NOT_EQUALS_IGNORE_CASE("ForAnyValue:StringNotEqualsIgnoreCase"),
        FOR_ANY_VALUE_STRING_MATCH("ForAnyValue:StringMatch"),
        FOR_ANY_VALUE_STRING_NOT_MATCH("ForAnyValue:StringNotMatch"),
        FOR_ANY_VALUE_STRING_Like("ForAnyValue:StringLike"),
        FOR_ANY_VALUE_STRING_NOT_Like("ForAnyValue:StringNotLike"),

        STRING_EQUALS("StringEquals"),
        STRING_NOT_EQUALS("StringNotEquals"),
        STRING_EQUALS_IGNORE_CASE("StringEqualsIgnoreCase"),
        STRING_NOT_EQUALS_IGNORE_CASE("StringNotEqualsIgnoreCase"),
        STRING_EQUALS_IF_EXISTS("StringEqualsIfExists"),
        STRING_NOT_EQUALS_IF_EXISTS("StringNotEqualsIfExists"),

        STRING_MATCH("StringMatch"),
        STRING_NOT_MATCH("StringNotMatch"),
        STRING_Like("StringLike"),
        STRING_NOT_Like("StringNotLike"),
        STRING_MATCH_IF_EXISTS("StringMatchIfExists"),
        STRING_NOT_MATCH_IF_EXISTS("StringNotMatchIfExists"),

        ARN_LIKE("ArnLike"),
        ARN_NOT_LIKE("ArnNotLike"),

        IPADDRESS("IpAddress"),
        NOTIPADDRESS("NotIpAddress");

        private final String value;

        VarOperator(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

        public static boolean isValid(String value) {
            return StringComparableEnum.isValid(value, VarOperator.class);
        }

        public static VarOperator fromString(String value) {
            return StringComparableEnum.fromString(value, VarOperator.class);
        }
    }
}
