package org.iam.utils;

import org.fusesource.jansi.Ansi;

import java.text.SimpleDateFormat;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Utility class for configuring and managing logging.
 * <p>
 * Provides methods to set up logging handlers, formatters, and log levels.
 * Supports colored console output and plain file output.
 * </p>
 */
public class LoggerUtil {
    private static String logFilePath;

    /**
     * Configures the root logger with console and file handlers.
     * Console output uses color formatting, file output is plain text.
     *
     * @param logFilePath the path to the log file
     * @return the configured Logger instance
     */
    public static Logger configureLogging(String logFilePath) {
        LoggerUtil.logFilePath = logFilePath;
        Logger logger = Logger.getLogger("");
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            logger.removeHandler(handler);
        }
        try {
            Formatter consoleFormatter = new CustomColorFormatter();
            Formatter fileFormatter = new PlainFormatter();
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(consoleFormatter);
            logger.addHandler(consoleHandler);
            FileHandler fileHandler = new FileHandler(logFilePath, true);
            fileHandler.setLevel(Level.INFO);
            fileHandler.setFormatter(fileFormatter);
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO);
            Logger objectInputFilterLogger = Logger.getLogger("java.io.ObjectInputFilter$Config");
            objectInputFilterLogger.setLevel(Level.INFO);
        } catch (IOException e) {
            System.err.println("Failed to configure file handler: " + e.getMessage());
        }
        return logger;
    }

    /**
     * Modifies the log level for the given logger and all its handlers.
     *
     * @param logger the Logger to modify
     * @param level  the new log level
     */
    public static void modifyLoggerLevel(Logger logger, Level level) {
        if (logger != null) {
            logger.setLevel(level);
            Handler[] handlers = logger.getHandlers();
            for (Handler handler : handlers) {
                handler.setLevel(level);
            }
        }
    }

    /**
     * Gets the current log file path.
     *
     * @return the log file path
     */
    public static String getLogFilePath() {
        return logFilePath;
    }

    /**
     * Formatter for colored console log output.
     */
    static class CustomColorFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            String logLevel = record.getLevel().getName();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = sdf.format(new Date(record.getMillis()));
//            String dateTime = DateFormat.getDateTimeInstance().format(new Date(record.getMillis()));
            String logMessage = record.getMessage();

            return Ansi.ansi()
                    .bold().fg(Ansi.Color.DEFAULT).a("[").reset().bold()
                    .fg(Ansi.Color.BLUE).a(logLevel).reset()
                    .bold().fg(Ansi.Color.DEFAULT).a("]")
                    .a(" ").a(dateTime)
                    .a(" : ").reset().fg(Ansi.Color.GREEN)
                    .a(logMessage).reset()
                    .toString() + "\n";
        }
    }

    /**
     * Formatter for plain text file log output.
     */
    static class PlainFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return String.format("[%s %s] %s%n",
                    record.getLevel().getName(),
                    DateFormat.getDateTimeInstance().format(new Date(record.getMillis())),
                    record.getMessage());
        }
    }
}