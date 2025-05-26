package org.iam.model;

import org.iam.core.MCPBitVector;

/**
 * Interface for objects that can be represented as symbolic variables using MCPBitVector.
 * <p>
 * Implementing classes should provide methods to initialize the symbolic factory
 * and to compute their symbolic representation.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public interface MCPVar {
    /**
     * Initializes the symbolic factory with the necessary domain variables.
     */
    void initialMCPFactory();

    /**
     * Computes and returns the symbolic representation (MCPBitVector) for this object.
     *
     * @return the MCPBitVector representing this object
     */
    MCPBitVector getMCPNodeCalculation();
}