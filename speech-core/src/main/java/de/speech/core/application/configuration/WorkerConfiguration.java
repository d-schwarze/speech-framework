package de.speech.core.application.configuration;

/**
 * Class equivalent for the speech-worker configuration that can added into the core-configuration-file.
 *
 */
public interface WorkerConfiguration {

    /**
     * Getter for the location of a speech-worker.
     * For details on the return type see the implementations.
     * @return location location to access the speech-worker
     */
    String getLocation();

}
