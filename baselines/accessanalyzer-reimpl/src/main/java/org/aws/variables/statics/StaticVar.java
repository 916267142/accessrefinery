package org.aws.variables.statics;

import org.aws.smt.Request;
import org.aws.variables.dynamics.DynamicVar;

public abstract class StaticVar {
    public abstract int hashCode();

    public abstract boolean equals(Object o);

    public abstract DynamicVar convert(Request request);

    public abstract Object getValue();
}
