package org.iam.smt;

import org.iam.config.Parameter;
import org.iam.core.Node;
import org.iam.exceptions.SolverRuntimeException;
import org.iam.grammer.*;
import org.iam.smt.CVC5Solver.CVC5FindingsToSMTConverter;
import org.iam.smt.CVC5Solver.CVC5PolicyToSMTConverter;
import org.iam.smt.CVC5Solver.CVC5Request;
import org.iam.smt.Z3Solver.Z3FindingsToSMTConverter;
import org.iam.smt.Z3Solver.Z3PolicyToSMTConverter;
import org.iam.smt.Z3Solver.Z3Request;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SMTConstraintFactory {
    private static final Z3PolicyToSMTConverter Z3PolicyConverter = new Z3PolicyToSMTConverter();
    private static final Z3FindingsToSMTConverter Z3FindingsConverter = new Z3FindingsToSMTConverter();
    private static final CVC5PolicyToSMTConverter CVC5PolicyConverter = new CVC5PolicyToSMTConverter();
    private static final CVC5FindingsToSMTConverter CVC5FindingsConverter = new CVC5FindingsToSMTConverter();

    /**
     * Converts the given input object (either `Policy` or `Findings`) into an SMT constraints.
     *
     * @param request The `Request` object containing the context and variables for SMT constraints.
     * @param input   The object to be converted. Must be an instance of `Policy` or `Findings`.
     * @return An SMT constraint representing the input object.
     * @throws IllegalArgumentException If the object is not an instance of `Policy` or `Findings`.
     */
    public static Object convertToSMT(Request request, Object input) throws SolverRuntimeException {
        if (input instanceof Policy) {
            return switch (Parameter.getActiveSolver()) {
                case Z3 -> Z3PolicyConverter.toSMTConstraint((Z3Request) request, (Policy) input);
                case CVC5 -> CVC5PolicyConverter.toSMTConstraint((CVC5Request) request, (Policy) input);
            };
        } else if (input instanceof Finding) {
            return switch (Parameter.getActiveSolver()) {
                case Z3 -> Z3FindingsConverter.toSMTConstraint((Z3Request) request, (Finding) input);
                case CVC5 -> CVC5FindingsConverter.toSMTConstraint((CVC5Request) request, (Finding) input);
            };
        } else {
            throw new IllegalArgumentException("Unsupported input type: " + input.getClass());
        }
    }

    public static Object convertToReduceFinding(Request request, Finding finding,
                                                Node<Principal> principalNode,
                                                Node<String> actionNode,
                                                Node<String> resourceNode,
                                                Map<String, Node<String>> conditionNode) throws SolverRuntimeException {
        return switch (Parameter.getActiveSolver()) {
            case Z3 -> Z3FindingsConverter.toReduceFinding((Z3Request) request, finding, principalNode, actionNode, resourceNode, conditionNode);
            case CVC5 -> CVC5FindingsConverter.toReduceFinding((CVC5Request) request, finding, principalNode, actionNode, resourceNode, conditionNode);
        };
    }

    /**
     * Extracts a set of keys from the given input object (either `Policy` or `Findings`).
     *
     * @param input The input object from which to extract keys. Must be an instance of `Policy` or `Findings`.
     * @return A set of keys extracted from the input object.
     * @throws IllegalArgumentException If the input object is not an instance of `Policy` or `Findings`.
     */
    public static HashMap<String, VarType> initializeKeyToType(Object input) {
        if (input instanceof Policy) {
            return getKeyToType((Policy) input);
        } else if (input instanceof Finding) {
            return getKeyToType((Finding) input);
        } else {
            throw new IllegalArgumentException("Unsupported input type: " + input.getClass());
        }
    }

    public static HashMap<String, VarType> getKeyToType(Policy input) {
        HashMap<String, VarType> keyToType = new HashMap<>();
        if (input == null || input.getStatement() == null) {
            return keyToType;
        }
        for (Statement statement : input.getStatement()) {
            if (statement == null || statement.getCondition() == null) {
                continue;
            }
            for (Condition condition : statement.getCondition()) {
                if (condition == null || condition.getKeyToValues() == null) {
                    continue;
                }
                for (Map.Entry<String, Set<String>> entry : condition.getKeyToValues().entrySet()) {
                    if (entry.getKey() != null) {
                        keyToType.put(entry.getKey(),
                                switch (condition.getOperator()) {
                                    case STRING_EQUALS,
                                         STRING_NOT_EQUALS,
                                         STRING_MATCH,
                                         STRING_NOT_MATCH,
                                         STRING_LIKE,
                                         STRING_NOT_LIKE,
                                         STRING_EQUALS_IGNORE_CASE,
                                         STRING_NOT_EQUALS_IGNORE_CASE,
                                         STRING_MATCH_IF_EXISTS,
                                         STRING_NOT_MATCH_IF_EXISTS,
                                         STRING_EQUALS_IF_EXISTS,
                                         STRING_NOT_EQUALS_IF_EXISTS,
                                         FOR_ALL_VALUES_STRING_EQUALS,
                                         FOR_ALL_VALUES_STRING_NOT_EQUALS,
                                         FOR_ALL_VALUES_STRING_MATCH,
                                         FOR_ALL_VALUES_STRING_NOT_MATCH,
                                         FOR_ALL_VALUES_STRING_EQUALS_IGNORE_CASE,
                                         FOR_ALL_VALUES_STRING_NOT_EQUALS_IGNORE_CASE,
                                         FOR_ANY_VALUE_STRING_EQUALS,
                                         FOR_ANY_VALUE_STRING_NOT_EQUALS,
                                         FOR_ANY_VALUE_STRING_MATCH,
                                         FOR_ANY_VALUE_STRING_NOT_MATCH,
                                         FOR_ANY_VALUE_STRING_EQUALS_IGNORE_CASE,
                                         FOR_ANY_VALUE_STRING_NOT_EQUALS_IGNORE_CASE,
                                         ARN_LIKE,
                                         ARN_NOT_LIKE -> VarType.SEQSORT;
                                    case IP_ADDRESS,
                                         NOT_IP_ADDRESS,
                                         IP_ADDRESS_IF_EXISTS,
                                         NOT_IP_ADDRESS_IF_EXISTS -> VarType.BITVECSORT;
                                    default -> throw new UnsupportedOperationException(
                                            "Unsupported operator type: " + condition.getOperator()
                                    );
                                }
                        );
                    }
                }
            }
        }
        return keyToType;
    }

    public static HashMap<String, VarType> getKeyToType(Finding input) {
        HashMap<String, VarType> keyToType = new HashMap<>();
        if (input == null || input.getCondition() == null) {
            return keyToType;
        }
        for (Condition condition : input.getCondition()) {
            if (condition == null || condition.getKeyToValues() == null) {
                continue;
            }
            for (Map.Entry<String, Set<String>> entry : condition.getKeyToValues().entrySet()) {
                if (entry.getKey() != null) {
                    keyToType.put(entry.getKey(),
                            switch (condition.getOperator()) {
                                case STRING_LIKE,
                                     STRING_NOT_LIKE,
                                     STRING_EQUALS,
                                     STRING_MATCH,
                                     STRING_NOT_EQUALS,
                                     STRING_NOT_MATCH,
                                     STRING_EQUALS_IF_EXISTS,
                                     STRING_NOT_EQUALS_IF_EXISTS,
                                     STRING_MATCH_IF_EXISTS,
                                     STRING_NOT_MATCH_IF_EXISTS,
                                     STRING_EQUALS_IGNORE_CASE_IF_EXISTS,
                                     STRING_NOT_EQUALS_IGNORE_CASE_IF_EXISTS,
                                     IP_ADDRESS_IF_EXISTS,
                                     NOT_IP_ADDRESS_IF_EXISTS,
                                     FOR_ALL_VALUES_STRING_EQUALS,
                                     FOR_ALL_VALUES_STRING_NOT_EQUALS,
                                     FOR_ALL_VALUES_STRING_MATCH,
                                     FOR_ALL_VALUES_STRING_NOT_MATCH,
                                     FOR_ALL_VALUES_STRING_EQUALS_IGNORE_CASE,
                                     FOR_ALL_VALUES_STRING_NOT_EQUALS_IGNORE_CASE,
                                     FOR_ANY_VALUE_STRING_EQUALS,
                                     FOR_ANY_VALUE_STRING_NOT_EQUALS,
                                     FOR_ANY_VALUE_STRING_MATCH,
                                     FOR_ANY_VALUE_STRING_NOT_MATCH,
                                     FOR_ANY_VALUE_STRING_EQUALS_IGNORE_CASE,
                                     FOR_ANY_VALUE_STRING_NOT_EQUALS_IGNORE_CASE,
                                     ARN_LIKE,
                                     ARN_NOT_LIKE,
                                     STRING_EQUALS_IGNORE_CASE,
                                     STRING_NOT_EQUALS_IGNORE_CASE  -> VarType.SEQSORT;
                                case IP_ADDRESS,
                                     NOT_IP_ADDRESS -> VarType.BITVECSORT;
                            }
                    );
                }
            }
        }
        return keyToType;
    }

    public enum VarType{
        SEQSORT, BITVECSORT
    }
}
