package org.iam.model;

import java.util.HashMap;
import java.util.HashSet;

/**
 * LabelTree represents a tree structure where each node has a set of children.
 * <p>
 * Provides methods to find the maximal children of each node, where a maximal child
 * is not contained by any other child of the same parent.
 * </p>
 *
 * @author
 * @since 2025-02-28
 * @param <T> the type of the nodes in the label tree
 */
public class LabelTree<T> {
    /**
     * Maps each node to its set of children.
     */
    HashMap<T, HashSet<T>> nodeToChildren;

    /**
     * Constructs a LabelTree with the given mapping of nodes to their children.
     *
     * @param nodeToChildren map from node to its children
     */
    public LabelTree(HashMap<T, HashSet<T>> nodeToChildren) {
        this.nodeToChildren = nodeToChildren;
    }

    /**
     * Returns a map from each node to its set of maximal children.
     *
     * @return map from node to set of maximal children
     */
    public HashMap<T, HashSet<T>> getNodeToMaximumChildren() {
        HashMap<T, HashSet<T>> nodeToMaximumChildren = new HashMap<>();
        for (T entry : nodeToChildren.keySet()) {
            nodeToMaximumChildren.put(entry, getMaximumChildrens(entry));
        }
        return nodeToMaximumChildren;
    }

    /**
     * Finds the set of maximal children for a given node.
     * A child is maximal if it is not contained by any other child of the same parent.
     *
     * @param nodeName the node for which to find maximal children
     * @return set of maximal children
     */
    public HashSet<T> getMaximumChildrens(T nodeName) {
        // Get the set of children of the current node
        HashSet<T> children = nodeToChildren.get(nodeName);
        // Initialize a set to store the maximal children of the current node
        HashSet<T> maximalChildren = new HashSet<>();

        // Iterate through each child of the current node
        for (T child : children) {
            if (this.equals(nodeName, child)) {
                continue;
            }
            // Assume the current child is maximal initially
            boolean isMaximal = true;
            // Check if there is any other child that contains the current child
            for (T otherChild : children) {
                if (this.equals(child, otherChild) || this.equals(nodeName, otherChild)) {
                    continue;
                }
                if (this.contains(otherChild, child)) {
                    isMaximal = false;
                    break;
                }
            }
            if (isMaximal) {
                maximalChildren.add(child);
            }
        }
        return maximalChildren;
    }

    /**
     * Checks if node {@code a} contains node {@code b}.
     *
     * @param a the parent node
     * @param b the child node
     * @return true if {@code a} contains {@code b}, false otherwise
     */
    private boolean contains(T a, T b) {
        return nodeToChildren.get(a).contains(b);
    }

    /**
     * Checks if two nodes are considered equal in the context of the tree.
     * Two nodes are equal if each contains the other in their set of children.
     *
     * @param a the first node
     * @param b the second node
     * @return true if equal, false otherwise
     */
    private boolean equals(T a, T b) {
        return nodeToChildren.get(a).contains(b) && nodeToChildren.get(b).contains(a);
    }
}