package org.aws.variables.dynamics;

public abstract class DynamicVar {
    public abstract DynamicVar union(DynamicVar other);

    public abstract DynamicVar inter(DynamicVar other);

    public abstract DynamicVar minus(DynamicVar other);

    public abstract Object getValue();

    public abstract boolean isEmpty();

    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
