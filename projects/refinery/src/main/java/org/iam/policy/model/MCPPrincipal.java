package org.iam.policy.model;

import com.google.common.collect.ImmutableSet;

import org.iam.model.MCPVar;
import org.iam.policy.grammer.Principal;
import org.iam.utils.Parameter;
import org.mcp.core.MCPBitVector;
import org.mcp.core.MCPFactory;
import org.mcp.variables.statics.LabelType;

/**
 * MCPPrincipal extends Principal and implements MCPVar for symbolic encoding.
 * <p>
 * Represents a principal entity as a symbolic MCPBitVector, supporting initialization
 * of symbolic domains and computation of the principal's symbolic node.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public class MCPPrincipal extends Principal implements MCPVar {
    /**
     * MCPFactory for symbolic encoding (shared by all MCPPrincipal instances).
     */
    static private MCPFactory _mcpFactory;

    /**
     * Symbolic encoding node for this principal.
     */
    private MCPBitVector mcpNode;

    /**
     * Constructs an MCPPrincipal by copying from another Principal.
     *
     * @param other the Principal to copy
     */
    public MCPPrincipal(Principal other) {
        super(other);
    }

    /**
     * Sets the MCPFactory for all MCPPrincipal instances.
     *
     * @param mcpFactory the MCPFactory to use
     */
    public static void setMCPFactory(MCPFactory mcpFactory) {
        _mcpFactory = mcpFactory;
    }

    /**
     * Returns the MCPFactory used by all MCPPrincipal instances.
     *
     * @param mcpFactory (unused, kept for compatibility)
     * @return the MCPFactory
     */
    public static MCPFactory getMCPFactory(MCPFactory mcpFactory) {
        return _mcpFactory;
    }

    /**
     * Initializes the MCPFactory with domain variables for this principal.
     * Adds each value as a domain variable with the appropriate type.
     */
    @Override
    public void initialMCPFactory() {
        if (domainName == null || values == null) {
            return;
        }

        if(Parameter.isSplitLabel) {
            values.forEach(value -> _mcpFactory.addVar(domainName, LabelType.REGEXP, value));
        } else {
            ImmutableSet<String> regexps = ImmutableSet.copyOf(values);
            _mcpFactory.addVar(domainName, LabelType.REGEXP_SET, regexps);
        }

    }

    /**
     * Computes and returns the symbolic encoding node for this principal.
     * Returns the cached value if already computed.
     *
     * @return the MCPBitVector node for this principal
     */
    @Override
    public MCPBitVector getMCPNodeCalculation() {
        if (domainName == null || values == null) {
            return _mcpFactory.getTrue();
        }
        if(mcpNode != null) {
            return mcpNode;
        }
        if(Parameter.isSplitLabel) {
            mcpNode = values.stream()
                    .map(value -> _mcpFactory.getVarFillOtherDomain(domainName, value))
                    .reduce(_mcpFactory.getFalse(), MCPBitVector::or);
        } else {
            mcpNode = _mcpFactory.getVarFillOtherDomain(domainName, ImmutableSet.copyOf(values));
        }
        return mcpNode;
    }

    /**
     * Returns a string representation of this MCPPrincipal.
     *
     * @return a string representation
     */
    @Override
    public String toString() {
        return "MCPPrincipal{" +
                "domainName='" + domainName + '\'' +
                ", values=" + values +
                '}' + "\n";
    }
}