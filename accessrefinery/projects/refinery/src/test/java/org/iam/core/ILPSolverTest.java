package org.iam.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.*;

import static org.junit.Assert.*;

public class ILPSolverTest {

    @Test
    public void testILPSolver() {

        HashMap<Object, Set<Integer>> subsets = new HashMap<>();
        subsets.put("S1", new HashSet<>(Arrays.asList(1, 2, 3)));
        subsets.put(42, new HashSet<>(Arrays.asList(2, 4))); // Integer as key
        subsets.put(3.14, new HashSet<>(Arrays.asList(3, 4, 5, 6))); // Double as key
        subsets.put('A', new HashSet<>(Arrays.asList(1, 5))); // Character as key
        subsets.put(UUID.randomUUID(), new HashSet<>(Arrays.asList(2, 3, 5))); // UUID as key

        Set<Integer> universe = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));

        Map<Object, Set<Integer>> result = ILPSolver.solve(subsets, universe);

        System.out.println("Selected optimal subsets:");
        for (Map.Entry<Object, Set<Integer>> entry : result.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    @Test
    public void testILP() {
        Loader.loadNativeLibraries(); // Load OR-Tools library

        // Define the set of elements U
        Set<Integer> U = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));

        // Define candidate subsets S
        int[][] subsets = {
            {1, 2, 3, 6},    // S1
            {2, 4},       // S2
            {3, 4, 5, 7},    // S3
            {1, 5},       // S4
            {2, 3, 5}     // S5
        };

        int n = subsets.length; // Number of subsets

        // Create OR-Tools solver
        MPSolver solver = new MPSolver("SetCover", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);

        // Define decision variables: whether each subset is selected (x_i ∈ {0,1})
        MPVariable[] x = new MPVariable[n];
        for (int i = 0; i < n; i++) {
            x[i] = solver.makeIntVar(0, 1, "x" + i);
        }

        // Objective function: minimize the number of selected subsets ∑ x_i
        MPObjective objective = solver.objective();
        for (int i = 0; i < n; i++) {
            objective.setCoefficient(x[i], 1);
        }
        objective.setMinimization();

        // Constraints: each element u ∈ U must be covered by at least one subset
        for (int u : U) {
            MPConstraint constraint = solver.makeConstraint(1, Double.POSITIVE_INFINITY, "cover_" + u);
            for (int i = 0; i < n; i++) {
                // If subset Si contains element u, then x_i contributes to this constraint
                if (contains(subsets[i], u)) {
                    constraint.setCoefficient(x[i], 1);
                }
            }
        }

        // Solve the problem
        MPSolver.ResultStatus status = solver.solve();

        // Output the result
        Set<String> selectedSubsets = new HashSet<>();
        if (status == MPSolver.ResultStatus.OPTIMAL) {
            for (int i = 0; i < n; i++) {
                if (x[i].solutionValue() == 1) {
                    selectedSubsets.add("S" + (i + 1) + " : " + Arrays.toString(subsets[i]));
                }
            }
            assertEquals(2, selectedSubsets.size()); // Example assertion, adjust as needed
            assertTrue(selectedSubsets.contains("S1 : [1, 2, 3, 6]"));
            assertTrue(selectedSubsets.contains("S3 : [3, 4, 5, 7]"));
        } else {
            fail("No optimal solution found");
        }
    }

    // Helper function: check if an element is in a subset
    private static boolean contains(int[] subset, int element) {
        for (int num : subset) {
            if (num == element) {
                return true;
            }
        }
        return false;
    }
}