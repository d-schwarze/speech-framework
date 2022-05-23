package de.speech.core.dispatcher.linking;

import de.speech.core.dispatcher.IFrameworkResult;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.ITask;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ITaskResult;

import java.util.concurrent.Future;

/**
 * The interface for building a TaskResult. If the task result is complete, the asynchronous computation
 * is finished and a caller can get the {@code ITaskResult}.
 */
public interface ITaskResultBuilder extends Future<ITaskResult<IAudioRequestResult>> {

    /**
     * A getter for the {@code ITask}.
     *
     * @return associated Task
     */
    ITask getTask();

    /**
     * Adds one request with all futures of the frameworks to the builder
     *
     * @param result  results of the frameworks
     * @param request request
     */
    void addRequestResult(Future<IFrameworkResult>[] result, IAudioRequest request);

    /**
     * Completes the builder with an error. If it is already completed it returns false.
     *
     * @param t error
     * @return true if could completed.
     */
    boolean completeExceptionally(Throwable t);
}
