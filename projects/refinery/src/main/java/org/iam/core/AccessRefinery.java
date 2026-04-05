package org.iam.core;

import net.sf.javabdd.BDD;

import org.iam.core.MCPFactory.MCPType;
import org.iam.policy.grammer.Policy;
import org.iam.policy.model.MCPPolicy;
import org.iam.intent.MCPIntent;
import org.iam.intent.MergeIntent;
import org.iam.utils.Parameter;
import org.iam.utils.PolicyParser;
import org.iam.utils.Printer;
import org.iam.utils.ResultsAnalyzer;
import org.iam.model.DomainLabelTrees;
import org.iam.model.IntentOrPolicyLabel;
import org.iam.utils.TimeMeasure;
import org.iam.variables.statics.Label;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AccessRefinery is the main processing class for analyzing constraint files.
 * <p>
 * Responsible for parsing input files, calculating findings, reducing and merging results,
 * and exporting the results in various formats. This class coordinates the workflow between
 * parsing, symbolic computation, and result output.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public class AccessRefinery {
    TimeMeasure time = new TimeMeasure();

    /**
     * Default constructor.
     */
    public AccessRefinery() {}

    /**
     * Calculates findings based on the constraint file specified by {@code fileName}.
     * Parses the file, initializes symbolic domains, computes findings, and optionally reduces or merges them.
     *
     * @param fileName the path to the input file
     * @param analyzer the summary analyzer for timing and statistics
     * @return a set of findings calculated from the input
     * @throws IOException if file reading or writing fails
     */
    public HashSet<MCPIntent> running(Path fileName, ResultsAnalyzer analyzer) throws IOException {
        if(Parameter.isTimeLog) {
            time.startMeasurement();
            time.stopMeasurement("Labels");
            time.stopMeasurement("Operation");
            time.stopMeasurement("Encoding");
            time.stopMeasurement("Labels");
            time.stopMeasurement("Reduction");
            TimeMeasure.writeKeysToFile(Parameter.timeLog, time);
        }
        analyzer.startMeasurement();
        Policy policy = PolicyParser.parseFile(fileName);
        analyzer.setNumberStatement(policy.getStatement().size());
        Parameter.LOGGER.info("[1/6]  finish parser policy");

        MCPFactory mcpFactory;
        if(Parameter.isBDD) {
            mcpFactory = new MCPFactory(MCPType.BDD);
        } else {
            mcpFactory = new MCPFactory(MCPType.SAT);
        }
        MCPPolicy.setMCPFactory(mcpFactory);
        MCPPolicy mcpPolicy = new MCPPolicy(policy);
        Parameter.LOGGER.info("[2/6]  finish ECs calculation");

        if(Parameter.isTimeLog) {
            time.stopMeasurement("Labels");
            TimeMeasure.appendValuesToFile(Parameter.timeLog, time);
        }

        MCPIntent rootFinding = MCPIntent.getRootFinding(mcpFactory);
        rootFinding.getMCPNode();
        MCPIntent.setDomainLabelTrees(new DomainLabelTrees(mcpFactory));
        Parameter.LOGGER.info("[3/6]  finish label tree calculation");
        analyzer.addMCILabelsTime();
        HashSet<MCPIntent> findings = miningIntents(mcpPolicy, rootFinding, time, analyzer);
        Parameter.LOGGER.info("[4/6]  finish findings mining : " + findings.size());
        analyzer.addMCIOperationsTime();
        analyzer.setNumberIntentsMCI(findings.size());
        if(Parameter.isReduced) {
            findings = reducingIntents(mcpPolicy, findings, analyzer);
            if(Parameter.isMerged) {
                findings = mergingIntents(findings);
            }
            Parameter.LOGGER.info("[6/6]  finish findings reduction : " + findings.size());
        } else {
            Parameter.LOGGER.info("[5/6]  successful generate file"); 
            Parameter.LOGGER.info("[6/6]  successful written findings : " + findings.size()); 
        }
        analyzer.setNumberIntentsRRI(findings.size());
        if(Parameter.isTimeLog) {
            time.stopMeasurement("Reduction");
            TimeMeasure.appendValuesToFile(Parameter.timeLog, time);
        }
        return findings;
    }

    /**
     * Reduces the set of findings using set cover, based on the given MCPPolicy.
     *
     * @param mcpPolicy the MCPPolicy object
     * @param mcpFinding the set of findings to reduce
     * @param analyzer the summary analyzer for timing and statistics
     * @return a reduced set of findings
     * @throws IOException if an error occurs during reduction
     */
    private HashSet<MCPIntent> reducingIntents(MCPPolicy mcpPolicy, HashSet<MCPIntent> mcpFinding, ResultsAnalyzer analyzer) throws IOException {
        analyzer.startMeasurement();
        if (mcpFinding.size() == 0 || mcpFinding == null) {
            return mcpFinding;
        }
        // Create variables for policy and findings
        Set<Label> mcpVars = mcpFinding.stream()
                .filter(f -> !((MCPIntent)f).getMCPNode().isZero())
                .map(f -> new IntentOrPolicyLabel.Builder().setFinding(f).build())
                .collect(Collectors.toSet());
        mcpVars.add(new IntentOrPolicyLabel.Builder().setPolicy(mcpPolicy).build());

        // Create ECs
        Label logicTrue = new IntentOrPolicyLabel.Builder()
                .setMCP(MCPPolicy.getMCPFactory().getOne())
                .build();
        ECEngine ECEngine = new ECEngine(mcpVars, logicTrue);
        Parameter.LOGGER.info("[5/6]  finish ECs calculation");

        // Partition ECs into findings and policy
        Map<Object, Set<Integer>> findingsVarToECs = new HashMap<>();
        Set<Integer> policyECs = null;

        for (var entry : ECEngine.getECs().entrySet()) {
            IntentOrPolicyLabel var = (IntentOrPolicyLabel) entry.getKey();
            if (var.getVarType() == IntentOrPolicyLabel.VarType.FINDING) {
                findingsVarToECs.put(var, entry.getValue());
            } 
            if (var.getVarType() == IntentOrPolicyLabel.VarType.POLICY) {
                policyECs = entry.getValue();
            }
        }

        // System.out.println(findingsVarToECs);
        // System.out.println(policyECs);
        analyzer.addRRIOperationsTime();
        HashSet<MCPIntent> results = ILPSolver.solve(findingsVarToECs, policyECs).keySet().stream()
            .map(k -> (MCPIntent)(((IntentOrPolicyLabel) k).getValue()))
            .collect(Collectors.toCollection(HashSet::new));
        analyzer.addRRIILPSolvingTime();
        return results;
    }    

    /**
     * Merges compatible findings into larger intents.
     *
     * @param findings the set of findings to merge
     * @return a set of merged findings
     */
    public HashSet<MCPIntent> mergingIntents(HashSet<MCPIntent> findings) {
        HashSet<MergeIntent> mergeIntents = findings.stream()
                .map(MergeIntent::new)
                .collect(Collectors.toCollection(HashSet::new));

        HashSet<MergeIntent> tempIntents = mergeIntents;
        while(true) {
            Boolean endFlag = true;
            HashSet<MergeIntent> visited = new HashSet<>();
            for(MergeIntent intent : mergeIntents) {
                visited.add(intent);
                for(MergeIntent intent2 : mergeIntents) {
                    if(visited.contains(intent2)) {
                        continue;
                    }
                    if(intent.isMerged(intent2)) {
                        endFlag = false;
                        MergeIntent mergedIntent = intent.merge(intent2);
                        tempIntents.remove(intent);
                        tempIntents.remove(intent2);
                        tempIntents.add(mergedIntent);
                        break;
                    }
                }
                if(!endFlag) {
                    break;
                }
            }
            if(endFlag) {
                break;
            }
            mergeIntents = tempIntents;
            tempIntents = mergeIntents;
        }
        HashSet<MCPIntent> intents = mergeIntents.stream()
                .map(MergeIntent::getFinding)
                .collect(Collectors.toCollection(HashSet::new));
        return intents;
    }

    /**
     * Calculates the security findings based on the given MCPPolicy and root finding.
     * This internal method uses a breadth - first search (BFS) approach to traverse the finding hierarchy.
     * It maintains a queue of findings to be processed, a set of visited findings, and a set of result findings.
     * For each finding in the queue, it calculates the remaining BDD representation and checks if the finding
     * can be accessed based on the remaining policy. If it can be accessed, the finding is added to the result
     * set, and the remaining policy is updated. Otherwise, its unvisited child findings are added to the queue.
     *
     * @param mcpPolicy The MCPPolicy object that represents the security policy to be evaluated.
     * @param rootFinding The root Finding object from which the finding calculation starts.
     * @return A HashSet of Finding objects that represent the security findings calculated from the policy.
     */
    private HashSet<MCPIntent> miningIntents(MCPPolicy mcpPolicy, MCPIntent rootFinding, TimeMeasure time, ResultsAnalyzer analyzer) throws IOException {
        Queue<MCPIntent> queue = new LinkedList<>();
        HashSet<MCPIntent> res = new HashSet<>();
        MCPBitVector resMCP = MCPPolicy.getMCPFactory().getFalse().id();
        queue.add(rootFinding);
        MCPBitVector restMCP = mcpPolicy.getMCPNodeCalculation().id();
        HashSet<MCPIntent> visited = new HashSet<>();
        // this.printMCPPolicy(mcpPolicy, Path.of("/home/simple/accessrefinery/testMCPPolicy.dot"));
        int MCISolvingRound = 0;
        while(!queue.isEmpty()) {
            MCPIntent finding = queue.poll();
            if(visited.contains(finding)) {
                continue;
            }
            MCISolvingRound++;
            visited.add(finding);

            if(Parameter.isTimeLog) {
                time.stopMeasurement("Encoding");
            }

            HashSet<MCPIntent> childFindings = finding.refines();
            MCPBitVector reduce = getRestBDD(finding, childFindings);
            boolean flag = isCanAccess(restMCP, reduce);

            if(Parameter.isTimeLog) {
                time.stopMeasurement("Operation");
                TimeMeasure.appendValuesToFile(Parameter.timeLog, time);
            }

            if(flag) {
                if(!(finding.getMCPNode().diff(resMCP)).isZero()) {
                    res.add(finding);     
                }
                resMCP = resMCP.or(finding.getMCPNode());
                if(Parameter.isRestMode) {
                    restMCP = restMCP.diff(reduce);
                }
            } else {
                for(MCPIntent childFinding : childFindings) {
                    if(!(childFinding.getMCPNode().diff(resMCP)).isZero()) {
                        queue.add(childFinding);       
                    }
                }
            }
        }
        analyzer.setMCISolvingRoundAverage(MCISolvingRound);
        return res;
    }

    /**
     * Computes the remaining symbolic value for a finding after subtracting its children.
     *
     * @param currFinding the current finding
     * @param childFindings the set of child findings
     * @return the remaining MCPBitVector
     */
    private MCPBitVector getRestBDD(MCPIntent currFinding, HashSet<MCPIntent> childFindings) {
        if (childFindings.isEmpty()) {
            return currFinding.getMCPNode();
        }
        return childFindings.stream()
                .map(MCPIntent::getMCPNode)
                .reduce(currFinding.getMCPNode(), MCPBitVector::diff);
    }

    /**
     * Checks if a finding can be accessed based on the remaining policy and the finding's symbolic value.
     *
     * @param restPolicy the remaining policy as an MCPBitVector
     * @param reducingIntents the finding's MCPBitVector
     * @return true if accessible, false otherwise
     */
    private boolean isCanAccess(MCPBitVector restPolicy, MCPBitVector reducingIntents) {
        return !(reducingIntents.and(restPolicy)).isZero();
    }

    /**
     * Writes the symbolic representation of the MCPPolicy to a DOT file.
     *
     * @param mcpPolicy the MCPPolicy object
     * @param path the output file path
     */
    public void printMCPPolicy(MCPPolicy mcpPolicy, Path path) {
        Printer.writeStringToFile(MCPPolicy.getMCPFactory().dot((BDD)mcpPolicy.getMCPNode().getValue()), path.toString());
    }
}