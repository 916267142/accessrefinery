package org.iam.policy.grammer;

/**
 * Interface for enums that can be compared and retrieved by string value.
 * <p>
 * Provides utility methods for case-insensitive string matching and conversion.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public interface StringComparableEnum {
    /**
     * Returns the string value associated with the enum constant.
     *
     * @return the string value
     */
    String getValue();

    /**
     * Checks if the given string matches any value in the enum (case-insensitive).
     *
     * @param value the string to check
     * @param enumClass the enum class
     * @return true if a match is found, false otherwise
     */
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

    /**
     * Returns the enum constant matching the given string value (case-insensitive).
     *
     * @param value the string value to match
     * @param enumClass the enum class
     * @param <T> the enum type
     * @return the matching enum constant
     * @throws IllegalArgumentException if no match is found
     */
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

