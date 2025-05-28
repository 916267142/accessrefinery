package org.iam.utils;

import org.fusesource.jansi.Ansi;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class LoggerUtil {
    private static String logFilePath;

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

    public static void modifyLoggerLevel(Logger logger, Level level) {
        if (logger != null) {
            logger.setLevel(level);
            Handler[] handlers = logger.getHandlers();
            for (Handler handler : handlers) {
                handler.setLevel(level);
            }
        }
    }

    public static String getLogFilePath() {
        return logFilePath;
    }

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