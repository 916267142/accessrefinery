package org.iam.variables.statics;

import org.iam.smt.Request;
import org.iam.variables.dynamics.DynamicVar;

public abstract class StaticVar {
    public abstract int hashCode();

    public abstract boolean equals(Object o);

    public abstract DynamicVar convert(Request request);

    public abstract Object getValue();
}
