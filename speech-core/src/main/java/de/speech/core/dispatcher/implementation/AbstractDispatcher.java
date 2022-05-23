package de.speech.core.dispatcher.implementation;

import de.speech.core.application.configuration.WorkerConfiguration;
import de.speech.core.dispatcher.IDispatcher;

public abstract class AbstractDispatcher implements IDispatcher {

    /**
     * Initializes the given worker.
     *
     * @param workers workers
     */
    public abstract void initializeWorker(WorkerConfiguration... workers);

    /**
     * Stops the worker with the given configuration.
     *
     * @param config Configuration of the worker
     */
    public abstract void stopWorker(WorkerConfiguration config) throws Exception;

    /**
     * Stops all workers
     *
     * @throws Exception if the workers can not terminated
     */
    public abstract void stopAll() throws Exception;
}
