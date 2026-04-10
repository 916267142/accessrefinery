/**
 * Internal model structures for label hierarchy and symbolic wrappers.
 *
 * <p>This package contains data structures used to organize domain labels and bind
 * them to policy/intent computations:
 * <ul>
 *   <li>{@link org.iam.model.LabelTree}: hierarchical relation model for domain labels</li>
 *   <li>{@link org.iam.model.DomainLabelTrees}: per-domain label tree registry</li>
 *   <li>{@link org.iam.model.IntentOrPolicyLabel}: unified label wrapper for reduction stages</li>
 *   <li>{@link org.iam.model.MCPVar}: symbolic-variable contract for MCP encodable entities</li>
 * </ul>
 *
 * <p>These models provide the structural layer that supports refinement traversal and
 * reduction-time equivalence reasoning.
 */
package org.iam.model;
