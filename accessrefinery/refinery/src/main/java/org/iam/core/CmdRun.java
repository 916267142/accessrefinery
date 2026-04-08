package org.iam.core;
import org.iam.intent.JsonIntent;
import org.iam.intent.MCPIntent;
import org.iam.utils.FileUtil;
import org.iam.utils.LoggerUtil;
import org.iam.utils.Parameter;
import org.iam.utils.ResultsAnalyzer;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

/**
 * Command-line entry point for running AccessRefinery and Zelkova batch processing.
 * <p>
 * Parses command-line arguments, sets parameters, and invokes mining or checking routines
 * for input files or directories.
 * </p>
 *
 * <ul>
 *   <li>-h, --help: Show help information</li>
 *   <li>-m, --mining: Enable mining mode (findings extraction)</li>
 *   <li>-r, --reducing: Enable reduction of findings</li>
 *   <li>-f, --file: Specify the input path for constraint files</li>
 *   <li>-s, --sat: Use SAT as the solving core (default is BDD)</li>
 *   <li>--round: Set the number of mining rounds</li>
 *   <li>--zelkova: Run Zelkova mode</li>
 *   <li>--rest: Enable REST mode</li>
 *   <li>--merge: Merge intents in output</li>
 * </ul>
 *
 * @author
 * @since 2025-02-28
 */
public class CmdRun {
    public static void run(String[] args) {
        Parameter.isTimeLog = true;
        Parameter.LOGGER = LoggerUtil.configureLogging(System.getProperty("user.dir") + "/accessrefinery.log");
        LoggerUtil.modifyLoggerLevel(Parameter.LOGGER, Level.INFO);

        Options options = new Options();
        options.addOption("h", "help", false, "Show help information");
        options.addOption("m", "mine", false, "Enable mining mode (extract findings)");
        options.addOption("r", "reduce", false, "Enable reduction of findings");
        options.addOption("f", "file", true, "Input path for constraint files");
        options.addOption("s", "sat", false, "Use SAT as solving core (default is BDD)");
        options.addOption(null, "round", true, "Number of mining rounds");
        options.addOption(null, "zelkova", false, "Run Zelkova mode");
        options.addOption(null, "rest", false, "Enable REST mode");
        options.addOption(null, "merge", false, "Merge intents in output");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("the help of accessrefinery", options);
                return;
            } 

            if (cmd.hasOption("s")) {
                Parameter.isBDD = false;
            } else {
                Parameter.isBDD = true;
            }

            if (cmd.hasOption("round")) {
                Parameter.round = Integer.parseInt(cmd.getOptionValue("round"));
            }
            
            if (cmd.hasOption("r")) {
                Parameter.isReduced = true;
            }

            if (cmd.hasOption("rest")) {
                Parameter.isRestMode = true;
            }

            if (cmd.hasOption("merge")) {
                Parameter.isMerged = true;
            }

            if (cmd.hasOption("m") && cmd.hasOption("f")) {
                String filePath = cmd.getOptionValue("f");
                runBatchAccessRefinery(filePath);
            } else if (cmd.hasOption("zelkova") && cmd.hasOption("f")) {
                String filePath = cmd.getOptionValue("f");
                runBatchZelkova(filePath);
            }

        } catch (ParseException | IOException e) {
            Parameter.LOGGER.severe("Parsing failed.  Reason: " + e.getMessage());
        }
    }

    static int policiesIdx = 0;
    public static void runBatchAccessRefinery(String input) throws IOException {
        Path inputFoldPath = Paths.get(input);
        Path outputFoldPath = FileUtil.replaceSecondLastLevel(inputFoldPath);
        FileUtil.createDirectoryIfNotExists(outputFoldPath);
        Parameter.LOGGER.info("----------[ AccessRefinery Mode ]-------------");
        Parameter.LOGGER.info("logger path: " + LoggerUtil.getLogFilePath());
        Parameter.LOGGER.info("input  path: " + inputFoldPath);
        Parameter.LOGGER.info("output path: " + outputFoldPath);
        List<String> fileNames = FileUtil.getFileNames(inputFoldPath);
        ResultsAnalyzer resultsAnalyzer = new ResultsAnalyzer();
        resultsAnalyzer.writeHeaderToFile(outputFoldPath.resolve("summary.txt").toString());
        for(String fileName : fileNames) {
            Parameter.LOGGER.info("----------< " + ++policiesIdx + "th policy - " + fileName + " >-----------");
            Path inputFilePath = inputFoldPath.resolve(fileName);
            Parameter.timeLog = outputFoldPath.resolve(FileUtil.changeToCsvWithTime(fileName)).toString();

            AccessRefinery miner = new AccessRefinery();
            int round = Parameter.round;
            HashSet<MCPIntent> findings = new HashSet<>();
            while(round-- > 0) {
                resultsAnalyzer.initializes();
                findings = miner.running(inputFilePath, resultsAnalyzer);
            }
            resultsAnalyzer.calculateAverage();
            resultsAnalyzer.writeAveragesToFile(outputFoldPath.resolve("summary.txt").toString());
            Path outputFindingPath = outputFoldPath.resolve(FileUtil.changeToJsonWithFindings(fileName));
            JsonIntent jsonFindings = new JsonIntent(inputFilePath.toString(), findings);
            JsonIntent.printToFile(jsonFindings, outputFindingPath);
        }
    }

    public static void runBatchZelkova(String input) throws IOException {
        Path inputFoldPath = Paths.get(input);
        Path outputFoldPath = FileUtil.replaceSecondLastLevel(inputFoldPath);
        FileUtil.createDirectoryIfNotExists(outputFoldPath);
        Parameter.LOGGER.info("----------[ Zelkova Mode ]-------------");
        Parameter.LOGGER.info("Logger path: " + LoggerUtil.getLogFilePath());
        Parameter.LOGGER.info("Input  path: " + inputFoldPath);
        List<String> fileNames = FileUtil.getFileNames(inputFoldPath);

        for(String fileName : fileNames) {
            Parameter.LOGGER.info("----------< " + ++policiesIdx + "th policy - " + fileName + " >-----------");
            Path inputFilePath = inputFoldPath.resolve(fileName);

            Zelkova checker = new Zelkova();
            String sat = checker.isSatisfiable(inputFilePath) ? "sat" : "unsat";
            Parameter.LOGGER.info("Policy " + fileName + " is " + sat + " : " + checker.getTime().getSingleRoundTotalTime() + "ms");

            Path outputFindingPath = outputFoldPath.resolve(FileUtil.changeToDot(fileName));
            checker.printMCPPolicy(outputFindingPath);
        }
    }
}
