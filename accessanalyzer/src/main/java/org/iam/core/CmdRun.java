package org.iam.core;

import org.iam.config.Parameter;
import org.iam.grammer.Policy;
import org.iam.grammer.serializer.JsonFindings;
import org.iam.grammer.Finding;
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

            if (cmd.hasOption("r")) {
                Parameter.isReduced = true;
            }

            if (cmd.hasOption("f")) {
                String filePath = cmd.getOptionValue("f");
                Path inputPath = Paths.get(filePath);
                File file = inputPath.toFile();
                if (!file.exists()) {
                    throw new ParseException("The input file does not exist.");
                } else if (file.isDirectory()) {
                    runBatchMiner(inputPath);
                } else if (file.isFile()) {
                    runSingleMiner(inputPath);
                } else {
                    throw new ParseException("The input file is neither a file nor a directory.");
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
        MinerFactory minerFactory = new MinerFactory();
        Policy policy = PolicyParser.parseFile(inputPath);
        Parameter.LOGGER.info("[1/5]  finish parser policy");
        long startTime = System.nanoTime();
        Set<Finding> ansFindings = minerFactory.mineIntent(policy, timeMeasure);
        Parameter.LOGGER.info("[3/5]  finish findings mining : " + ansFindings.size());

        if (Parameter.isReduced) {
            ansFindings = minerFactory.reduceIntent(policy, ansFindings);
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

    static int policiesIdx = 0;
    public static void runBatchMiner(Path inputFolderPath) throws IOException {
        Path outputFolderPath = FileUtil.replaceSecondLastLevel(inputFolderPath);
        FileUtil.createDirectoryIfNotExists(outputFolderPath);
        Parameter.LOGGER.info("----------[ Shaky Jenga Tower Code ]-------------");
        Parameter.LOGGER.info("logger path: " + LoggerUtil.getLogFilePath());
        Parameter.LOGGER.info("input  path: " + inputFolderPath);
        Parameter.LOGGER.info("output path: " + outputFolderPath);
        List<String> fileNames = FileUtil.getFileNames(inputFolderPath);

        for (String fileName : fileNames) {
            Parameter.LOGGER.info("----------< " + ++policiesIdx + "th policy - " + fileName + " >-----------");
            Path inputFilePath = inputFolderPath.resolve(fileName);
            Parameter.timeLog = outputFolderPath.resolve(FileUtil.changeToCsvWithTime(fileName)).toString();

            TimeMeasure timeMeasure = new TimeMeasure();
            MinerFactory minerFactory = new MinerFactory();
            Policy policy = PolicyParser.parseFile(inputFilePath);
            Parameter.LOGGER.info("[1/5]  finish parser policy");
            long startTime = System.nanoTime();
            Set<Finding> ansFindings = minerFactory.mineIntent(policy, timeMeasure);
            Parameter.LOGGER.info("[3/5]  finish findings mining : " + ansFindings.size());

            if (Parameter.isReduced) {
                ansFindings = minerFactory.reduceIntent(policy, ansFindings);
                Parameter.LOGGER.info("[5/5]  finish findings reduction : " + ansFindings.size());
            } else {
                Parameter.LOGGER.info("[4/5]  successful generate file");
                Parameter.LOGGER.info("[5/5]  successful written findings : " + ansFindings.size());
            }

            long endTime = System.nanoTime();
            long wholeTime = endTime - startTime;
            timeMeasure.setWholeTime(wholeTime);
            JsonFindings jsonFindings = new JsonFindings(ansFindings);

            Path outputFindingPath = outputFolderPath.resolve(FileUtil.changeToJsonWithFindings(fileName));
            JsonFindings.printToFile(jsonFindings, outputFindingPath);
            Parameter.LOGGER.info("The findings file was output to " + outputFindingPath);
            Parameter.LOGGER.info("The time file was output to " + Parameter.timeLog);
            Parameter.LOGGER.info(String.format("Time: %.4f%n", wholeTime / 1e9));
            timeMeasure.writeToFile(Parameter.timeLog);
        }
    }
}
