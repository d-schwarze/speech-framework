package de.speech.core.logging;

import java.util.logging.Logger;

/**
 * This interface provides a global logger.
 * @see SpeechLogging
 */
public interface Loggable {

    /**
     * Global logger
     */
    Logger LOGGER = SpeechLogging.getLogger();

}
