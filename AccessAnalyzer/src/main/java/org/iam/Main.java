package org.iam;

import org.iam.config.Parameter;
import org.iam.core.CmdRun;
import org.iam.utils.LoggerUtil;

import java.util.logging.Level;

public class Main {
    public static void main(String[] args) {
        Parameter.LOGGER = LoggerUtil.configureLogging(System.getProperty("user.dir") + "/miner.log");
        LoggerUtil.modifyLoggerLevel(Parameter.LOGGER, Level.INFO);

        CmdRun.run(args);
    }
}