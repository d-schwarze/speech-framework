package de.speech.core.dispatcher;

import de.speech.core.application.configuration.WorkerConfiguration;

/**
 * Creates a new {@linkplain IWorkerCore} from config object.
 */
public interface IWorkerCoreFactory {

    /**
     * Create a new {@linkplain IWorkerCore}.
     *
     * @param config location of the worker
     * @return worker
     */
    IWorkerCore createWorkerCore(WorkerConfiguration config);
}
