package org.iam.smt.CVC5Solver;

import io.github.cvc5.*;
import org.iam.config.Parameter;
import org.iam.exceptions.SolverRuntimeException;
import org.iam.grammer.Condition;
import org.iam.grammer.Policy;
import org.iam.grammer.Principal;
import org.iam.grammer.Statement;
import org.iam.smt.SMTConstraintConverter;

public class CVC5PolicyToSMTConverter implements SMTConstraintConverter<Term, CVC5Request, Policy> {
    @Override
    public Term toSMTConstraint(CVC5Request CVC5Request, Policy input) throws SolverRuntimeException {
        try {
            TermManager termManager = CVC5Request.getTermManager();
            Term allowConstraint = null;
            Term denyConstraint = null;
            for (Statement statement : input.getStatement()) {
                Term principalConstraint = null;
                if (statement.getPrincipal() != null && !statement.getPrincipal().isEmpty()) {
                    for (Principal principal : statement.getPrincipal()) {
                        Term temporaryConstraint = null;
                        for (String value : principal.getValues()) {
                            temporaryConstraint = temporaryConstraint == null
                                    ? CVC5Encoder.commonEncode(termManager, value, CVC5Request.getPrincipalValue())
                                    : termManager.mkTerm(
                                    Kind.OR,
                                    temporaryConstraint,
                                    CVC5Encoder.commonEncode(termManager, value, CVC5Request.getPrincipalValue()
                                    ));
                        }
                        if (temporaryConstraint == null) {
                            continue;
                        }
                        if (principalConstraint == null) {
                            principalConstraint = termManager.mkTerm(
                                    Kind.AND,
                                    temporaryConstraint,
                                    CVC5Encoder.commonEncode(
                                            termManager,
                                            principal.getDomainName(),
                                            CVC5Request.getPrincipalDomain()
                                    ));
                        } else {
                            principalConstraint = termManager.mkTerm(
                                    Kind.OR,
                                    principalConstraint,
                                    termManager.mkTerm(
                                            Kind.AND,
                                            temporaryConstraint,
                                            CVC5Encoder.commonEncode(
                                                    termManager,
                                                    principal.getDomainName(),
                                                    CVC5Request.getPrincipalDomain()
                                            )));
                        }

                    }
                }
                Term actionConstraint = null;
                if (statement.getAction() != null && !statement.getAction().isEmpty()) {
                    for (String actionString : statement.getAction()) {
                        actionConstraint = actionConstraint == null
                                ? CVC5Encoder.commonEncode(termManager, actionString, CVC5Request.getAction())
                                : termManager.mkTerm(
                                Kind.OR,
                                actionConstraint,
                                CVC5Encoder.commonEncode(termManager, actionString, CVC5Request.getAction())
                        );
                    }
                }
                Term resourceConstraint = null;
                if (statement.getResource() != null && !statement.getResource().isEmpty()) {
                    for (String resourceString : statement.getResource()) {
                        resourceConstraint = resourceConstraint == null
                                ? CVC5Encoder.commonEncode(termManager, resourceString, CVC5Request.getResource())
                                : termManager.mkTerm(
                                Kind.OR,
                                resourceConstraint,
                                CVC5Encoder.commonEncode(termManager, resourceString, CVC5Request.getResource())
                        );
                    }
                }
                Term conditionConstraint = null;
                if (statement.getCondition() != null) {
                    for (Condition condition : statement.getCondition()) {
                        conditionConstraint = conditionConstraint == null
                                ? CVC5Encoder.conditionEncode(termManager, condition, CVC5Request.getKeyToTerm())
                                : termManager.mkTerm(
                                Kind.AND,
                                conditionConstraint,
                                CVC5Encoder.conditionEncode(termManager, condition, CVC5Request.getKeyToTerm())
                        );
                    }
                }
                Term result = null;
                if (principalConstraint != null) {
                    result = result == null
                            ? principalConstraint
                            : termManager.mkTerm(Kind.AND, result, principalConstraint);
                }
                if (actionConstraint != null) {
                    result = result == null
                            ? actionConstraint
                            : termManager.mkTerm(Kind.AND, result, actionConstraint);
                }
                if (resourceConstraint != null) {
                    result = result == null
                            ? resourceConstraint
                            : termManager.mkTerm(Kind.AND, result, resourceConstraint);
                }
                if (conditionConstraint != null) {
                    result = result == null
                            ? conditionConstraint
                            : termManager.mkTerm(Kind.AND, result, conditionConstraint);
                }
                if (result == null) continue;
                if (statement.getEffect().getValue().equals("ALLOW")) {
                    allowConstraint = allowConstraint == null
                            ? result
                            : termManager.mkTerm(Kind.OR, allowConstraint, result);
                } else if (statement.getEffect().getValue().equals("DENY")) {
                    denyConstraint = denyConstraint == null
                            ? result
                            : termManager.mkTerm(Kind.OR, denyConstraint, result);
                }
            }
            Term resultConstraint = null;
            if (allowConstraint != null) {
                resultConstraint = allowConstraint;
            } else {
                resultConstraint = termManager.mkFalse();
            }
            if (denyConstraint != null) {
                resultConstraint = resultConstraint == null
                        ? termManager.mkTerm(Kind.NOT, denyConstraint)
                        : termManager.mkTerm(Kind.AND, resultConstraint, termManager.mkTerm(Kind.NOT, denyConstraint));
            }
            return resultConstraint == null ? termManager.mkTrue() : resultConstraint;
        } catch (CVC5ApiException e) {
            throw new SolverRuntimeException(Parameter.SolverType.CVC5, e);
        }
    }
}
