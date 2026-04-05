package org.iam.core;

import org.iam.config.Parameter;
import org.iam.grammar.Policy;
import org.iam.grammar.serializer.JsonFindings;
import org.iam.grammar.Finding;
import org.iam.utils.FileUtil;
import org.iam.utils.LoggerUtil;
import org.iam.utils.PolicyParser;
import org.iam.utils.TimeMeasure;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class CmdRun {
    public static void run(String[] args) {
        Options options = new Options();
        options.addOption(Option.builder("h")
                .longOpt("help")
                .hasArg(false)
                .desc("output the help information")
                .build());
        options.addOption(Option.builder("f")
                .longOpt("file")
                .hasArg(true)
                .desc("the input path of policies")
                .build());
        options.addOption(Option.builder("s")
                .longOpt("solver")
                .hasArg(true)
                .desc("use which SMT solver, CVC5 or Z3")
                .build());
        options.addOption(Option.builder("r")
                .longOpt("reduce")
                .hasArg(false)
                .desc("reduce the number of intents")
                .build());
        options.addOption(Option.builder("c")
                .longOpt("cover")
                .hasArg(true)
                .numberOfArgs(2)
                .valueSeparator(' ')
                .desc("check whether the findings cover the policy, provide policy file and findings file")
                .build());

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("the help of miner", options);
                return;
            }

            if (cmd.hasOption("s")) {
                String optionValue = cmd.getOptionValue("s");
                switch (optionValue.toUpperCase()) {
                    case "Z3":
                        Parameter.setActiveSolver(Parameter.SolverType.Z3);
                        break;
                    case "CVC5":
                        Parameter.setActiveSolver(Parameter.SolverType.CVC5);
                        break;
                    default:
                        throw new ParseException(
                                String.format("Invalid type of solver: '%s' (Available options: Z3, CVC5)", optionValue)
                        );
                }
            }

            if (cmd.hasOption("c")) {
                String[] values = cmd.getOptionValues("c");
                if (values.length != 2) {
                    throw new ParseException("Please provide both policy file and findings file for covering check.");
                }
                Path policyPath = Paths.get(values[0]);
                Path findingsPath = Paths.get(values[1]);
                CmdRun cmdRun = new CmdRun();
                cmdRun.runCoveringChecker(policyPath, findingsPath);
                return;
            }

            if (cmd.hasOption("r")) {
                Parameter.isReduced = true;
            }

            if (cmd.hasOption("f")) {
                String filePath = cmd.getOptionValue("f");
                Path inputPath = Paths.get(filePath);
                File file = inputPath.toFile();
                if (!file.exists()) {
                    throw new ParseException("The input file does not exist.");
                } else if (file.isFile()) {
                    runSingleMiner(inputPath);
                } else {
                    throw new ParseException("The input file is neither a file.");
                }
            }
        } catch (IOException | ParseException e) {
            Parameter.LOGGER.severe("Parsing failed. Reason: " + e.getMessage());
        }
    }

    public static void runSingleMiner(Path inputPath) throws IOException {
        Path outputPath = FileUtil.replaceThirdLastLevel(inputPath);
        FileUtil.createDirectoryIfNotExists(outputPath);

        Parameter.LOGGER.info("----------[ Shaky Jenga Tower Code ]-------------");
        Parameter.LOGGER.info("logger path: " + LoggerUtil.getLogFilePath());
        Parameter.LOGGER.info("input  path: " + inputPath);
        Parameter.LOGGER.info("output path: " + outputPath);

        String fileName = inputPath.getFileName().toString();
        Parameter.LOGGER.info("----------< Processing policy - " + fileName + " >-----------");

        Parameter.timeLog = outputPath.resolve(FileUtil.changeToCsvWithTime(fileName)).toString();

        TimeMeasure timeMeasure = new TimeMeasure();
        Miner miner = new Miner();
        Policy policy = PolicyParser.parseFile(inputPath);
        Parameter.LOGGER.info("[1/5]  finish parser policy");
        long startTime = System.nanoTime();
        Set<Finding> ansFindings = miner.mineIntent(policy, timeMeasure);
        Parameter.LOGGER.info("[3/5]  finish findings mining : " + ansFindings.size());

        if (Parameter.isReduced) {
            ansFindings = miner.reduceIntent(policy, ansFindings);
            Parameter.LOGGER.info("[5/5]  finish findings reduction : " + ansFindings.size());
        } else {
            Parameter.LOGGER.info("[4/5]  successful generate file");
            Parameter.LOGGER.info("[5/5]  successful written findings : " + ansFindings.size());
        }

        long endTime = System.nanoTime();
        long wholeTime = endTime - startTime;
        timeMeasure.setWholeTime(wholeTime);
        JsonFindings jsonFindings = new JsonFindings(ansFindings);

        Path outputFindingPath = outputPath.resolve(FileUtil.changeToJsonWithFindings(fileName));
        JsonFindings.printToFile(jsonFindings, outputFindingPath);
        Parameter.LOGGER.info("The findings file was output to " + outputFindingPath);
        Parameter.LOGGER.info("The time file was output to " + Parameter.timeLog);
        Parameter.LOGGER.info(String.format("Time: %.4f%n", wholeTime / 1e9));
        timeMeasure.writeToFile(Parameter.timeLog);
    }

    public void runCoveringChecker(Path policyPath, Path findingsPath) {
        Path outputPath = FileUtil.replaceThirdLastLevel(policyPath);
        FileUtil.createDirectoryIfNotExists(outputPath);

        Parameter.LOGGER.info("----------[ Shaky Jenga Tower Code ]-------------");
        Parameter.LOGGER.info("logger path: " + LoggerUtil.getLogFilePath());
        Parameter.LOGGER.info("policy path: " + policyPath);
        Parameter.LOGGER.info("findings path: " + findingsPath);

        String policyName = policyPath.getFileName().toString();
        String findingsName = findingsPath.getFileName().toString();
        Parameter.LOGGER.info("----------< Processing policy - " + policyName + " >-----------");
        Parameter.LOGGER.info("----------< Processing findings - " + findingsName + " >-----------");

        Miner miner = new Miner();
        Policy policy = PolicyParser.parseFile(policyPath);
        Set<Finding> findings = PolicyParser.parseFindings(findingsPath);
        Parameter.LOGGER.info("[1/2]  finish parser policy and findings");
        boolean ans = miner.checkCovering(policy, findings);
        if (ans) {
            Parameter.LOGGER.info("The findings cover the policy.");
        } else {
            Parameter.LOGGER.info("The findings do not cover the policy.");
        }
        Parameter.LOGGER.info("[2/2]  finish covering checking : " + ans);
    }
}
