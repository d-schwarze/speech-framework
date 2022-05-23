package de.speech.core.dispatcher;

import de.speech.core.framework.IFramework;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Used to dispatch a {@linkplain IWorkerAudioRequest}. All workers have to use the same framework and model.
 * Saves the requests until a worker has free places.
 */
public interface IFrameWorkDispatcher {

    /**
     * Adds an audio request to a queue for the workers. If the queue is full and the calling thread is blocked.
     *
     * @param request request
     * @return Future with the result.
     */
    Future<IFrameworkResult> dispatchRequest(IWorkerAudioRequest request);

    /**
     * A getter for all workers with this {@linkplain IFramework}
     *
     * @return list with all frameworks.
     */
    List<IWorkerCore> getWorkers();

    /**
     * Adds one worker to this frameworkDispatcher.
     *
     * @param worker worker
     */
    void addWorker(IWorkerCore worker);

    /**
     * Removes one worker from this dispatcher
     *
     * @param worker worker
     */
    void removeWorker(IWorkerCore worker);

    /**
     * A getter for the Framework.
     *
     * @return framework
     */
    IFramework getFramework();
}
