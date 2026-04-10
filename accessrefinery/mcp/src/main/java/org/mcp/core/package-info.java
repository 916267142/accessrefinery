/**
 * Core abstractions for the MCP symbolic engine.
 *
 * <p>This package defines the foundational model used by MCP to represent and manipulate
 * policy constraints across domains:
 * <ul>
 *   <li>{@link org.mcp.core.MCPLabels}: builds domain-level label sets and equivalence classes (ECs)</li>
 *   <li>{@link org.mcp.core.ECEngine}: computes minimal disjoint EC partitions from labels</li>
 *   <li>{@link org.mcp.core.MCPFactory}: creates backend-aware symbolic values (BDD or SAT)</li>
 *   <li>{@link org.mcp.core.MCPBitVector}: unified logical operations over backend values</li>
 *   <li>{@link org.mcp.core.MCPOperableLabel}: operable label wrapper used by dynamic label APIs</li>
 * </ul>
 *
 * <p>The package provides a backend-independent symbolic layer for intersection, union,
 * difference, satisfiability checks, and domain reasoning.
 */
package org.mcp.core;
