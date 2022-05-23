package de.speech.core.application.configuration;

import java.util.List;

/**
 * Class equivalent to the core-configuration-file.
 * <br><br>
 * <i>Core-configuration-file:</i>
 * <br>
 * Main file where the speech framework is configured for usage
 * The file contains
 * <ul>
 *     <li>All configured speech-workers are listed here, so that that can be used by the dispatcher.</li>
 * </ul>
 */
public interface SpeechConfiguration {

    /**
     * Gets all configured speech-workers.
     * @return all speech-worker
     */
    List<WorkerConfiguration> getWorkers();

    int getResultTimeout();

    int getHttpTimeout();

    int getPort();

    int getAcceptors();

    int getSelectors();

    int getQueueSize();

}
