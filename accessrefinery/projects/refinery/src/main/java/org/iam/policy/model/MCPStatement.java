package org.iam.policy.model;

import com.google.common.collect.ImmutableSet;

import org.iam.core.MCPBitVector;
import org.iam.core.MCPFactory;
import org.iam.policy.grammer.Statement;
import org.iam.utils.Parameter;
import org.iam.variables.statics.LabelType;

import java.util.List;

/**
 * MCPStatement extends Statement and provides symbolic encoding for statements.
 * <p>
 * Supports initialization of symbolic domains and computation of the symbolic node
 * for principals, actions, resources, and conditions.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public class MCPStatement extends Statement {
  /**
   * MCPFactory for symbolic encoding (shared by all MCPStatement instances).
   */
  private static MCPFactory _mcpFactory;

  /**
   * Principals associated with this statement.
   */
  private List<MCPPrincipal> mcpPrincipals;

  /**
   * Conditions associated with this statement.
   */
  private List<MCPCondition> mcpConditions;

  /**
   * Symbolic encoding node for this statement.
   */
  private MCPBitVector mcpNode;

  /**
   * Default constructor.
   */
  public MCPStatement() {}

  /**
   * Constructs an MCPStatement from a Statement object.
   * Initializes principals and conditions for symbolic encoding.
   *
   * @param statement the Statement to convert
   */
  public MCPStatement(Statement statement) {
    super(statement);
    if (principal != null) {
      mcpPrincipals = principal.stream()
              .map(MCPPrincipal::new)
              .toList();
    }
    if (condition != null) {
      mcpConditions = condition.stream()
              .map(MCPCondition::new)
              .toList();
    }
  }

  /**
   * Sets the MCPFactory for all MCPStatement instances.
   *
   * @param MCPFactory the MCPFactory to use
   */
  public static void setMCPFactory(MCPFactory MCPFactory) {
    _mcpFactory = MCPFactory;
  }

  /**
   * Initializes the MCPFactory with domain variables for this statement.
   * Adds principals, actions, resources, and conditions to the symbolic domain.
   */
  public void initialMCPFactory() {
    if (mcpPrincipals != null) {
      mcpPrincipals.forEach(MCPPrincipal::initialMCPFactory);
    }

    if (action != null) {
      if(Parameter.isSplitLabel) {
        action.forEach(action -> _mcpFactory.addVar("Action", LabelType.REGEXP, action));
      } else {
        ImmutableSet<String> regexps = ImmutableSet.copyOf(action);
        _mcpFactory.addVar("Action", LabelType.REGEXP_SET, regexps);
      }
    }

    if (resource != null) {
      if(Parameter.isSplitLabel) {
        resource.forEach(resource -> _mcpFactory.addVar("Resource", LabelType.REGEXP, resource));
      } else {
        ImmutableSet<String> regexps = ImmutableSet.copyOf(resource);
        _mcpFactory.addVar("Resource", LabelType.REGEXP_SET, regexps);
      }
    }

    if (mcpConditions != null) {
      mcpConditions.forEach(MCPCondition::initialMCPFactory);
    }
  }

  /**
   * Computes and returns the symbolic encoding node for this statement.
   * Combines principals, actions, resources, and conditions using logical AND.
   *
   * @return the MCPBitVector node for this statement
   */
  public MCPBitVector getMCPNodeCalculation() {
    MCPBitVector res = _mcpFactory.getTrue();

    if (mcpPrincipals != null)
      res.andWith(mcpPrincipals.stream()
              .map(MCPPrincipal::getMCPNodeCalculation)
              .reduce(_mcpFactory.getFalse(), MCPBitVector::or));

    if (action != null)
      if(Parameter.isSplitLabel) {
        res.andWith(action.stream()
                .map(value -> _mcpFactory.getVarFillOtherDomain("Action", value))
                .reduce(_mcpFactory.getFalse(), MCPBitVector::or));
      } else {
        res.andWith(_mcpFactory.getVarFillOtherDomain("Action", ImmutableSet.copyOf(action)));
      }

    if (resource != null)
      if(Parameter.isSplitLabel) {
        res.andWith(resource.stream()
                .map(value -> _mcpFactory.getVarFillOtherDomain("Resource", value))
                .reduce(_mcpFactory.getFalse(), MCPBitVector::or));
      } else {
        res.andWith(_mcpFactory.getVarFillOtherDomain("Resource", ImmutableSet.copyOf(resource)));
      }

    if (mcpConditions != null)
      res.andWith(mcpConditions.stream()
              .map(MCPCondition::getMCPNodeCalculation)
              .reduce(_mcpFactory.getTrue(), MCPBitVector::and));

    mcpNode = res;
    return mcpNode;
  }

  /**
   * Returns the symbolic encoding node for this statement.
   *
   * @return the MCPBitVector node
   */
  public MCPBitVector getMCPNode() {
    return mcpNode;
  }

  /**
   * Returns a string representation of this MCPStatement.
   *
   * @return a string representation
   */
  @Override
  public String toString() {
    return "MCPStatement{" +
            "mcpConditions=" + mcpConditions +
            ", mcpPrincipals=" + mcpPrincipals +
            ", effect=" + effect +
            ", action=" + action +
            ", resource=" + resource +
            '}' + "\n";
  }
}