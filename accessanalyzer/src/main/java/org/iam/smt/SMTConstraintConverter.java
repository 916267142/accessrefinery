package org.iam.smt;

import org.iam.exceptions.SolverRuntimeException;

public interface SMTConstraintConverter<T, R extends Request, I>{
    T toSMTConstraint(R request, I input) throws SolverRuntimeException;
}
