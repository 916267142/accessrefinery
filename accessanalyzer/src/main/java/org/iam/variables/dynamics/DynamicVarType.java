package org.iam.variables.dynamics;

public enum DynamicVarType {
    Z3(com.microsoft.z3.BoolExpr.class, Z3DynamicVar.class),
    CVC5(io.github.cvc5.Term.class, CVC5DynamicVar.class);

    private final Class<?> valueClass;
    private final Class<? extends DynamicVar> dynamicVarClass;

    DynamicVarType(Class<?> valueClass, Class<? extends DynamicVar> dynamicVarClass) {
        this.valueClass = valueClass;
        this.dynamicVarClass = dynamicVarClass;
    }

    public Class<?> getValueClass() {
        return valueClass;
    }

    public Class<? extends DynamicVar> getDynamicVarClass() {
        return dynamicVarClass;
    }
}
