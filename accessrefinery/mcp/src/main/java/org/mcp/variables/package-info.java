/**
 * Type system for MCP symbolic variables.
 *
 * <p>This package defines the variable model split into two complementary subpackages:
 * <ul>
 *   <li>{@code org.mcp.variables.statics}: static label definitions and factory/type metadata</li>
 *   <li>{@code org.mcp.variables.dynamics}: operable symbolic labels used in set algebra</li>
 * </ul>
 *
 * <p>The static layer describes domain values and conversion semantics, while the dynamic
 * layer provides backend-agnostic symbolic operations such as union/intersection/difference.
 */
package org.mcp.variables;
