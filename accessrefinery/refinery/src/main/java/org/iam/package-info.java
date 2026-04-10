/**
 * Top-level package for the AccessRefinery analysis engine.
 *
 * <p>This package organizes the refinery implementation into layered modules:
 * <ul>
 *   <li>{@code org.iam.core}: pipeline orchestration, solving, and execution flow</li>
 *   <li>{@code org.iam.intent}: intent/finding representations and refinement logic</li>
 *   <li>{@code org.iam.model}: internal label-tree and symbolic wrapper models</li>
 *   <li>{@code org.iam.policy}: policy grammar and MCP-aware policy models</li>
 *   <li>{@code org.iam.utils}: shared utilities for parsing, IO, logging, and measurements</li>
 * </ul>
 *
 * <p>Together, these packages implement policy parsing, symbolic encoding, intent mining,
 * and reduction for IAM policy analysis.
 */
package org.iam;
