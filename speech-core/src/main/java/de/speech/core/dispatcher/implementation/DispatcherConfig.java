package de.speech.core.dispatcher.implementation;

import de.speech.core.dispatcher.IWorkerCoreFactory;

/**
 * The configuration for the dispatcher.
 */
public class DispatcherConfig {

    protected final IWorkerCoreFactory workerCoreFactory;

    public DispatcherConfig(IWorkerCoreFactory factory) {
        this.workerCoreFactory = factory;
    }

    /**
     * A getter for the workerFactory.
     *
     * @return factory
     */
    public IWorkerCoreFactory getWorkerCoreFactory() {
        return workerCoreFactory;
    }
}
