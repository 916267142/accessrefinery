package org.mcp.variables.statics;

import org.mcp.variables.dynamics.OperableLabel;

public abstract class Label {
    public abstract int hashCode();

    public abstract boolean equals(Object o);

    public abstract OperableLabel convert();

    public abstract Object getValue();
}