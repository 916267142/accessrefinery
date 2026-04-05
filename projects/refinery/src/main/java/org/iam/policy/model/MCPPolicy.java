package org.iam.policy.model;

import org.iam.core.MCPBitVector;
import org.iam.core.MCPFactory;
import org.iam.policy.grammer.Policy;
import org.iam.policy.grammer.Statement;
import org.iam.model.MCPVar;

import java.util.HashSet;

/**
 * MCPPolicy extends Policy and implements MCPVar for symbolic encoding.
 * <p>
 * Represents a policy as a symbolic MCPBitVector, supporting allow/deny statements,
 * symbolic initialization, and computation of the policy's symbolic node.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public class MCPPolicy extends Policy implements MCPVar {
    /**
     * MCPFactory for symbolic encoding (shared by all MCPPolicy instances).
     */
    private static MCPFactory _mcpFactory = null;

    /**
     * Symbolic encoding node for this policy.
     */
    private MCPBitVector mcpNode;

    /**
     * Set of allow statements in this policy.
     */
    HashSet<MCPStatement> allowMCPStatements = new HashSet<>();

    /**
     * Set of deny statements in this policy.
     */
    HashSet<MCPStatement> denyMCPStatements = new HashSet<>();

    /**
     * Constructs an MCPPolicy from a Policy object.
     * Separates allow and deny statements, sets up symbolic factories, and computes the symbolic node.
     *
     * @param policy the Policy to convert
     * @throws AssertionError if the MCPFactory has not been set
     */
    public MCPPolicy(Policy policy) {
        super(policy);
        assert _mcpFactory != null;

        statement.forEach(statement -> {
            if (statement.getEffect().equals(Statement.VarEffect.Allow)) {
                allowMCPStatements.add(new MCPStatement(statement));
            }
            if (statement.getEffect().equals(Statement.VarEffect.Deny)) {
                denyMCPStatements.add(new MCPStatement(statement));
            }
        });
        MCPPrincipal.setMCPFactory(_mcpFactory);
        MCPStatement.setMCPFactory(_mcpFactory);
        MCPCondition.setMCPFactory(_mcpFactory);
        this.initialMCPFactory();
        this.getMCPNodeCalculation();
    }

    /**
     * Sets the MCPFactory for all MCPPolicy instances.
     *
     * @param mcpFactory the MCPFactory to use
     */
    public static void setMCPFactory(MCPFactory mcpFactory) {
        _mcpFactory = mcpFactory;
    }

    /**
     * Returns the MCPFactory used by all MCPPolicy instances.
     *
     * @return the MCPFactory
     */
    public static MCPFactory getMCPFactory() {
        return _mcpFactory;
    }

    /**
     * Returns the symbolic encoding node for this policy.
     *
     * @return the MCPBitVector node
     */
    public MCPBitVector getMCPNode() {
        return mcpNode;
    }

    /**
     * Initializes the MCPFactory with domain variables from allow and deny statements.
     */
    @Override
    public void initialMCPFactory() {
        allowMCPStatements.forEach(MCPStatement::initialMCPFactory);
        denyMCPStatements.forEach(MCPStatement::initialMCPFactory);
        _mcpFactory.updates();
    }

    /**
     * Computes and returns the symbolic encoding node for this policy.
     * The result is the union of allow statements minus the union of deny statements.
     *
     * @return the MCPBitVector node for this policy
     * @throws AssertionError if the MCPFactory has not been set
     */
    @Override
    public MCPBitVector getMCPNodeCalculation() {
        assert _mcpFactory != null;
        MCPBitVector allow = allowMCPStatements.stream()
                .map(MCPStatement::getMCPNodeCalculation)
                .reduce(_mcpFactory.getFalse(), MCPBitVector::or);

        MCPBitVector deny = denyMCPStatements.stream()
                .map(MCPStatement::getMCPNodeCalculation)
                .reduce(_mcpFactory.getFalse(), MCPBitVector::or);
        this.mcpNode = allow.diff(deny);

        return mcpNode;
    }

    /**
     * Returns a string representation of this MCPPolicy.
     *
     * @return a string representation
     */
    @Override
    public String toString() {
        return "MCPPolicy{" +
                "denyMCPStatements=" + denyMCPStatements +
                ", allowMCPStatements=" + allowMCPStatements +
                '}' + "\n";
    }
}