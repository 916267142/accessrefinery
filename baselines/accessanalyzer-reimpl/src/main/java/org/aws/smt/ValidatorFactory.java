package org.aws.smt;

import org.aws.config.Parameter;
import org.aws.core.Node;
import org.aws.grammar.Finding;
import org.aws.grammar.Policy;
import org.aws.grammar.Principal;
import org.aws.smt.CVC5Solver.CVC5Validator;
import org.aws.smt.Z3Solver.Z3Validator;

import java.util.Map;

public class ValidatorFactory {
    private final Z3Validator z3Validator = new Z3Validator();
    private final CVC5Validator cvc5Validator = new CVC5Validator();

    public boolean checkImplication(Object objectP, Object objectQ) {
        return switch (Parameter.getActiveSolver()) {
            case Z3 -> z3Validator.checkImplication(objectP, objectQ);
            case CVC5 -> cvc5Validator.checkImplication(objectP, objectQ);
        };
    }

    public boolean checkIntersection(Object objectP, Object objectQ) {
        return switch (Parameter.getActiveSolver()) {
            case Z3 -> z3Validator.checkIntersection(objectP, objectQ);
            case CVC5 -> cvc5Validator.checkIntersection(objectP, objectQ);
        };
    }

    public boolean checkIntersectionOfReduceFinding(Policy policy, Finding finding,
                                                   Node<Principal> principalNode,
                                                   Node<String> actionNode,
                                                   Node<String> resourceNode,
                                                   Map<String, Node<String>> conditionNode) {
        return switch (Parameter.getActiveSolver()) {
            case Z3 -> z3Validator.checkIntersectionOfReduceFinding(policy, finding,
                    principalNode, actionNode, resourceNode, conditionNode);
            case CVC5 -> cvc5Validator.checkIntersectionOfReduceFinding(policy, finding,
                    principalNode, actionNode, resourceNode, conditionNode);
        };
    }
}
