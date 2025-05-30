package org.iam.variables.statics;

import org.iam.variables.dynamics.OperableLabel;

public abstract class Label {
    public abstract int hashCode();

    public abstract boolean equals(Object o);

    public abstract OperableLabel convert();

    public abstract Object getValue();
}