/**
 * Core orchestration and solving workflow for AccessRefinery.
 *
 * <p>This package coordinates end-to-end refinement analysis from policy parsing to
 * intent mining, reduction, and output generation:
 * <ul>
 *   <li>{@link org.iam.core.AccessRefinery}: main pipeline for mining/reduction/merge flow</li>
 *   <li>{@link org.iam.core.ILPSolver}: set-cover style reduction support</li>
 *   <li>{@link org.iam.core.Zelkova}: theorem-solver style checks used in the pipeline</li>
 *   <li>{@link org.iam.core.CmdRun}: command execution helpers for external tools</li>
 * </ul>
 *
 * <p>The package acts as the execution backbone that glues policy modeling, symbolic
 * encodings, and optimization stages together.
 */
package org.iam.core;
