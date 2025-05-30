package org.iam.variables.statics;

public class LabelFactory {
    public static Label createVar(LabelType type, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null.");
        }
        if (!type.getValueClass().isInstance(value)) {
            throw new IllegalArgumentException("Value type does not match the expected type for " + type.name());
        }
        try {
            return type.getLabelClass().getConstructor(type.getValueClass()).newInstance(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Label instance for type " + type.name(), e);
        }
    }
}