package org.iam.sat;

import org.logicng.datastructures.Assignment;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;
import java.util.Arrays;

/**
 * Utility methods for SAT-based symbolic computations.
 * <p>
 * Provides helper functions for creating SAT solvers, bit vectors, checking satisfiability,
 * and extracting assignments.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public class SATUtils {
    /**
     * Creates a new SAT solver instance for the given formula factory.
     *
     * @param factory the formula factory
     * @return a new SATSolver instance
     */
    public static SATSolver createSolver(FormulaFactory factory) {
        return MiniSat.miniSat(factory);
    }

    /**
     * Creates a bit vector of SAT variables.
     *
     * @param factory the formula factory
     * @param length  the number of bits
     * @param start   the starting index for variable names
     * @param reverse whether to reverse the bit order
     * @return an array of SAT variables
     */
    public static Variable[] bitvector(FormulaFactory factory, int length, int start, boolean reverse) {
        Variable[] bitvec = new Variable[length];
        for (int i = 0; i < length; i++) {
            int idx;
            if (reverse) {
                idx = start + length - i - 1;
            } else {
                idx = start + i;
            }
            bitvec[i] = factory.variable("v_" + idx);
        }
        return bitvec;
    }

    /**
     * Concatenates multiple bit vectors into a single array.
     *
     * @param arrays arrays of SAT variables to concatenate
     * @return a single concatenated array of SAT variables
     */
    public static Variable[] concatBitvectors(Variable[]... arrays) {
        return Arrays.stream(arrays).flatMap(Arrays::stream).toArray(Variable[]::new);
    }

    /**
     * Checks if the given formula is satisfiable.
     *
     * @param factory the formula factory
     * @param formula the formula to check
     * @return true if satisfiable, false otherwise
     */
    public static boolean isSatisfying (FormulaFactory factory, Formula formula) {
        SATSolver miniSat = MiniSat.miniSat(factory);
        miniSat.add(formula);
        Tristate result = miniSat.sat();
        return result == Tristate.TRUE;
    } 

    /**
     * Returns a satisfying assignment for the given formula and bit vector.
     * Throws an assertion error if the formula is not satisfiable.
     *
     * @param factory the formula factory
     * @param formula the formula to solve
     * @param bitvec  the bit vector variables
     * @return the satisfying assignment
     */
    public static Assignment getAssignment (FormulaFactory factory, Formula formula, Variable[] bitvec) {
        
        SATSolver miniSat = MiniSat.miniSat(factory);
        miniSat.add(formula);
        Tristate result = miniSat.sat();
        assert result == Tristate.TRUE : "Formula is not satisfiable";
        return miniSat.model(bitvec);
    } 
}