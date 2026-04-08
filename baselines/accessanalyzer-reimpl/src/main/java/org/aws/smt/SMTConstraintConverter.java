package org.aws.smt;

import org.aws.exceptions.SolverRuntimeException;

public interface SMTConstraintConverter<T, R extends Request, I>{
    T toSMTConstraint(R request, I input) throws SolverRuntimeException;
}
