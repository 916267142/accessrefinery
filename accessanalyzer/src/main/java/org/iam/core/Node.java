package org.iam.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * The class serves as the tool to maintain the condition tree relationships.
 *
 * @version 1.1
 */
public class Node<T> {
    // One node's children shouldn't contain itself.
    private Map<T, HashSet<T>> childrenNodes;

    private Map<T, HashSet<T>> dominators;

    public Node() {
    }

    public Node(Map<T, HashSet<T>> childrenNodes) {
        this.childrenNodes = childrenNodes;
        dominators = new HashMap<>();
        preprocessDominators();
    }

    public void initialize(Map<T, HashSet<T>> childrenNodes) {
        this.childrenNodes = childrenNodes;
        dominators = new HashMap<>();
        preprocessDominators();
    }

    /**
     * Preprocess the dominators.
     */
    private void preprocessDominators() {
        for (Map.Entry<T, HashSet<T>> entry : childrenNodes.entrySet()) {
            HashSet<T> dominator = new HashSet<>();
            for (T child : entry.getValue()) {
                boolean flag = false;
                for (T otherChild : entry.getValue()) {
                    if (otherChild.equals(child)) continue;
                    if (childrenNodes.get(otherChild).contains(child)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    dominator.add(child);
                }
            }
            dominators.put(entry.getKey(), dominator);
        }
    }

    public HashSet<T> getChildrenNode(T value) {
        return childrenNodes.get(value);
    }

    public HashSet<T> getDominator(T value) {
        return dominators.get(value);
    }

    @Override
    public String toString() {
        return "Node{" +
                "dominators=" + dominators +
                '}';
    }
}
