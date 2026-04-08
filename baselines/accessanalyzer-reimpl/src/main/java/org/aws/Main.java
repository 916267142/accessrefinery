package org.aws;

import org.aws.config.Parameter;
import org.aws.core.CmdRun;
import org.aws.utils.LoggerUtil;

import java.util.logging.Level;

public class Main {
    public static void main(String[] args) {
        Parameter.LOGGER = LoggerUtil.configureLogging(System.getProperty("user.dir") + "/miner.log");
        LoggerUtil.modifyLoggerLevel(Parameter.LOGGER, Level.INFO);

        CmdRun.run(args);
    }
}