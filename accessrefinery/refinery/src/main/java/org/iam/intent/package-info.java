/**
 * Intent abstractions used by AccessRefinery mining and refinement.
 *
 * <p>This package models discovered intents/findings and intent transformations:
 * <ul>
 *   <li>{@link org.iam.intent.MCPIntent}: symbolic intent with domain values and refinement operations</li>
 *   <li>{@link org.iam.intent.MergeIntent}: merge-friendly wrapper for intent consolidation</li>
 *   <li>{@link org.iam.intent.JsonIntent}: serialization-oriented intent representation</li>
 * </ul>
 *
 * <p>Intent objects bridge domain-value assignments and symbolic MCP nodes for search,
 * pruning, and final result emission.
 */
package org.iam.intent;
