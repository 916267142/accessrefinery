package org.iam.smt.Z3Solver;

import com.microsoft.z3.*;
import org.iam.smt.Request;
import org.iam.smt.SMTConstraintFactory;

import java.util.HashMap;
import java.util.Map;

public class Z3Request implements Request {
    private final Context context;
    private final Expr<SeqSort<CharSort>> principalDomain;
    private final Expr<SeqSort<CharSort>> principalValue;
    private final Expr<SeqSort<CharSort>> action;
    private final Expr<SeqSort<CharSort>> resource;
    private final Map<String, Expr<?>> keyToExpr;

    public Z3Request(Context ctx) {
        this.context = ctx;
        principalDomain = ctx.mkConst("principalDomain", ctx.mkStringSort());
        principalValue = ctx.mkConst("principalValue", ctx.mkStringSort());
        action = ctx.mkConst("action", ctx.mkStringSort());
        resource = ctx.mkConst("resource", ctx.mkStringSort());
        keyToExpr = new HashMap<>();
    }

    public Z3Request(Z3Request other) {
        context = other.context;
        principalDomain = other.principalDomain;
        principalValue = other.principalValue;
        action = other.action;
        resource = other.resource;
        keyToExpr = other.keyToExpr;
    }

    @Override
    public void addKey(Map<String, SMTConstraintFactory.VarType> keyToType) {
        keyToType.forEach((key, type) -> keyToExpr.put(key,
                switch (type) {
                    case SEQSORT -> context.mkConst(key, context.mkStringSort());
                    case BITVECSORT -> context.mkBVConst(key, 32);
                }
        ));
    }

    public Context getContext() {
        return context;
    }

    public Expr<SeqSort<CharSort>> getPrincipalDomain() {
        return principalDomain;
    }

    public Expr<SeqSort<CharSort>> getPrincipalValue() {
        return principalValue;
    }

    public Expr<SeqSort<CharSort>> getAction() {
        return action;
    }

    public Expr<SeqSort<CharSort>> getResource() {
        return resource;
    }

    public Map<String, Expr<?>> getKeyToExpr() {
        return keyToExpr;
    }
}
