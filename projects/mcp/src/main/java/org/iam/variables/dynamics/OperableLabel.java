package org.iam.variables.dynamics;

public abstract class OperableLabel {
    public abstract OperableLabel union(OperableLabel other);

    public abstract OperableLabel inter(OperableLabel other);

    public abstract OperableLabel minus(OperableLabel other);

    public abstract Object getValue();

    public abstract boolean equals(Object o);

    public abstract int hashCode();

    public abstract String toString();

    public abstract boolean isEmpty();

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

