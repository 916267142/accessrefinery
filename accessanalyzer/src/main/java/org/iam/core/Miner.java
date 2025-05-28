package org.iam.core;

import com.microsoft.z3.Context;
import io.github.cvc5.TermManager;
import org.iam.config.Parameter;
import org.iam.grammer.Condition;
import org.iam.grammer.Finding;
import org.iam.grammer.Policy;
import org.iam.smt.CVC5Solver.CVC5Request;
import org.iam.smt.Request;
import org.iam.smt.SMTConstraintFactory;
import org.iam.smt.Z3Solver.Z3Request;
import org.iam.utils.Pair;
import org.iam.utils.TimeMeasure;
import org.iam.variables.statics.FindingOrPolicyStaticVar;
import org.iam.variables.statics.StaticVar;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Miner {
    abstract public Set<Finding> mineIntent(Policy policy, TimeMeasure timeMeasure);

    private Context context;
    private TermManager termManager;

    public Set<Finding> reduceIntent(Policy policy, Set<Finding> findings) {
        try {
            switch (Parameter.getActiveSolver()) {
                case Z3 -> context = new Context();
                case CVC5 -> termManager = new TermManager();
            }
            Request request = switch (Parameter.getActiveSolver()) {
                case Z3 -> new Z3Request(context);
                case CVC5 -> new CVC5Request(termManager);
            };
            if (findings == null || findings.isEmpty()) {
                return findings;
            }

            Map<String, SMTConstraintFactory.VarType> map = SMTConstraintFactory.initializeKeyToType(policy);
            request.addKey(map);

            // Create variables for policy and findings
            Set<StaticVar> smtVars = findings.stream()
                    .map(f -> new FindingOrPolicyStaticVar.Builder().setFinding(f).build())
                    .collect(Collectors.toSet());
            smtVars.add(new FindingOrPolicyStaticVar.Builder().setPolicy(policy).build());

            // Create atomic predicates
            StaticVar logicTrue = switch (Parameter.getActiveSolver()) {
                case Z3 -> new FindingOrPolicyStaticVar.Builder()
                        .setZ3Expr(context.mkTrue())
                        .build();
                case CVC5 -> new FindingOrPolicyStaticVar.Builder()
                        .setCVC5Term(termManager.mkTrue())
                        .build();
            };
            VarAtomicPredicates varAtomicPredicates = new VarAtomicPredicates(smtVars, logicTrue, request);
            Parameter.LOGGER.info("[4/5]  finish atomic predicates calculation");

            // Partition atomic predicates into findings and policy
            Map<Object, Set<Integer>> findingsVarToAPs = new HashMap<>();
            Set<Integer> policyAPs = null;

            for (var entry : varAtomicPredicates.getAtomicPredicates().entrySet()) {
                FindingOrPolicyStaticVar var = (FindingOrPolicyStaticVar) entry.getKey();
                if (var.getVarType() == FindingOrPolicyStaticVar.VarType.FINDING) {
                    findingsVarToAPs.put(var, entry.getValue());
                } else if (var.getVarType() == FindingOrPolicyStaticVar.VarType.POLICY) {
                    policyAPs = entry.getValue();
                }
            }

            // Solve the set cover problem and return the selected findings
            return SetCoverSolver.solve(findingsVarToAPs, policyAPs).keySet().stream()
                    .map(k -> (Finding) (((FindingOrPolicyStaticVar) k).getValue()))
                    .collect(Collectors.toCollection(HashSet::new));
        } catch (Exception e) {
            throw new RuntimeException("Error during reduction: " + e.getMessage(), e);
        }
    }

    protected Map<String, Pair<VarType, Set<String>>> mergeMap(
            Set<Map<String, Pair<VarType, Set<String>>>> maps
    ) {
        Map<String, Pair<VarType, Set<String>>> mergedMap = new HashMap<>();
        for (Map<String, Pair<VarType, Set<String>>> originalMap : maps) {
            for (Map.Entry<String, Pair<VarType, Set<String>>> entry : originalMap.entrySet()) {
                String key = entry.getKey();
                Pair<VarType, Set<String>> pair = entry.getValue();
                Set<String> copiedValues = new HashSet<>(pair.getSecond());
                VarType originalType = pair.getFirst();

                if (mergedMap.containsKey(key)) {
                    Pair<VarType, Set<String>> existingPair = mergedMap.get(key);
                    VarType existingType = existingPair.getFirst();
                    Set<String> existingValues = existingPair.getSecond();

                    if (originalType != existingType) {
                        throw new IllegalArgumentException("Inconsistent types for key: " + key);
                    }

                    existingValues.addAll(copiedValues.stream()
                            .filter(value -> !existingValues.contains(value))
                            .collect(Collectors.toSet()));
                } else {
                    mergedMap.put(key, new Pair<>(originalType, copiedValues));
                }
            }
        }
        return mergedMap;
    }

    protected enum VarType {
        STRING(Condition.VarOperator.STRING_EQUALS),
        BITVEC(Condition.VarOperator.IP_ADDRESS);

        private final Condition.VarOperator defaultOperator;
        private static final Map<Condition.VarOperator, VarType> OPERATOR_MAP = new HashMap<>();

        static {
            OPERATOR_MAP.put(Condition.VarOperator.STRING_EQUALS, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.STRING_NOT_EQUALS, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.STRING_MATCH, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.STRING_NOT_MATCH, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.STRING_LIKE, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.STRING_NOT_LIKE, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.STRING_EQUALS_IGNORE_CASE, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.STRING_NOT_EQUALS_IGNORE_CASE, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.STRING_MATCH_IF_EXISTS, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.STRING_NOT_MATCH_IF_EXISTS, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.STRING_EQUALS_IF_EXISTS, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.STRING_NOT_EQUALS_IF_EXISTS, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.FOR_ALL_VALUES_STRING_EQUALS, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.FOR_ALL_VALUES_STRING_NOT_EQUALS, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.FOR_ALL_VALUES_STRING_MATCH, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.FOR_ALL_VALUES_STRING_NOT_MATCH, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.FOR_ALL_VALUES_STRING_EQUALS_IGNORE_CASE, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.FOR_ALL_VALUES_STRING_NOT_EQUALS_IGNORE_CASE, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.FOR_ANY_VALUE_STRING_EQUALS, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.FOR_ANY_VALUE_STRING_NOT_EQUALS, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.FOR_ANY_VALUE_STRING_MATCH, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.FOR_ANY_VALUE_STRING_NOT_MATCH, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.FOR_ANY_VALUE_STRING_EQUALS_IGNORE_CASE, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.FOR_ANY_VALUE_STRING_NOT_EQUALS_IGNORE_CASE, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.ARN_LIKE, STRING);
            OPERATOR_MAP.put(Condition.VarOperator.ARN_NOT_LIKE, STRING);

            OPERATOR_MAP.put(Condition.VarOperator.IP_ADDRESS, BITVEC);
            OPERATOR_MAP.put(Condition.VarOperator.NOT_IP_ADDRESS, BITVEC);
            OPERATOR_MAP.put(Condition.VarOperator.IP_ADDRESS_IF_EXISTS, BITVEC);
            OPERATOR_MAP.put(Condition.VarOperator.NOT_IP_ADDRESS_IF_EXISTS, BITVEC);
        }

        VarType(Condition.VarOperator defaultOperator) {
            this.defaultOperator = defaultOperator;
        }

        public static VarType fromOperator(Condition.VarOperator operator) {
            VarType type = OPERATOR_MAP.get(operator);
            if (type == null) {
                throw new IllegalArgumentException("Can not find the corresponding VarType for operator: " + operator);
            }
            return type;
        }
    }
}