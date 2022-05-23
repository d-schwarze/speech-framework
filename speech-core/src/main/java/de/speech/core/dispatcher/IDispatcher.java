package de.speech.core.dispatcher;

import de.speech.core.task.ITask;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ITaskResult;

import java.util.concurrent.Future;

/**
 * Interface used to communicate with the dispatcher. Used to dispatch a {@linkplain ITask}.
 */
public interface IDispatcher {

    /**
     * Dispatches the task. Returns a future, that contains the task result.
     * The future is ready, if all requests are computed and added.
     *
     * @param task task
     * @return Future with the {@linkplain ITaskResult}
     */
    Future<ITaskResult<IAudioRequestResult>> dispatchTask(ITask task);
}
