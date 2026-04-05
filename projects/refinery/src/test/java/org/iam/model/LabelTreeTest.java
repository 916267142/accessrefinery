package org.iam.model;

import org.junit.Assert;
import org.junit.Test;
import org.mcp.core.MCPLabels;
import org.mcp.variables.statics.LabelType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LabelTreeTest {
  /*
   * Input : 1 : {2, 3, 4, 5}  2 : {4, 3}  4 : {3}  5 : {3}
   * Output: 1 : {2, 5}        2 : {4}     4 : {3}  5 : {3}
   *      1
   *     / \
   *    2   5
   *    |   |
   *    4   |
   *      \ |
   *        3
   */

  @Test
  public void testLabelTreeWithInteger() {
    // Create a mapping to represent the containment relationships between nodes
    HashMap<Integer, HashSet<Integer>> nodeToChildren = new HashMap<>();

    // Simplified way to add containment relationships
    nodeToChildren.put(1, new HashSet<>(Set.of(2, 3, 4, 5)));
    nodeToChildren.put(2, new HashSet<>(Set.of(4, 3)));
    nodeToChildren.put(3, new HashSet<>(Set.of(3)));
    nodeToChildren.put(4, new HashSet<>(Set.of(3)));
    nodeToChildren.put(5, new HashSet<>(Set.of(3)));

    LabelTree<Integer> labelTree = new LabelTree<>(nodeToChildren);

    // Call the convert method to calculate the maximal children
    HashMap<Integer, HashSet<Integer>> nodeToMaximumChildren = new HashMap<>();
    nodeToMaximumChildren.put(1, new HashSet<>(Set.of(2, 5)));
    nodeToMaximumChildren.put(2, new HashSet<>(Set.of(4)));
    nodeToMaximumChildren.put(3, new HashSet<>(Set.of()));
    nodeToMaximumChildren.put(4, new HashSet<>(Set.of(3)));
    nodeToMaximumChildren.put(5, new HashSet<>(Set.of(3)));

    Assert.assertEquals(labelTree.getNodeToMaximumChildren(), nodeToMaximumChildren);
  }

  @Test
  public void testLabelTreeWithMCPLabels() {
    MCPLabels mcp = new MCPLabels();
    Set.of(".*", "a*", "aa*", "aaa*", "aaaa*", "b*", "bb*", "a*|b*").forEach(value->
            mcp.addVar("Action", LabelType.REGEXP, value)
    );
    mcp.computeLabels();

    LabelTree<Object> labelTree = new LabelTree<>(mcp.getDomainChildrenNodes("Action"));

    HashMap<String, HashSet<String>> nodeToMaximumChildren = new HashMap<>();
    nodeToMaximumChildren.put(".*", new HashSet<>(Set.of("a*|b*")));
    nodeToMaximumChildren.put("a*|b*", new HashSet<>(Set.of("a*", "b*")));
    nodeToMaximumChildren.put("b*", new HashSet<>(Set.of("bb*")));
    nodeToMaximumChildren.put("a*", new HashSet<>(Set.of("aa*")));
    nodeToMaximumChildren.put("aa*", new HashSet<>(Set.of("aaa*")));
    nodeToMaximumChildren.put("aaa*", new HashSet<>(Set.of("aaaa*")));
    nodeToMaximumChildren.put("bb*", new HashSet<>(Set.of()));
    nodeToMaximumChildren.put("aaaa*", new HashSet<>(Set.of()));

    Assert.assertEquals(labelTree.getNodeToMaximumChildren(), nodeToMaximumChildren);
  }
}
