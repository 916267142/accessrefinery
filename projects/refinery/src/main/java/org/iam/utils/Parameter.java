package org.iam.utils;

import java.util.logging.Logger;

/**
 * Global parameters and flags for controlling AccessRefinery behavior.
 * <p>
 * This class holds static configuration options and runtime flags
 * used throughout the application.
 * </p>
 */
public class Parameter {
    /** Enable or disable time logging. */
    public static boolean   isTimeLog      =   false;
    /** Logger instance for findings and general logging. */
    public static Logger    LOGGER         =   Logger.getLogger("Findings AccessRefinery");
    /** Stores time log information as a string. */
    public static String    timeLog        =   "";
    /** Treat the list of values as a whole label, e.g., IAM:[User1, User2]. */
    public static boolean   isSplitLabel   =   true;
    /** If true, use the whole space minus the finding when mining findings. */
    public static boolean   isRestMode     =   false;
    /** Whether to reduce the findings. */
    public static boolean   isReduced      =   false;
    /** Whether to merge results or findings. */
    public static boolean   isMerged       =   false;
    /** Current round or iteration count. */
    public static int       round          =   1;
    /** Enable or disable BDD (Binary Decision Diagram) mode. */
    public static boolean   isBDD          =   true;
}