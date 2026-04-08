package org.aws.smt;

import java.util.Map;

public interface Request {
    void addKey(Map<String, SMTConstraintFactory.VarType> keyToType) throws Exception;
}
