package org.iam.smt.Z3Solver;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import org.iam.grammer.Condition;
import org.iam.grammer.Policy;
import org.iam.grammer.Principal;
import org.iam.grammer.Statement;
import org.iam.smt.SMTConstraintConverter;

public class Z3PolicyToSMTConverter implements SMTConstraintConverter<BoolExpr, Z3Request, Policy> {
    @Override
    public BoolExpr toSMTConstraint(Z3Request z3Request, Policy input) {
        Context ctx = z3Request.getContext();
        BoolExpr allowConstraint = null;
        BoolExpr denyConstraint = null;
        for (Statement statement : input.getStatement()) {
            BoolExpr principalConstraint = null;
            if (statement.getPrincipal() != null && !statement.getPrincipal().isEmpty()) {
                for (Principal principal : statement.getPrincipal()) {
                    BoolExpr temporaryConstraint = null;
                    for (String value : principal.getValues()) {
                        temporaryConstraint = temporaryConstraint == null
                                ? Z3Encoder.commonEncode(ctx, value, z3Request.getPrincipalValue())
                                : ctx.mkOr(temporaryConstraint, Z3Encoder.commonEncode(ctx, value, z3Request.getPrincipalValue()));
                    }
                    if (temporaryConstraint == null) {
                        continue;
                    }
                    if (principalConstraint == null) {
                        principalConstraint = ctx.mkAnd(temporaryConstraint,
                                Z3Encoder.commonEncode(ctx, principal.getDomainName(), z3Request.getPrincipalDomain()));
                    } else {
                        principalConstraint = ctx.mkOr(principalConstraint,
                                ctx.mkAnd(temporaryConstraint,
                                        Z3Encoder.commonEncode(ctx, principal.getDomainName(), z3Request.getPrincipalDomain())));
                    }
                }
            }
            BoolExpr actionConstraint = null;
            if (statement.getAction() != null && !statement.getAction().isEmpty()) {
                for (String actionString : statement.getAction()) {
                    actionConstraint = actionConstraint == null
                            ? Z3Encoder.commonEncode(ctx, actionString, z3Request.getAction())
                            : ctx.mkOr(actionConstraint, Z3Encoder.commonEncode(ctx, actionString, z3Request.getAction()));
                }
            }
            BoolExpr resourceConstraint = null;
            if (statement.getResource() != null && !statement.getResource().isEmpty()) {
                for (String resourceString : statement.getResource()) {
                    resourceConstraint = resourceConstraint == null
                            ? Z3Encoder.commonEncode(ctx, resourceString, z3Request.getResource())
                            : ctx.mkOr(resourceConstraint, Z3Encoder.commonEncode(ctx, resourceString, z3Request.getResource()));
                }
            }
            BoolExpr conditionConstraint = null;
            if (statement.getCondition() != null) {
                for (Condition condition : statement.getCondition()) {
                    conditionConstraint = conditionConstraint == null
                            ? Z3Encoder.conditionEncode(ctx, condition, z3Request.getKeyToExpr())
                            : ctx.mkAnd(conditionConstraint, Z3Encoder.conditionEncode(ctx, condition, z3Request.getKeyToExpr()));
                }
            }
            BoolExpr result = null;
            if (principalConstraint != null) {
                result = result == null
                        ? principalConstraint
                        : ctx.mkAnd(result, principalConstraint);
            }
            if (actionConstraint != null) {
                result = result == null
                        ? actionConstraint
                        : ctx.mkAnd(result, actionConstraint);
            }
            if (resourceConstraint != null) {
                result = result == null
                        ? resourceConstraint
                        : ctx.mkAnd(result, resourceConstraint);
            }
            if (conditionConstraint != null) {
                result = result == null
                        ? conditionConstraint
                        : ctx.mkAnd(result, conditionConstraint);
            }
            if (result == null) {
                continue;
            }
            if (statement.getEffect().getValue().equals("ALLOW")) {
                allowConstraint = allowConstraint == null
                        ? result
                        : ctx.mkOr(allowConstraint, result);
            } else {
                denyConstraint = denyConstraint == null
                        ? result
                        : ctx.mkOr(denyConstraint, result);
            }
        }
        BoolExpr resultConstraint = null;
        if (allowConstraint != null) {
            resultConstraint = resultConstraint == null
                    ? allowConstraint
                    : ctx.mkAnd(resultConstraint, allowConstraint);
        }
        if (denyConstraint != null) {
            resultConstraint = resultConstraint == null
                    ? ctx.mkNot(denyConstraint)
                    : ctx.mkAnd(resultConstraint, ctx.mkNot(denyConstraint));
        }
        return resultConstraint == null
                ? ctx.mkTrue()
                : resultConstraint;
    }
}
