package de.speech.core.dispatcher.linking.implementation;

import de.speech.core.dispatcher.IFrameworkResult;
import de.speech.core.dispatcher.linking.ITaskResultBuilder;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.ITask;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ITaskResult;
import de.speech.core.task.result.implementation.AudioRequestResult;
import de.speech.core.task.result.implementation.TaskResult;

import java.util.Arrays;
import java.util.concurrent.*;

/**
 * Builds the Task from all futures from the frameworks.
 */
public class TaskResultBuilder implements ITaskResultBuilder {

    private final RequestResultBuilder[] resultBuilders;
    private final int neededResults;
    private final ITask task;
    /**
     * The finished result.
     */
    ITaskResult<IAudioRequestResult> result;
    private int resultsReceived = 0;
    private boolean canceled = false;
    private boolean completedWithException = false;
    private Throwable throwable;

    public TaskResultBuilder(ITask task) {
        this.task = task;
        neededResults = task.getAudioRequests().size();
        this.resultBuilders = new RequestResultBuilder[task.getAudioRequests().size()];
    }

    @Override
    public ITask getTask() {
        return task;
    }

    /**
     * Add one request result.
     *
     * @param result  futures with the frameworkResults
     * @param request request
     */
    public synchronized void addRequestResult(Future<IFrameworkResult>[] result, IAudioRequest request) {
        RequestResultBuilder requestResultBuilder = new RequestResultBuilder(request, result);
        if (resultBuilders[(int) request.getRequestId()] == null) {
            resultsReceived++;
            resultBuilders[(int) request.getRequestId()] = requestResultBuilder;
        }
    }

    @Override
    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        if (!isDone())
            canceled = true;
        notifyAll();
        return canceled;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    /**
     * Returns true if canceled or all requests done.
     *
     * @return true if canceled or all requests done
     */
    @Override
    public boolean isDone() {
        if (canceled) {
            return true;
        }

        if (completedWithException) {
            return true;
        }

        if (result != null) {
            return true;
        }

        if (resultsReceived < neededResults) {
            return false;
        }

        for (RequestResultBuilder builder : resultBuilders) {
            if (!builder.isDone()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Waits til canceled, exceptional completed or all {@linkplain RequestResultBuilder} are finished.
     * Creates the {@linkplain ITaskResult<IAudioRequestResult>} if not yet completed.
     *
     * @return result of the task
     * @throws InterruptedException If interrupted during waiting.
     * @throws ExecutionException   If completed with exception.
     */
    @Override
    public synchronized ITaskResult<IAudioRequestResult> get() throws InterruptedException, ExecutionException {
        while (resultsReceived < neededResults && !canceled && !completedWithException) {
            wait();
        }

        if (canceled) {
            throw new CancellationException();
        } else if (completedWithException) {
            throw new ExecutionException(throwable);
        }

        if (result == null) {
            createTaskResult();
        }

        return result;
    }

    /**
     * Waits for the given time til canceled, exceptional completed or all {@linkplain RequestResultBuilder} are finished.
     *
     * @param timeout the maximum time to wait
     * @param unit    the timeunit
     * @return result of the task
     * @throws InterruptedException if interrupted during waiting.
     * @throws ExecutionException   if the task can not be processed.
     * @throws TimeoutException     if the wait timed out.
     */
    @Override
    public synchronized ITaskResult<IAudioRequestResult> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        long start = System.currentTimeMillis();
        long waited = 0;
        while (resultsReceived < task.getAudioRequests().size() && !canceled && !completedWithException) {
            wait(timeout - waited);
            waited = System.currentTimeMillis() - start;

            if (waited >= timeout) {
                throw new TimeoutException();
            }
        }

        if (canceled) {
            throw new CancellationException();
        } else if (completedWithException) {
            throw new ExecutionException(throwable);
        }

        if (result == null) {
            createTaskResult(unit.toMillis((timeout - waited)));
        }

        return result;
    }


    /**
     * Creates the {@linkplain ITaskResult<IAudioRequestResult>}.
     *
     * @throws InterruptedException If the thread was interrupted during waiting.
     */
    private void createTaskResult() throws InterruptedException {
        IAudioRequestResult[] audioRequests = new AudioRequestResult[task.getAudioRequests().size()];

        for (int r = 0; r < audioRequests.length; r++) {
            RequestResultBuilder builder = resultBuilders[r];
            audioRequests[r] = builder.createRequestResult();
        }

        this.result = new TaskResult(task, Arrays.asList(audioRequests));
    }

    /**
     * Creates the {@linkplain ITaskResult<IAudioRequestResult>}.
     *
     * @param time the maximum time to wait in milliseconds
     * @throws InterruptedException If the thread is interrupted during waiting.
     * @throws TimeoutException     If the wait timed out.
     */
    private void createTaskResult(long time) throws InterruptedException, TimeoutException {
        IAudioRequestResult[] audioRequests = new AudioRequestResult[task.getAudioRequests().size()];

        for (int r = 0; r < audioRequests.length; r++) {
            RequestResultBuilder builder = resultBuilders[r];
            audioRequests[r] = builder.createRequestResult(time, TimeUnit.MILLISECONDS);
        }

        this.result = new TaskResult(task, Arrays.asList(audioRequests));
    }

    /**
     * Completes this future with a {@linkplain Throwable}, if it is not done.
     *
     * @param t throwable
     * @return true if successfully completed with throwable. false if done
     */
    @Override
    public synchronized boolean completeExceptionally(Throwable t) {
        if (isDone()) {
            return false;
        }

        completedWithException = true;
        this.throwable = t;

        return true;
    }
}