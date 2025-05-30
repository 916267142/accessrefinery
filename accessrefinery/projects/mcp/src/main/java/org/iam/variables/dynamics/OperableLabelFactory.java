package org.iam.variables.dynamics;

public class OperableLabelFactory {
    public static OperableLabel createVar(OperableLabelType type, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null.");
        }
        if (!type.getValueClass().isInstance(value)) {
            throw new IllegalArgumentException("Value type does not match the expected type for " + type.name());
        }
        try {
            return type.getOperableLabelClass().getConstructor(type.getValueClass()).newInstance(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create OperableLabel instance for type " + type.name(), e);
        }
    }
}
