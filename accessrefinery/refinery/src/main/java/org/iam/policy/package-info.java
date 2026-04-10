/**
 * IAM policy representation and parsing support.
 *
 * <p>This package groups policy-related abstractions used by AccessRefinery and is
 * organized into subpackages:
 * <ul>
 *   <li>{@code org.iam.policy.grammer}: JSON grammar/DTO layer and deserializers for policy documents</li>
 *   <li>{@code org.iam.policy.model}: MCP-aware symbolic policy model and statement components</li>
 * </ul>
 *
 * <p>Together, these components transform raw policy documents into analyzable symbolic
 * structures for intent mining and reduction.
 */
package org.iam.policy;
