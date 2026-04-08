package org.iam.core;

import org.iam.policy.grammer.Policy;
import org.iam.policy.model.MCPPolicy;
import org.iam.utils.Parameter;
import org.iam.utils.PolicyParser;
import org.iam.utils.TimeMeasure;
import org.mcp.core.MCPFactory;
import org.mcp.utils.Printer;

import net.sf.javabdd.BDD;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Zelkova provides satisfiability checking and symbolic analysis for constraint files.
 * <p>
 * Parses input files, builds symbolic representations, checks satisfiability,
 * and exports symbolic graphs in DOT format.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public class Zelkova {
    TimeMeasure time = new TimeMeasure();

    MCPFactory mcpFactory;

    MCPPolicy mcpPolicy;

    /**
     * Default constructor.
     */
    public Zelkova() {}

    /**
     * Checks if the constraint file is satisfiable.
     *
     * @param fileName the path to the input file
     * @return true if satisfiable, false otherwise
     * @throws IOException if file reading fails
     */
    public boolean isSatisfiable(Path fileName) throws IOException {
        if(Parameter.isTimeLog) {
            time.startMeasurement();
        }

        Policy policy = PolicyParser.parseFile(fileName);
        Parameter.LOGGER.info("finish parser policy                        [1/3]");

        mcpFactory = new MCPFactory();
        MCPPolicy.setMCPFactory(mcpFactory);
        mcpPolicy = new MCPPolicy(policy);
        Parameter.LOGGER.info("finish ECs calculation        [2/3]");

        if(Parameter.isTimeLog) {
            time.stopMeasurement("Calculation");
        }
        int satCount = (int) mcpPolicy.getMCPNodeCalculation().satCount();
        Parameter.LOGGER.info("finish policy encoding                      [3/3]");
        return satCount != 0;
    }

    /**
     * Returns the MCPFactory used for symbolic computation.
     *
     * @return the MCPFactory instance
     */
    public MCPFactory getMCPFactory() {
        return mcpFactory;
    }

    /**
     * Returns the MCPPolicy built from the input file.
     *
     * @return the MCPPolicy instance
     */
    public MCPPolicy getMCPPolicy() {
        return mcpPolicy;
    }

    /**
     * Writes the symbolic representation of the MCPPolicy to a DOT file.
     *
     * @param path the output file path
     */
    public void printMCPPolicy(Path path) {
        Printer.writeStringToFile(MCPPolicy.getMCPFactory().dot((BDD)mcpPolicy.getMCPNode().getValue()), path.toString());
    }

    /**
     * Returns the TimeMeasure instance for timing statistics.
     *
     * @return the TimeMeasure instance
     */
    public TimeMeasure getTime() {
        return time;
    }
}