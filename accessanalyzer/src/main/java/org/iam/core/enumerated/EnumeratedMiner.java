package org.iam.core.enumerated;

import org.iam.grammer.*;
import org.iam.core.Miner;
import org.iam.smt.ValidatorFactory;
import org.iam.utils.Pair;
import org.iam.utils.TimeMeasure;

import java.util.*;
import java.util.stream.Collectors;

public class EnumeratedMiner extends Miner {
    @Override
    public Set<Finding> mineIntent(Policy policy, TimeMeasure timeMeasure) {
        ValidatorFactory validatorFactory = new ValidatorFactory();
        Set<Principal> principals = new HashSet<>();
        Set<String> actions = new HashSet<>();
        Set<String> resources = new HashSet<>();
        Set<Map<String, Pair<VarType, Set<String>>>> keyToPairSet = new HashSet<>();

        // extracts existing principals, actions, resources and conditions from the policy
        boolean flag = false;
        for (Statement statement : policy.getStatement()) {
            Set<Principal> currentPrincipal = statement.getPrincipal() == null ? Set.of() : statement.getPrincipal();
            Set<String> currentAction = statement.getAction() == null ? Set.of() : statement.getAction();
            Set<String> currentResource = statement.getResource() == null ? Set.of() : statement.getResource();
            if (flag) {
                if (principals.isEmpty() != currentPrincipal.isEmpty()) {
                    throw new IllegalArgumentException("Inconsistent existence of principals in the policy");
                }
                if (actions.isEmpty() != currentAction.isEmpty()) {
                    throw new IllegalArgumentException("Inconsistent existence of actions in the policy");
                }
                if (resources.isEmpty() != currentResource.isEmpty()) {
                    throw new IllegalArgumentException("Inconsistent existence of resources in the policy");
                }
            }

            principals.addAll(currentPrincipal == null ? Set.of() : currentPrincipal.stream()
                    .filter(value -> !principals.contains(value))
                    .collect(Collectors.toSet()));
            actions.addAll(currentAction == null ? Set.of() : currentAction.stream()
                    .filter(value -> !actions.contains(value))
                    .collect(Collectors.toSet()));
            resources.addAll(currentResource == null ? Set.of() : currentResource.stream()
                    .filter(value -> !resources.contains(value))
                    .collect(Collectors.toSet()));
            for (Condition condition : statement.getCondition()) {
                Map<String, Pair<VarType, Set<String>>> keyToPair = new HashMap<>();
                for (Map.Entry<String, Set<String>> entry : condition.getKeyToValues().entrySet()) {
                    keyToPair.put(entry.getKey(), new Pair<>(VarType.fromOperator(condition.getOperator()), entry.getValue()));
                }
                keyToPairSet.add(keyToPair);
            }
            if (!flag) {
                flag = true;
            }
        }
        Map<String, Pair<VarType, Set<String>>> allKeyToValues = mergeMap(keyToPairSet);
        for (Map.Entry<String, Pair<VarType, Set<String>>> entry : allKeyToValues.entrySet()) {
            switch (entry.getValue().getFirst()) {
                case STRING -> {
                    if (!entry.getValue().getSecond().contains("*")) {
                        entry.getValue().getSecond().add("*");
                    }
                }
                case BITVEC -> {
                    if (!entry.getValue().getSecond().contains("0.0.0.0/0")) {
                        entry.getValue().getSecond().add("0.0.0.0/0");
                    }
                }
            }
        }
        Set<Set<?>> conditions = allKeyToValues.entrySet().stream()
                .map(entry -> entry.getValue().getSecond().stream()
                        .map(value -> switch (entry.getValue().getFirst()) {
                            case STRING -> new Condition(Condition.VarOperator.STRING_MATCH, Map.of(entry.getKey(), Set.of(value)));
                            case BITVEC -> new Condition(Condition.VarOperator.IP_ADDRESS, Map.of(entry.getKey(), Set.of(value)));
                        })
                        .collect(Collectors.toSet()))
                .collect(Collectors.toSet());

        // generates the Cartesian product of all the fields
        boolean principalEmpty = principals.isEmpty();
        boolean actionEmpty = actions.isEmpty();
        boolean resourceEmpty = resources.isEmpty();
        Set<Set<?>> lists = new HashSet<>(Set.of(principals, actions, resources));
        lists.addAll(conditions);
        Set<Set<?>> combinations = lists.stream()
                .reduce(
                        Set.of(Set.of()),
                        (acc, list) -> {
                            if (list.isEmpty()) {
                                return acc;
                            }
                            return acc.stream()
                            .flatMap(existing -> list.stream()
                                    .map(item -> {
                                        Set<Object> newSet = new HashSet<>(existing);
                                        newSet.add(item);
                                        return newSet;
                                    }))
                            .collect(Collectors.toSet());
                        },
                        (a, b) -> {
                            Set<Set<?>> combined = new HashSet<>(a);
                            combined.addAll(b);
                            return combined;
                        }
                );
        Set<Finding> findings = combinations.stream()
                .map(combination -> {
                    int index = 0;
                    List<Object> combinationList = new ArrayList<>(combination);
                    Set<Principal> principal = principalEmpty ? null
                            : Set.of((Principal) combinationList.get(index));
                    index += principalEmpty ? 0 : 1;
                    Set<String> action = actionEmpty ? null
                            : Set.of((String) combinationList.get(index));
                    index += actionEmpty ? 0 : 1;
                    Set<String> resource = resourceEmpty ? null
                            : Set.of((String) combinationList.get(index));
                    index += resourceEmpty ? 0 : 1;

                    Set<Condition> condition = new HashSet<>();
                    for (; index < combinationList.size(); ++index) {
                        condition.add((Condition) combinationList.get(index));
                    }

                    return new Finding(principal, action, resource, condition);
                })
                .collect(Collectors.toSet());

        // generates the answer Finding list
        Set<Finding> resultFinding = findings.stream()
                .filter(
                        finding -> validatorFactory.checkIntersection(finding, policy)
                )
                .collect(Collectors.toSet());

        // remove duplicate findings
//        return resultFinding.stream()
//                .filter(finding1 -> resultFinding.stream()
//                        .noneMatch(finding2 ->
//                                !finding1.equals(finding2) && validatorFactory.checkImplication(finding1, finding2)))
//                .toSet();

        return resultFinding;
    }
}
