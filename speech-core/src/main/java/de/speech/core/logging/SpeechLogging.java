package de.speech.core.logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Class modifies the default global logger and provides it for usage.
 */
public class SpeechLogging {

    /**
     * Handler for writing logs to a logging file
     */
    private static FileHandler logFileHandler;

    /**
     * Log file location
     */
    private static String logFile = "logging.txt";

    /**
     * Modifies the default global logger
     *
     * @throws IOException if an IO-Error occurs with log file.
     * {@linkplain FileHandler#FileHandler(String) See FileHandler}
     */
    public static void setup() throws IOException {
        Logger logger = Logger.getGlobal();

        logger.setLevel(Level.ALL);

        logFileHandler = new FileHandler(logFile);
        logFileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(logFileHandler);
    }

    /**
     * Method for getting the default global logger.
     * @return default global logger
     */
    public static Logger getLogger() {
        return Logger.getGlobal();
    }
}
