package de.speech.core.dispatcher;

import de.speech.core.application.configuration.WorkerConfiguration;
import de.speech.core.dispatcher.implementation.WorkerStatus;
import de.speech.core.framework.IFramework;

/**
 * This interface defines an object that represents a worker. The worker get its request from
 * a {@linkplain IWorkerAudioRequestSource}.
 */
public interface IWorkerCore {

    /**
     * Initializes the worker.
     */
    void initialize();

    /**
     * Stops the worker.
     *
     * @throws Exception if unable to terminate.
     */
    void stop() throws Exception;

    /**
     * A Setter for the requests source.
     *
     * @param source source of new requests
     */
    void setRequestSource(IWorkerAudioRequestSource source);


    /**
     * Gets the next element from the source and send it.
     */
    void sendRequest();

    /**
     * Returns the minimum amount of free places in the queue.
     *
     * @return number of free places in the queue.
     */
    int getQueuePlacesFree();

    /**
     * Returns the number of queue items.
     *
     * @return Number of queue items
     */
    int getQueueItemsAmount();

    /**
     * A getter for the framework.
     *
     * @return The id of the framework
     */
    IFramework getFramework();

    /**
     * A getter for the ready status.
     *
     * @return The ready status
     */
    boolean isReady();

    /**
     * A getter for the status of the worker.
     *
     * @return status
     */
    WorkerStatus getStatus();

    /**
     * A getter for the configuration of the worker.
     *
     * @return configuration
     */
    WorkerConfiguration getConfiguration();

    /**
     * Adds a statusChangeListener
     *
     * @param listener listener
     */
    void addStatusChangeListener(IStatusChangeListener listener);

    /**
     * Removes a statusChangeListener
     *
     * @param listener listener
     */
    void removeStatusChangeListener(IStatusChangeListener listener);
}