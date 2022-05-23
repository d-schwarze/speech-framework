package de.speech.worker.local;

import de.speech.core.dispatcher.IWorkerAudioRequest;

/**
 * The main interface of the Worker module. Used by the remote package to interact with the local package.
 */
public interface IWorkerServer {

    /**
     * Sets the handler used for receiving the result of a {@linkplain de.speech.core.dispatcher.IWorkerAudioRequest}
     *
     * @param handler The {@linkplain IResultHandler} to use
     */
    void setResultHandler(IResultHandler handler);

    /**
     * Returns the name given to the worker.
     * The name of two workers is identical if they use the same framework.
     *
     * @return The name of the framework
     */
    String getName();

    /**
     * Returns the model identifier that is returned by {@code ISpeechToTextService}
     *
     * @return the model identifier
     */
    String getModel();

    /**
     * Adds an {@linkplain IWorkerAudioRequest} to the queue of the worker
     *
     * @param request the request to add
     */
    void submitWork(IWorkerAudioRequest request);

    /**
     * Returns the maximum size the internal queue can have
     *
     * @return the maximum queue size
     */
    int getMaxQueueSize();

    /**
     * Returns the current size of the internal queue
     *
     * @return the internal queue size
     */
    int getQueueSize();

    /**
     * Used to safely shutdown the used threads
     *
     * @throws InterruptedException if joining the other thread fails
     */
    void shutdown() throws InterruptedException;
}
