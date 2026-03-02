package org.iam.config;

import java.util.Objects;
import java.util.logging.Logger;

public class Parameter {
    public static String timeLog = "";
    public static Logger LOGGER = Logger.getLogger("Findings Miner");
    public static boolean isReduced = false;
    private static SolverType activeSolver = SolverType.Z3;

    public static void setActiveSolver(SolverType solver) {
        Objects.requireNonNull(solver, "The solver type should not be null.");
        activeSolver = solver;
    }

    public static SolverType getActiveSolver() {
        return activeSolver;
    }

    public enum SolverType {
        Z3,
        CVC5
    }
}
