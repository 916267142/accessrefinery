package org.iam.core.stratified;

import org.iam.config.Parameter;
import org.iam.core.Miner;
import org.iam.core.Node;
import org.iam.grammer.*;
import org.iam.smt.ValidatorFactory;
import org.iam.utils.Pair;
import org.iam.utils.TimeMeasure;

import java.util.*;
import java.util.stream.Collectors;

public class StratifiedMiner extends Miner {
    private final Map<String, Node<String>> keyToNode = new HashMap<>();
    private final Node<Principal> principalNode = new Node<>();
    private final Node<String> actionNode = new Node<>();
    private final Node<String> resourceNode = new Node<>();
    private boolean principalEmpty;
    private boolean actionEmpty;
    private boolean resourceEmpty;

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

            Set<Principal> finalPrincipals = principals;
            principals.addAll(currentPrincipal == null ? Set.of() : currentPrincipal.stream()
                    .filter(value -> !finalPrincipals.contains(value))
                    .collect(Collectors.toSet()));
            actions.addAll(currentAction == null ? Set.of() : currentAction.stream()
                    .filter(value -> !actions.contains(value))
                    .collect(Collectors.toSet()));
            resources.addAll(currentResource == null ? Set.of() : currentResource.stream()
                    .filter(value -> !resources.contains(value))
                    .collect(Collectors.toSet()));
            Set<Condition> currentCondition = statement.getCondition() == null ? Set.of() : statement.getCondition();
            for (Condition condition : currentCondition) {
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

        // generate Nodes for principals, actions and resources
        principalEmpty = principals.isEmpty();
        actionEmpty = actions.isEmpty();
        resourceEmpty = resources.isEmpty();
        Principal startPrincipal = null;
        if (!principalEmpty) {
            Set<Principal> newPrincipals = new HashSet<>();
            for (Principal principal : principals) {
                for (String value : principal.getValues()) {
                    Principal newPrincipal = new Principal(principal.getDomainName(), Set.of(value));
                    newPrincipals.add(newPrincipal);
                }
            }
            principals = newPrincipals;

            if (principals.size() == 1) {
                startPrincipal = principals.iterator().next();
            } else if (!principals.contains(new Principal("*", Collections.singleton("*")))) {
                startPrincipal = new Principal("*", Collections.singleton("*"));
                principals.add(startPrincipal);
            } else {
                startPrincipal = new Principal("*", Collections.singleton("*"));
            }

            Map<Principal, Finding> principalToFinding = new HashMap<>();
            principalToFinding.putAll(
                    principals.stream()
                            .collect(Collectors.toMap(
                                    principal -> principal,
                                    principal -> new Finding(
                                            Set.of(principal),
                                            null,
                                            null,
                                            null
                                    ),
                                    (oldVal, newVal) -> oldVal
                            )));
            Map<Principal, HashSet<Principal>> principalChildrenNode = new HashMap<>();
            for (Principal principal : principals) {
                HashSet<Principal> children = new HashSet<>();
                for (Principal otherPrincipal : principals) {
                    if (otherPrincipal.equals(principal)) continue;
                    if (validatorFactory.checkImplication(
                            principalToFinding.get(otherPrincipal),
                            principalToFinding.get(principal)
                    )) {
                        children.add(otherPrincipal);
                    }
                }
                principalChildrenNode.put(principal, children);
            }
            principalNode.initialize(principalChildrenNode);
        }

        String startAction = null;
        if (!actionEmpty) {
            if (actions.size() == 1) {
                startAction = actions.iterator().next();
            } else if (!actions.contains("*")) {
                startAction = "*";
                actions.add(startAction);
            } else {
                startAction = "*";
            }

            Map<String, Finding> actionToFinding = new HashMap<>();
            actionToFinding.putAll(
                    actions.stream()
                            .collect(Collectors.toMap(
                                    action -> action,
                                    action -> new Finding(
                                            null,
                                            Set.of(action),
                                            null,
                                            null
                                    ),
                                    (oldVal, newVal) -> oldVal
                            )));
            Map<String, HashSet<String>> actionChildrenNode = new HashMap<>();
            for (String action : actions) {
                HashSet<String> children = new HashSet<>();
                for (String otherAction : actions) {
                    if (otherAction.equals(action)) continue;
                    if (validatorFactory.checkImplication(
                            actionToFinding.get(otherAction),
                            actionToFinding.get(action)
                    )) {
                        children.add(otherAction);
                    }
                }
                actionChildrenNode.put(action, children);
            }
            actionNode.initialize(actionChildrenNode);
        }

        String startResource = null;
        if (!resourceEmpty) {
            if (resources.size() == 1) {
                startResource = resources.iterator().next();
            } else if (!resources.contains("*")) {
                startResource = "*";
                resources.add(startResource);
            } else {
                startResource = "*";
            }

            Map<String, Finding> resourceToFinding = new HashMap<>();
            resourceToFinding.putAll(
                    resources.stream()
                            .collect(Collectors.toMap(
                                    resource -> resource,
                                    resource -> new Finding(
                                            null,
                                            null,
                                            Set.of(resource),
                                            null
                                    ),
                                    (oldVal, newVal) -> oldVal
                            )));
            Map<String, HashSet<String>> resourceChildrenNode = new HashMap<>();
            for (String resource : resources) {
                HashSet<String> children = new HashSet<>();
                for (String otherResource : resources) {
                    if (otherResource.equals(resource)) continue;
                    if (validatorFactory.checkImplication(
                            resourceToFinding.get(otherResource),
                            resourceToFinding.get(resource)
                    )) {
                        children.add(otherResource);
                    }
                }
                resourceChildrenNode.put(resource, children);
            }
            resourceNode.initialize(resourceChildrenNode);
        }

        // generate Nodes for all the keys of conditions
        Set<Condition> startConditions = new HashSet<>();
        for (Map.Entry<String, Pair<VarType, Set<String>>> entry : allKeyToValues.entrySet()) {
            switch (entry.getValue().getFirst()) {
                case STRING -> {
                    entry.getValue().getSecond().add("*");
                    startConditions.add(new Condition(
                            Condition.VarOperator.STRING_MATCH,
                            new HashMap<>(Collections.singletonMap(entry.getKey(),
                                    Collections.singleton("*")))
                    ));
                }
                case BITVEC -> {
                    entry.getValue().getSecond().add("1.0.0.0/0");
                    startConditions.add(new Condition(Condition.VarOperator.IP_ADDRESS,
                            new HashMap<>(Collections.singletonMap(entry.getKey(),
                                    Collections.singleton("1.0.0.0/0")))
                    ));
                }
                default -> throw new UnsupportedOperationException("Unsupported type: " + entry.getValue().getFirst());
            }

            Map<String, Finding> valueToFinding = new HashMap<>();
            valueToFinding.putAll(
                    entry.getValue().getSecond().stream()
                            .collect(Collectors.toMap(
                                    value -> value,
                                    value -> mkTemporaryFinding(entry.getKey(), entry.getValue().getFirst(), value),
                                    (oldVal, newVal) -> oldVal
                            )));

            Map<String, HashSet<String>> childrenNode = new HashMap<>();
            for (String value : entry.getValue().getSecond()) {
                HashSet<String> children = new HashSet<>();
                for (String otherValue : entry.getValue().getSecond()) {
                    if (otherValue.equals(value)) continue;
                    if (validatorFactory.checkImplication(valueToFinding.get(otherValue), valueToFinding.get(value))) {
                        children.add(otherValue);
                    }
                }

                childrenNode.put(value, children);
            }
            keyToNode.put(entry.getKey(), new Node<>(childrenNode));
        }
        Parameter.LOGGER.info("[2/5]  finish label tree calculation");

        Queue<Finding> workList = new ArrayDeque<>();
        workList.add(new Finding(
                principalEmpty ? null : Set.of(startPrincipal),
                actionEmpty ? null : Set.of(startAction),
                resourceEmpty ? null : Set.of(startResource),
                startConditions
        ));
        Policy ansPolicy = new Policy("", new HashSet<>());
        Set<Finding> ansFindings = new HashSet<>();
        Set<Finding> visited = new HashSet<>();

        int index = 0;

        while (!workList.isEmpty()) {
            long startTime = System.nanoTime();

            Finding currentFinding = workList.poll();

            if (visited.contains(currentFinding)) {
                continue;
            }
            visited.add(currentFinding);
            index++;

            if (validatorFactory.checkIntersectionOfReduceFinding(policy, currentFinding,
                    principalNode, actionNode, resourceNode, keyToNode)) {
                if (checkImplication(currentFinding, ansFindings, ansPolicy, validatorFactory)) {
                    continue;
                }
                ansFindings.add(currentFinding);
                ansPolicy.getStatement().add(new Statement(currentFinding));
            } else {
                // add the current finding to the worklist
                if (!principalEmpty) {
                    for (Principal principal : currentFinding.getPrincipal()) {
                        for (Principal dominatedChild : principalNode.getDominator(principal)) {
                            Finding newFinding = new Finding(currentFinding);
                            newFinding.setPrincipal(Set.of(dominatedChild));
                            if (!visited.contains(newFinding) && !workList.contains(newFinding)) {
                                if (!checkImplication(newFinding, ansFindings, ansPolicy, validatorFactory)) {
                                    workList.add(newFinding);
                                }
                            }
                        }
                    }
                }

                if (!actionEmpty) {
                    for (String action : currentFinding.getAction()) {
                        for (String dominatedChild : actionNode.getDominator(action)) {
                            Finding newFinding = new Finding(currentFinding);
                            newFinding.setAction(Set.of(dominatedChild));
                            if (!visited.contains(newFinding) && !workList.contains(newFinding)) {
                                if (!checkImplication(newFinding, ansFindings, ansPolicy, validatorFactory)) {
                                    workList.add(newFinding);
                                }
                            }
                        }
                    }
                }

                if (!resourceEmpty) {
                    for (String resource : currentFinding.getResource()) {
                        for (String dominatedChild : resourceNode.getDominator(resource)) {
                            Finding newFinding = new Finding(currentFinding);
                            newFinding.setResource(Set.of(dominatedChild));
                            if (!visited.contains(newFinding) && !workList.contains(newFinding)) {
                                if (!checkImplication(newFinding, ansFindings, ansPolicy, validatorFactory)) {
                                    workList.add(newFinding);
                                }
                            }
                        }
                    }
                }

                for (Condition condition : currentFinding.getCondition()) {
                    for (Map.Entry<String, Set<String>> entry : condition.getKeyToValues().entrySet()) {
                        for (String value : entry.getValue()) {
                            for (String dominatedChild : keyToNode.get(entry.getKey()).getDominator(value)) {
                                Condition newCondition = new Condition(
                                        condition.getOperator(),
                                        new HashMap<>(Collections.singletonMap(entry.getKey(),
                                                Collections.singleton(dominatedChild)))
                                );
                                Finding newFinding = new Finding(currentFinding);
                                Set<Condition> newConditionSet = new HashSet<>(newFinding.getCondition());
                                newConditionSet.remove(condition);
                                newConditionSet.add(newCondition);
                                newFinding.setCondition(newConditionSet);
                                if (!visited.contains(newFinding) && !workList.contains(newFinding)) {
                                    if (!checkImplication(newFinding, ansFindings, ansPolicy, validatorFactory)) {
                                        workList.add(newFinding);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            long endTime = System.nanoTime();

            timeMeasure.addRound(endTime - startTime);
        }
        return ansFindings;
    }

    private boolean checkImplication(Finding finding, Set<Finding> ansFindings, Policy ansPolicy, ValidatorFactory validatorFactory) {
        if (ansFindings.isEmpty()) {
            return false;
        }
        return internalCheckImplication(finding, ansFindings, validatorFactory);
    }

    private boolean internalCheckImplication(Finding finding, Set<Finding> findingSet, ValidatorFactory validatorFactory) {
        for (Finding currentFinding : findingSet) {
            if (!principalEmpty) {
                if (!finding.getPrincipal().equals(currentFinding.getPrincipal())
                        && !validatorFactory.checkImplication(
                        new Finding(
                                finding.getPrincipal(),
                                null,
                                null,
                                null
                        ),
                        new Finding(
                                currentFinding.getPrincipal(),
                                null,
                                null,
                                null
                        )
                )) {
                    continue;
                }
            }
            if (!actionEmpty) {
                if (!finding.getAction().equals(currentFinding.getAction())
                        && !validatorFactory.checkImplication(
                        new Finding(
                                null,
                                finding.getAction(),
                                null,
                                null
                        ),
                        new Finding(
                                null,
                                currentFinding.getAction(),
                                null,
                                null
                        )
                )) {
                    continue;
                }
            }
            if (!resourceEmpty) {
                if (!finding.getResource().equals(currentFinding.getResource())
                        && !validatorFactory.checkImplication(
                        new Finding(
                                null,
                                null,
                                finding.getResource(),
                                null
                        ),
                        new Finding(
                                null,
                                null,
                                currentFinding.getResource(),
                                null
                        )
                )) {
                    continue;
                }
            }
            boolean flag = false;
            for (Condition condition : currentFinding.getCondition()) {
                if (!finding.getCondition().contains(condition)
                        && !validatorFactory.checkImplication(
                        new Finding(
                                null,
                                null,
                                null,
                                Set.of(finding.getCondition().stream()
                                        .filter(value -> value.getKeyToValues().containsKey(condition.getKeyToValues().keySet().iterator().next()))
                                        .findFirst()
                                        .orElse(null))
                        ),
                        new Finding(
                                null,
                                null,
                                null,
                                Set.of(condition)
                        )
                )) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                continue;
            }
            return true;
        }
        return false;
    }

    /**
     * Makes a policy only contains a key-value pair, which intends to check the implication relationship.
     *
     * @param key   The key of the condition.
     * @param value The value of the condition.
     * @return The temporary Policy.
     */
    private Finding mkTemporaryFinding(String key, VarType type, String value) { // add a parameter type
        Set<String> conditionValues = Collections.singleton(value);
        HashMap<String, Set<String>> condition = new HashMap<>(Collections.singletonMap(key, conditionValues));
        Condition conditions = switch (type) {
            case STRING -> new Condition(Condition.VarOperator.STRING_MATCH, condition);
            case BITVEC -> new Condition(Condition.VarOperator.IP_ADDRESS, condition);
        };
        return new Finding(
                null,
                null,
                null,
                Collections.singleton(conditions));
    }
}