package org.aws.variables.dynamics;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import org.aws.smt.Z3Solver.Z3Request;

public class Z3DynamicVar extends DynamicVar {
    private final Context context;
    private final BoolExpr value;

    public Z3DynamicVar(Context context, BoolExpr value) {
        this.context = context;
        this.value = value;
    }

    public Z3DynamicVar(Z3Request request, BoolExpr value) {
        context = request.getContext();
        this.value = value;
    }

    @Override
    public DynamicVar union(DynamicVar other) {
        if (!(other instanceof Z3DynamicVar)) {
            throw new IllegalArgumentException("Invalid type for Z3 union operation:" + other.getClass());
        }
        Z3DynamicVar otherZ3Var = (Z3DynamicVar) other;
        if (this.context != otherZ3Var.context) {
            throw new IllegalArgumentException("Could not perform union on different contexts");
        }

        BoolExpr unionExpr = context.mkOr(this.value, otherZ3Var.value);
        return new Z3DynamicVar(this.context, unionExpr);
    }

    @Override
    public DynamicVar inter(DynamicVar other) {
        if (!(other instanceof Z3DynamicVar)) {
            throw new IllegalArgumentException("Invalid type for Z3 intersection operation:" + other.getClass());
        }
        Z3DynamicVar otherZ3Var = (Z3DynamicVar) other;
        if (this.context != otherZ3Var.context) {
            throw new IllegalArgumentException("Could not perform intersection on different contexts");
        }

        BoolExpr interExpr = context.mkAnd(this.value, otherZ3Var.value);
        return new Z3DynamicVar(this.context, interExpr);
    }

    @Override
    public DynamicVar minus(DynamicVar other) {
        if (!(other instanceof Z3DynamicVar)) {
            throw new IllegalArgumentException("Invalid type for Z3 minus operation:" + other.getClass());
        }
        Z3DynamicVar otherZ3Var = (Z3DynamicVar) other;
        if (this.context != otherZ3Var.context) {
            throw new IllegalArgumentException("Could not perform minus on different contexts");
        }

        BoolExpr minusExpr = context.mkAnd(this.value, context.mkNot(otherZ3Var.value));
        return new Z3DynamicVar(this.context, minusExpr);
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public boolean isEmpty() {
        if (value.isFalse()) {
            return true;
        }

        Solver solver = context.mkSolver();
        solver.add(value);
        Status status = solver.check();
        return status == Status.UNSATISFIABLE;
    }
    @Override
    public String toString() {
        return "Z3DynamicVar{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Z3DynamicVar that = (Z3DynamicVar) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
