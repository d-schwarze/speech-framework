package de.speech.core.logging;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.logging.Logger;

public class SpeechLoggerTest {

    @Test
    public final void testSpeechLogger() throws IOException {
        SpeechLogging.setup();

        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.info("Test");

    }

    @Test
    public final void testLoggable() {
        LoggableTest loggableTest = new LoggableTest();
        loggableTest.testLogging();
    }

    class LoggableTest implements Loggable {
        public void testLogging() {
            LOGGER.info("test");
        }
    }
}
