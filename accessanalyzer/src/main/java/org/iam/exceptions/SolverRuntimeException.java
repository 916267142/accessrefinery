package org.iam.exceptions;

import org.iam.config.Parameter;

public class SolverRuntimeException extends RuntimeException {
    private final Parameter.SolverType solverType;

    public SolverRuntimeException(Parameter.SolverType type, Throwable cause) {
        super("Solver " + type + " error", cause);
        this.solverType = type;
    }

    public <T extends Exception> T getSolverException(Class<T> exType) {
        if (exType.isInstance(this.getCause())) {
            return exType.cast(this.getCause());
        }
        throw new ClassCastException("Not a " + exType.getSimpleName());
    }
}
