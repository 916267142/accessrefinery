/**
 * BDD-based symbolic primitives adapted for MCP network-policy reasoning.
 *
 * <p>This package provides low-level binary decision diagram abstractions used by
 * MCP symbolic execution and domain encoding:
 * <ul>
 *   <li>{@link org.batfish.BDDInteger} / {@link org.batfish.MutableBDDInteger}: bit-vector integer encoding on BDD variables</li>
 *   <li>{@link org.batfish.BDDDomain}: finite-domain wrapper over BDD integer encodings</li>
 *   <li>{@link org.batfish.BDDUtils}: utility helpers for factory setup, bit vectors, and variable pairing</li>
 * </ul>
 *
 * <p>These components are used as a backend foundation for fast symbolic set operations
 * and satisfiability-driven reasoning.
 */
package org.batfish;
