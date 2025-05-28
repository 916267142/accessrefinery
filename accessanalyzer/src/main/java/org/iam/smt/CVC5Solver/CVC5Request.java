package org.iam.smt.CVC5Solver;

import io.github.cvc5.*;
import org.iam.smt.Request;
import org.iam.smt.SMTConstraintFactory;

import java.util.HashMap;
import java.util.Map;

public class CVC5Request implements Request {
    private final TermManager termManager;
    private final Term principalDomain;
    private final Term principalValue;
    private final Term action;
    private final Term resource;
    private final Map<String, Term> keyToTerm;

    public CVC5Request(TermManager termManager) {
        this.termManager = termManager;
        Sort stringSort = termManager.getStringSort();
        principalDomain = termManager.mkConst(stringSort, "principalDomain");
        principalValue = termManager.mkConst(stringSort, "principalValue");
        action = termManager.mkConst(stringSort, "action");
        resource = termManager.mkConst(stringSort, "resource");
        keyToTerm = new HashMap<>();
    }

    public CVC5Request(CVC5Request other) {
        termManager = other.termManager;
        principalDomain = other.principalDomain;
        principalValue = other.principalValue;
        action = other.action;
        resource = other.resource;
        keyToTerm = other.keyToTerm;
    }

    @Override
    public void addKey(Map<String, SMTConstraintFactory.VarType> keyToType) throws CVC5ApiException {
        for (Map.Entry<String, SMTConstraintFactory.VarType> entry : keyToType.entrySet()) {
            String key = entry.getKey();
            SMTConstraintFactory.VarType type = entry.getValue();

            Sort sort = switch (type) {
                case SEQSORT -> termManager.getStringSort();
                case BITVECSORT -> termManager.mkBitVectorSort(32);
            };

            keyToTerm.put(key, termManager.mkConst(sort, key));
        }
    }

    public TermManager getTermManager() {
        return termManager;
    }

    public Term getPrincipalDomain() {
        return principalDomain;
    }

    public Term getPrincipalValue() {
        return principalValue;
    }

    public Term getAction() {
        return action;
    }

    public Term getResource() {
        return resource;
    }

    public Map<String, Term> getKeyToTerm() {
        return keyToTerm;
    }
}
