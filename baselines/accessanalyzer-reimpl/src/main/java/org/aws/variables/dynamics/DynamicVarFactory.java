package org.aws.variables.dynamics;

import org.aws.smt.Request;

public class DynamicVarFactory {
    public static DynamicVar createVar(DynamicVarType type, Request request, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null.");
        }
        if (!type.getValueClass().isInstance(value)) {
            throw new IllegalArgumentException("Value type does not match the expected type for " + type.name());
        }
        try {
            return type.getDynamicVarClass().getConstructor(request.getClass(), type.getValueClass()).newInstance(request, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create DynamicVar instance for type " + type.name(), e);
        }
    }
}