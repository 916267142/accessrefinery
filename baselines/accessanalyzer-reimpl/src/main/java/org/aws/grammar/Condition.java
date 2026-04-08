package org.aws.grammar;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Condition {
    protected VarOperator operator;

    protected Map<String, Set<String>> keyToValues;

    public Condition(Condition other) {
        this.operator = other.operator;
        this.keyToValues = other.keyToValues;
    }

    public Condition(VarOperator operator, Map<String, Set<String>> keyToValues) {
        this.operator = operator;
        this.keyToValues = keyToValues;
    }

    public VarOperator getOperator() {
        return operator;
    }

    public void setOperator(VarOperator operator) {
        this.operator = operator;
    }

    public Map<String, Set<String>> getKeyToValues() {
        return keyToValues;
    }

    public void setKeyToValues(Map<String, Set<String>> keyToValues) {
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

    public enum VarOperator implements StringComparableEnum {
        STRING_MATCH("StringMatch"),
        STRING_NOT_MATCH("StringNotMatch"),
        STRING_LIKE("StringLike"),
        STRING_NOT_LIKE("StringNotLike"),
        STRING_EQUALS("StringEquals"),
        STRING_NOT_EQUALS("StringNotEquals"),
        STRING_EQUALS_IGNORE_CASE("StringEqualsIgnoreCase"),
        STRING_NOT_EQUALS_IGNORE_CASE("StringNotEqualsIgnoreCase"),

        IP_ADDRESS("IpAddress"),
        NOT_IP_ADDRESS("NotIpAddress"),

        STRING_EQUALS_IF_EXISTS("StringEqualsIfExists"),
        STRING_NOT_EQUALS_IF_EXISTS("StringNotEqualsIfExists"),
        STRING_MATCH_IF_EXISTS("StringMatchIfExists"),
        STRING_NOT_MATCH_IF_EXISTS("StringNotMatchIfExists"),
        STRING_EQUALS_IGNORE_CASE_IF_EXISTS("StringEqualsIgnoreCaseIfExists"),
        STRING_NOT_EQUALS_IGNORE_CASE_IF_EXISTS("StringNotEqualsIgnoreCaseIfExists"),

        IP_ADDRESS_IF_EXISTS("IpAddressIfExists"),
        NOT_IP_ADDRESS_IF_EXISTS("NotIpAddressIfExists"),

        FOR_ALL_VALUES_STRING_EQUALS("ForAllValues:StringEquals"),
        FOR_ALL_VALUES_STRING_NOT_EQUALS("ForAllValues:StringNotEquals"),
        FOR_ALL_VALUES_STRING_MATCH("ForAllValues:StringMatch"),
        FOR_ALL_VALUES_STRING_NOT_MATCH("ForAllValues:StringNotMatch"),
        FOR_ALL_VALUES_STRING_EQUALS_IGNORE_CASE("ForAllValues:StringEqualsIgnoreCase"),
        FOR_ALL_VALUES_STRING_NOT_EQUALS_IGNORE_CASE("ForAllValues:StringNotEqualsIgnoreCase"),

        FOR_ANY_VALUE_STRING_EQUALS("ForAnyValue:StringEquals"),
        FOR_ANY_VALUE_STRING_NOT_EQUALS("ForAnyValue:StringNotEquals"),
        FOR_ANY_VALUE_STRING_MATCH("ForAnyValue:StringMatch"),
        FOR_ANY_VALUE_STRING_NOT_MATCH("ForAnyValue:StringNotMatch"),
        FOR_ANY_VALUE_STRING_EQUALS_IGNORE_CASE("ForAnyValue:StringEqualsIgnoreCase"),
        FOR_ANY_VALUE_STRING_NOT_EQUALS_IGNORE_CASE("ForAnyValue:StringNotEqualsIgnoreCase"),

        ARN_LIKE("ArnLike"),
        ARN_NOT_LIKE("ArnNotLike");

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

        @Override
        public String toString() {
            return "VarOperator{" +
                    "value='" + value + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Condition{" +
                "operator=" + operator +
                ", keyToValues=" + keyToValues +
                '}';
    }
}
