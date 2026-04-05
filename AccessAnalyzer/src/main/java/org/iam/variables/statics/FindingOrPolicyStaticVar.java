package org.iam.variables.statics;

import com.microsoft.z3.BoolExpr;
import io.github.cvc5.Term;
import org.iam.config.Parameter;
import org.iam.grammar.Finding;
import org.iam.grammar.Policy;
import org.iam.smt.Request;
import org.iam.smt.SMTConstraintFactory;
import org.iam.variables.dynamics.DynamicVar;
import org.iam.variables.dynamics.DynamicVarFactory;
import org.iam.variables.dynamics.DynamicVarType;

public class FindingOrPolicyStaticVar extends StaticVar{
    private Finding finding;
    private Policy policy;
    private BoolExpr z3Expr;
    private Term cvc5Term;
    private VarType varType;
    private DynamicVar cachedDynamicVar;

    public FindingOrPolicyStaticVar(Builder builder) {
        this.finding = builder.finding;
        this.policy = builder.policy;
        this.z3Expr = builder.z3Expr;
        this.cvc5Term = builder.cvc5Term;
        this.varType = builder.varType;
    }

    public static class Builder {
        private Finding finding;
        private Policy policy;
        private BoolExpr z3Expr;
        private Term cvc5Term;
        private VarType varType;

        public Builder setPolicy(Policy policy) {
            this.policy = policy;
            this.varType = VarType.POLICY;
            return this;
        }

        public Builder setFinding(Finding finding) {
            this.finding = finding;
            this.varType = VarType.FINDING;
            return this;
        }

        public Builder setZ3Expr(BoolExpr z3Expr) {
            this.z3Expr = z3Expr;
            this.varType = VarType.Z3EXPR;
            return this;
        }

        public Builder setCVC5Term(Term cvc5Term) {
            this.cvc5Term = cvc5Term;
            this.varType = VarType.CVC5TERM;
            return this;
        }

        public FindingOrPolicyStaticVar build() {
            return new FindingOrPolicyStaticVar(this);
        }
    }

    public enum VarType {
        FINDING,
        POLICY,
        Z3EXPR,
        CVC5TERM
    }

    @Override
    public int hashCode() {
        switch (varType) {
            case FINDING:
                return finding.hashCode();
            case POLICY:
                return policy.hashCode();
            case Z3EXPR:
                return z3Expr.hashCode();
            case CVC5TERM:
                return cvc5Term.hashCode();
            default:
                throw new IllegalStateException("Unexpected value: " + varType);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FindingOrPolicyStaticVar that = (FindingOrPolicyStaticVar) o;
        if (varType != that.varType) return false;

        return switch (varType) {
            case FINDING -> finding != null && finding.equals(that.finding);
            case POLICY -> policy != null && policy.equals(that.policy);
            case Z3EXPR -> z3Expr != null && z3Expr.equals(that.z3Expr);
            case CVC5TERM -> cvc5Term != null && cvc5Term.equals(that.cvc5Term);
        };
    }

    @Override
    public DynamicVar convert(Request request) {
        if (cachedDynamicVar != null) {
            return cachedDynamicVar;
        }

        Object value = switch (varType) {
            case FINDING -> SMTConstraintFactory.convertToSMT(request, finding);
            case POLICY -> SMTConstraintFactory.convertToSMT(request, policy);
            case Z3EXPR -> z3Expr;
            case CVC5TERM -> cvc5Term;
        };

        cachedDynamicVar = DynamicVarFactory.createVar(
                switch (Parameter.getActiveSolver()) {
                    case Z3 -> DynamicVarType.Z3;
                    case CVC5 -> DynamicVarType.CVC5;
                },
                request,
                value
        );
        return cachedDynamicVar;
    }

    @Override
    public Object getValue() {
        return switch (varType) {
            case FINDING -> finding;
            case POLICY -> policy;
            case Z3EXPR -> z3Expr;
            case CVC5TERM -> cvc5Term;
        };
    }

    @Override
    public String toString() {
        return switch (varType) {
            case FINDING -> "Type: FINDING, Content: " + finding.toString();
            case POLICY -> "Type: POLICY, Content: " + policy.toString();
            case Z3EXPR -> "Type: Z3EXPR, Content: " + z3Expr.toString();
            case CVC5TERM -> "Type: CVC5TERM, Content: " + cvc5Term.toString();
        };
    }

    public VarType getVarType() {
        return varType;
    }
}
