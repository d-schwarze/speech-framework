package de.speech.core.dispatcher;

import java.util.concurrent.CompletableFuture;

/**
 * Used to store the request and a {@linkplain CompletableFuture} that is used to store the result
 * of the framework. It is completed when the result is received.
 */
public interface ICompletableFrameworkAudioRequest {

    /**
     * A Getter for the CompletableFuture.
     *
     * @return CompletableFuture that contains the result when finished.
     */
    CompletableFuture<IFrameworkResult> getCompletableFuture();

    /**
     * Returns the associated {@linkplain IWorkerAudioRequest}
     *
     * @return The associated {@linkplain IWorkerAudioRequest}
     */
    IWorkerAudioRequest getWorkerAudioRequest();
}
