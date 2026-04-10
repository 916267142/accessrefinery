/**
 * SAT backend support for MCP symbolic domains and bit-vector encodings.
 *
 * <p>This package contains LogicNG-based structures for representing finite-domain
 * constraints as propositional formulas:
 * <ul>
 *   <li>{@link org.mcp.sat.SATInteger} / {@link org.mcp.sat.MutableSATInteger}: integer-to-formula encoding</li>
 *   <li>{@link org.mcp.sat.SATDomain}: finite-domain wrapper around SAT integer encodings</li>
 *   <li>{@link org.mcp.sat.SATUtils}: solver, satisfiability, and assignment utilities</li>
 * </ul>
 *
 * <p>These types enable MCP to run symbolic reasoning with a SAT formula backend in
 * parallel to the BDD backend.
 */
package org.mcp.sat;
