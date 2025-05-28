package org.iam.smt.Z3Solver;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import org.iam.core.Node;
import org.iam.grammer.Condition;
import org.iam.grammer.Principal;
import org.iam.grammer.Finding;
import org.iam.smt.SMTConstraintConverter;

import java.util.Map;
import java.util.Set;

public class Z3FindingsToSMTConverter implements SMTConstraintConverter<BoolExpr, Z3Request, Finding> {
    @Override
    public BoolExpr toSMTConstraint(Z3Request z3Request, Finding input) {
        Context ctx = z3Request.getContext();
        BoolExpr principalConstraint = null;
        if (input.getPrincipal() != null && !input.getPrincipal().isEmpty()) {
            for (Principal principal : input.getPrincipal()) {
                BoolExpr temporaryConstraint = null;
                for (String value : principal.getValues()) {
                    temporaryConstraint = temporaryConstraint == null ?
                            Z3Encoder.commonEncode(ctx, value, z3Request.getPrincipalValue()) :
                            ctx.mkOr(temporaryConstraint, Z3Encoder.commonEncode(ctx, value, z3Request.getPrincipalValue()));
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
        if (input.getAction() != null && !input.getAction().isEmpty()) {
            for (String actionString : input.getAction()) {
                actionConstraint = actionConstraint == null ?
                        Z3Encoder.commonEncode(ctx, actionString, z3Request.getAction()) :
                        ctx.mkOr(actionConstraint, Z3Encoder.commonEncode(ctx, actionString, z3Request.getAction()));
            }
        }
        BoolExpr resourceConstraint = null;
        if (input.getResource() != null && !input.getResource().isEmpty()) {
            for (String resourceString : input.getResource()) {
                resourceConstraint = resourceConstraint == null ?
                        Z3Encoder.commonEncode(ctx, resourceString, z3Request.getResource()) :
                        ctx.mkOr(resourceConstraint, Z3Encoder.commonEncode(ctx, resourceString, z3Request.getResource()));
            }
        }
        BoolExpr conditionConstraint = null;
        if (input.getCondition() != null && !input.getCondition().isEmpty()) {
            for (Condition condition : input.getCondition()) {
                conditionConstraint = conditionConstraint == null ?
                        Z3Encoder.conditionEncode(ctx, condition, z3Request.getKeyToExpr()) :
                        ctx.mkAnd(conditionConstraint, Z3Encoder.conditionEncode(ctx, condition, z3Request.getKeyToExpr()));
            }
        }

        BoolExpr result = null;
        if (principalConstraint != null) {
            result = result == null ?
                    principalConstraint :
                    ctx.mkAnd(result, principalConstraint);
        }
        if (actionConstraint != null) {
            result = result == null ?
                    actionConstraint :
                    ctx.mkAnd(result, actionConstraint);
        }
        if (resourceConstraint != null) {
            result = result == null ?
                    resourceConstraint :
                    ctx.mkAnd(result, resourceConstraint);
        }
        if (conditionConstraint != null) {
            result = result == null ?
                    conditionConstraint :
                    ctx.mkAnd(result, conditionConstraint);
        }
        return result == null ? ctx.mkTrue() : result;
    }

    public BoolExpr toReduceFinding(Z3Request z3Request, Finding input,
                                    Node<Principal> principalNode,
                                    Node<String> actionNode,
                                    Node<String> resourceNode,
                                    Map<String, Node<String>> conditionNode) {
        Context ctx = z3Request.getContext();
        BoolExpr principalConstraint = null;
        if (input.getPrincipal() != null && !input.getPrincipal().isEmpty()) {
            Principal currentPrincipal = input.getPrincipal().iterator().next();
            BoolExpr temporaryConstraint = null;
            for (String value : currentPrincipal.getValues()) {
                temporaryConstraint = temporaryConstraint == null ?
                        Z3Encoder.commonEncode(ctx, value, z3Request.getPrincipalValue()) :
                        ctx.mkOr(temporaryConstraint, Z3Encoder.commonEncode(ctx, value, z3Request.getPrincipalValue()));
            }
            principalConstraint = ctx.mkAnd(temporaryConstraint,
                    Z3Encoder.commonEncode(ctx, currentPrincipal.getDomainName(), z3Request.getPrincipalDomain()));
            for (Principal principal : principalNode.getDominator(currentPrincipal)) {
                temporaryConstraint = null;
                for (String value : principal.getValues()) {
                    temporaryConstraint = temporaryConstraint == null ?
                            Z3Encoder.commonEncode(ctx, value, z3Request.getPrincipalValue()) :
                            ctx.mkOr(temporaryConstraint, Z3Encoder.commonEncode(ctx, value, z3Request.getPrincipalValue()));
                }
                if (temporaryConstraint == null) {
                    continue;
                }
                principalConstraint = principalConstraint == null
                        ? ctx.mkNot(ctx.mkAnd(temporaryConstraint,
                                Z3Encoder.commonEncode(ctx, principal.getDomainName(), z3Request.getPrincipalDomain())))
                        : ctx.mkAnd(principalConstraint,
                        ctx.mkNot(ctx.mkAnd(temporaryConstraint,
                                Z3Encoder.commonEncode(ctx, principal.getDomainName(), z3Request.getPrincipalDomain()))));
            }
        }
        BoolExpr actionConstraint = null;
        if (input.getAction() != null && !input.getAction().isEmpty()) {
            String currentAction = input.getAction().iterator().next();
            actionConstraint = Z3Encoder.commonEncode(ctx, currentAction, z3Request.getAction());
            for (String action : actionNode.getDominator(currentAction)) {
                actionConstraint = ctx.mkAnd(actionConstraint,
                        ctx.mkNot(Z3Encoder.commonEncode(ctx, action, z3Request.getAction())));
            }
        }
        BoolExpr resourceConstraint = null;
        if (input.getResource() != null && !input.getResource().isEmpty()) {
            String currentResource = input.getResource().iterator().next();
            resourceConstraint = Z3Encoder.commonEncode(ctx, currentResource, z3Request.getResource());
            for (String resource : resourceNode.getDominator(currentResource)) {
                resourceConstraint = ctx.mkAnd(resourceConstraint,
                        ctx.mkNot(Z3Encoder.commonEncode(ctx, resource, z3Request.getResource())));
            }
        }
        BoolExpr conditionConstraint = null;
        if (input.getCondition() != null && !input.getCondition().isEmpty()) {
            for (Condition condition : input.getCondition()) {
                conditionConstraint = conditionConstraint == null ?
                        Z3Encoder.conditionEncode(ctx, condition, z3Request.getKeyToExpr()) :
                        ctx.mkAnd(conditionConstraint, Z3Encoder.conditionEncode(ctx, condition, z3Request.getKeyToExpr()));
                String currentKey = condition.getKeyToValues().keySet().iterator().next();
                String currentValue = condition.getKeyToValues().get(currentKey).iterator().next();
                for (String value : conditionNode.get(currentKey).getDominator(currentValue)) {
                    Condition newCondition = new Condition(condition.getOperator(), Map.of(currentKey, Set.of(value)));
                    conditionConstraint = conditionConstraint == null ?
                            ctx.mkNot(Z3Encoder.conditionEncode(ctx, newCondition, z3Request.getKeyToExpr())) :
                            ctx.mkAnd(conditionConstraint,
                                    ctx.mkNot(Z3Encoder.conditionEncode(ctx, newCondition, z3Request.getKeyToExpr())));
                }
            }
        }
        BoolExpr result = null;
        if (principalConstraint != null) {
            result = result == null ?
                    principalConstraint :
                    ctx.mkAnd(result, principalConstraint);
        }
        if (actionConstraint != null) {
            result = result == null ?
                    actionConstraint :
                    ctx.mkAnd(result, actionConstraint);
        }
        if (resourceConstraint != null) {
            result = result == null ?
                    resourceConstraint :
                    ctx.mkAnd(result, resourceConstraint);
        }
        if (conditionConstraint != null) {
            result = result == null ?
                    conditionConstraint :
                    ctx.mkAnd(result, conditionConstraint);
        }
        return result == null ? ctx.mkTrue() : result;
    }
}
