package org.iam.smt.CVC5Solver;

import io.github.cvc5.CVC5ApiException;
import io.github.cvc5.Kind;
import io.github.cvc5.Term;
import io.github.cvc5.TermManager;
import org.iam.config.Parameter;
import org.iam.core.Node;
import org.iam.exceptions.SolverRuntimeException;
import org.iam.grammer.Condition;
import org.iam.grammer.Finding;
import org.iam.grammer.Principal;
import org.iam.smt.SMTConstraintConverter;

import java.util.Map;
import java.util.Set;

public class CVC5FindingsToSMTConverter implements SMTConstraintConverter<Term, CVC5Request, Finding> {
    @Override
    public Term toSMTConstraint(CVC5Request CVC5Request, Finding input) throws SolverRuntimeException {
        try {
            TermManager termManager = CVC5Request.getTermManager();
            Term principalConstraint = null;
            if (input.getPrincipal() != null && !input.getPrincipal().isEmpty()) {
                for (Principal principal : input.getPrincipal()) {
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
            if (input.getAction() != null && !input.getAction().isEmpty()) {
                for (String actionString : input.getAction()) {
                    actionConstraint = actionConstraint == null
                            ? CVC5Encoder.commonEncode(termManager, actionString, CVC5Request.getAction())
                            : termManager.mkTerm(
                            Kind.OR,
                            actionConstraint,
                            CVC5Encoder.commonEncode(termManager, actionString, CVC5Request.getAction()));
                }
            }
            Term resourceConstraint = null;
            if (input.getResource() != null && !input.getResource().isEmpty()) {
                for (String resourceString : input.getResource()) {
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
            if (input.getCondition() != null) {
                for (Condition condition : input.getCondition()) {
                    conditionConstraint = conditionConstraint == null
                            ? CVC5Encoder.conditionEncode(termManager, condition, CVC5Request.getKeyToTerm())
                            : termManager.mkTerm(
                            Kind.AND,
                            conditionConstraint,
                            CVC5Encoder.conditionEncode(termManager, condition, CVC5Request.getKeyToTerm()));
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
            return result == null ? termManager.mkTrue() : result;
        } catch (CVC5ApiException e) {
            throw new SolverRuntimeException(Parameter.SolverType.CVC5, e);
        }
    }

    public Term toReduceFinding(CVC5Request cvc5Request, Finding input,
                                Node<Principal> principalNode,
                                Node<String> actionNode,
                                Node<String> resourceNode,
                                Map<String, Node<String>> conditionNode) {
        try {
            TermManager termManager = cvc5Request.getTermManager();
            Term principalConstraint = null;
            if (input.getPrincipal() != null && !input.getPrincipal().isEmpty()) {
                Principal currentPrincipal = input.getPrincipal().iterator().next();
                Term temporaryConstraint = null;
                for (String value : currentPrincipal.getValues()) {
                    temporaryConstraint = temporaryConstraint == null
                            ? CVC5Encoder.commonEncode(termManager, value, cvc5Request.getPrincipalValue())
                            : termManager.mkTerm(
                            Kind.OR,
                            temporaryConstraint,
                            CVC5Encoder.commonEncode(termManager, value, cvc5Request.getPrincipalValue()));
                }
                principalConstraint = termManager.mkTerm(Kind.AND, temporaryConstraint,
                        CVC5Encoder.commonEncode(termManager, currentPrincipal.getDomainName(), cvc5Request.getPrincipalDomain()));
                for (Principal principal : principalNode.getDominator(currentPrincipal)) {
                    temporaryConstraint = null;
                    for (String value : principal.getValues()) {
                        temporaryConstraint = temporaryConstraint == null
                                ? CVC5Encoder.commonEncode(termManager, value, cvc5Request.getPrincipalValue())
                                : termManager.mkTerm(
                                Kind.OR,
                                temporaryConstraint,
                                CVC5Encoder.commonEncode(termManager, value, cvc5Request.getPrincipalValue()));
                    }
                    if (temporaryConstraint == null) {
                        continue;
                    }
                    principalConstraint = principalConstraint == null
                            ? termManager.mkTerm(Kind.NOT, termManager.mkTerm(Kind.AND, temporaryConstraint,
                            CVC5Encoder.commonEncode(termManager, principal.getDomainName(), cvc5Request.getPrincipalDomain())))
                            : termManager.mkTerm(Kind.AND, principalConstraint,
                            termManager.mkTerm(Kind.NOT, termManager.mkTerm(Kind.AND, temporaryConstraint,
                                    CVC5Encoder.commonEncode(termManager, principal.getDomainName(), cvc5Request.getPrincipalDomain()))));
                }
            }
            Term actionConstraint = null;
            if (input.getAction() != null && !input.getAction().isEmpty()) {
                String currentAction = input.getAction().iterator().next();
                actionConstraint = CVC5Encoder.commonEncode(termManager, currentAction, cvc5Request.getAction());
                for (String action : actionNode.getDominator(currentAction)) {
                    actionConstraint = termManager.mkTerm(Kind.AND, actionConstraint,
                            termManager.mkTerm(Kind.NOT, CVC5Encoder.commonEncode(termManager, action, cvc5Request.getAction())));
                }
            }
            Term resourceConstraint = null;
            if (input.getResource() != null && !input.getResource().isEmpty()) {
                String currentResource = input.getResource().iterator().next();
                resourceConstraint = CVC5Encoder.commonEncode(termManager, currentResource, cvc5Request.getResource());
                for (String resource : resourceNode.getDominator(currentResource)) {
                    resourceConstraint = termManager.mkTerm(Kind.AND, resourceConstraint,
                            termManager.mkTerm(Kind.NOT, CVC5Encoder.commonEncode(termManager, resource, cvc5Request.getResource())));
                }
            }
            Term conditionConstraint = null;
            if (input.getCondition() != null && !input.getCondition().isEmpty()) {
                for (Condition condition : input.getCondition()) {
                    conditionConstraint = conditionConstraint == null
                            ? CVC5Encoder.conditionEncode(termManager, condition, cvc5Request.getKeyToTerm())
                            : termManager.mkTerm(
                            Kind.AND,
                            conditionConstraint,
                            CVC5Encoder.conditionEncode(termManager, condition, cvc5Request.getKeyToTerm()));
                    String currentKey = condition.getKeyToValues().keySet().iterator().next();
                    String currentValue = condition.getKeyToValues().get(currentKey).iterator().next();
                    for (String value : conditionNode.get(currentKey).getDominator(currentValue)) {
                        Condition newCondition = new Condition(condition.getOperator(), Map.of(currentKey, Set.of(value)));
                        conditionConstraint = conditionConstraint == null
                                ? termManager.mkTerm(Kind.NOT, CVC5Encoder.conditionEncode(termManager, newCondition, cvc5Request.getKeyToTerm()))
                                : termManager.mkTerm(
                                Kind.AND,
                                conditionConstraint,
                                termManager.mkTerm(Kind.NOT, CVC5Encoder.conditionEncode(termManager, newCondition, cvc5Request.getKeyToTerm())));
                    }
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
            return result == null ? termManager.mkTrue() : result;
        } catch (CVC5ApiException e) {
            throw new SolverRuntimeException(Parameter.SolverType.CVC5, e);
        }
    }
}
