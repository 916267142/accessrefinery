package org.aws.core;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.util.*;

public class SetCoverSolver {
    static {
        Loader.loadNativeLibraries();
    }

    public static Map<Object, Set<Integer>> solve(
            Map<Object, Set<Integer>> subsets,
            Set<Integer> universe) {

        // Create an ILP solver using the CBC solver backend
        MPSolver solver = new MPSolver("SetCover", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
        List<Object> subsetKeys = new ArrayList<>(subsets.keySet());
        int n = subsetKeys.size(); // Number of subsets

        // Decision variables: x_i ∈ {0,1} (1 if subset i is selected, 0 otherwise)
        MPVariable[] x = new MPVariable[n];
        for (int i = 0; i < n; i++) {
            x[i] = solver.makeIntVar(0, 1, "x" + i);
        }

        // Objective function: minimize the number of selected subsets
        MPObjective objective = solver.objective();
        for (int i = 0; i < n; i++) {
            objective.setCoefficient(x[i], 1);
        }
        objective.setMinimization();

        // Constraints: each element in the universe must be covered by at least one selected subset
        for (int u : universe) {
            MPConstraint constraint = solver.makeConstraint(1, Double.POSITIVE_INFINITY, "cover_" + u);
            for (int i = 0; i < n; i++) {
                if (subsets.get(subsetKeys.get(i)).contains(u)) {
                    constraint.setCoefficient(x[i], 1);
                }
            }
        }

        // Solve the ILP problem
        MPSolver.ResultStatus status = solver.solve();

        // Process and return the solution
        Map<Object, Set<Integer>> selectedSubsets = new HashMap<>();
        if (status == MPSolver.ResultStatus.OPTIMAL) {
            for (int i = 0; i < n; i++) {
                if (x[i].solutionValue() == 1) {
                    Object key = subsetKeys.get(i);
                    selectedSubsets.put(key, new HashSet<>(subsets.get(key)));
                }
            }
        }
        return selectedSubsets;
    }
}
