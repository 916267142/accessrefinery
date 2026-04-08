package org.aws.variables.dynamics;

import io.github.cvc5.Kind;
import io.github.cvc5.Solver;
import io.github.cvc5.Term;
import io.github.cvc5.TermManager;
import org.aws.smt.CVC5Solver.CVC5Request;

public class CVC5DynamicVar extends DynamicVar{
    private final TermManager termManager;
    private final Term value;

    public CVC5DynamicVar(TermManager termManager, Term value) {
        this.termManager = termManager;
        this.value = value;
    }

    public CVC5DynamicVar(CVC5Request request, Term value) {
        termManager = request.getTermManager();
        this.value = value;
    }

    @Override
    public DynamicVar union(DynamicVar other) {
        if (!(other instanceof CVC5DynamicVar)) {
            throw new IllegalArgumentException("Invalid type for CVC5 union operation:" + other.getClass());
        }
        CVC5DynamicVar otherCVC5Var = (CVC5DynamicVar) other;
        if (this.termManager != otherCVC5Var.termManager) {
            throw new IllegalArgumentException("Could not perform union on different contexts");
        }

        Term unionTerm = termManager.mkTerm(Kind.OR, this.value, otherCVC5Var.value);
        return new CVC5DynamicVar(this.termManager, unionTerm);
    }

    @Override
    public DynamicVar inter(DynamicVar other) {
        if (!(other instanceof CVC5DynamicVar)) {
            throw new IllegalArgumentException("Invalid type for CVC5 intersection operation:" + other.getClass());
        }
        CVC5DynamicVar otherCVC5Var = (CVC5DynamicVar) other;
        if (this.termManager != otherCVC5Var.termManager) {
            throw new IllegalArgumentException("Could not perform intersection on different contexts");
        }

        Term interTerm = termManager.mkTerm(Kind.AND, this.value, otherCVC5Var.value);
        return new CVC5DynamicVar(this.termManager, interTerm);
    }

    @Override
    public DynamicVar minus(DynamicVar other) {
        if (!(other instanceof CVC5DynamicVar)) {
            throw new IllegalArgumentException("Invalid type for CVC5 minus operation:" + other.getClass());
        }
        CVC5DynamicVar otherCVC5Var = (CVC5DynamicVar) other;
        if (this.termManager != otherCVC5Var.termManager) {
            throw new IllegalArgumentException("Could not perform minus on different contexts");
        }

        Term minusTerm = termManager.mkTerm(Kind.AND, this.value, termManager.mkTerm(Kind.NOT, otherCVC5Var.value));
        return new CVC5DynamicVar(this.termManager, minusTerm);
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public boolean isEmpty() {
        Solver solver = new Solver(termManager);
        solver.assertFormula(value);
        return solver.checkSat().isUnsat();
    }

    @Override
    public String toString() {
        return "CVC5DynamicVar{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CVC5DynamicVar that = (CVC5DynamicVar) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
