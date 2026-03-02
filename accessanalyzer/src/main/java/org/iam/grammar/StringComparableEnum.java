package org.iam.grammar;

public interface StringComparableEnum {
    String getValue();

    // Utility method for comparing string values (case-insensitive)
    static boolean isValid(String value, Class<? extends Enum<?>> enumClass) {
        if (value == null) {
            return false;
        }

        String lowerCaseValue = value.toLowerCase();
        for (Enum<?> constant : enumClass.getEnumConstants()) {
            if (((StringComparableEnum) constant).getValue().toLowerCase().equals(lowerCaseValue)) {
                return true;
            }
        }
        return false;
    }

    // Utility method for getting enum by matching string value (case-insensitive)
    static <T extends Enum<T> & StringComparableEnum> T fromString(String value, Class<T> enumClass) {
        if (value != null) {
            String lowerCaseValue = value.toLowerCase();
            for (T attribute : enumClass.getEnumConstants()) {
                if (attribute.getValue().toLowerCase().equals(lowerCaseValue)) {
                    return attribute;
                }
            }
        }
        throw new IllegalArgumentException("Unexpected value: " + value);
    }
}

