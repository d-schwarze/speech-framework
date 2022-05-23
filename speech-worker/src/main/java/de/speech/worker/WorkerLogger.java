package de.speech.worker;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * The logger of the worker
 */
public class WorkerLogger {

    private static final String PATH = "worker.log";

    private static final Logger logger;

    static {
        logger = Logger.getGlobal();
        logger.setLevel(Level.ALL);

        try {
            FileHandler fileHandler = new FileHandler(PATH);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Error while accessing the log file, the log will not be written to the file!");
        }
    }

    /**
     * Logs a message with {@linkplain Level#INFO}
     *
     * @param message the message to log
     */
    public static void info(String message) {
        logger.info(message);
    }

    /**
     * Logs a message with {@linkplain Level#SEVERE}
     *
     * @param message   the message to log
     * @param throwable the {@linkplain Throwable} associated with the error
     */
    public static void error(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }

    /**
     * Logs a message with {@linkplain Level#SEVERE}
     *
     * @param message the message to log
     */
    public static void error(String message) {
        logger.log(Level.SEVERE, message);
    }
}
