package de.speech.core.dispatcher.linking.implementation;

import de.speech.core.dispatcher.IFrameworkResult;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ISpeechToTextServiceData;
import de.speech.core.task.result.implementation.AudioRequestResult;
import de.speech.core.task.result.implementation.RequestResultStatus;
import de.speech.core.task.result.implementation.ResultStatus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RequestResultBuilder {

    private final IAudioRequest request;
    private final Future<IFrameworkResult>[] results;

    /**
     * Create a new builder.
     *
     * @param request request
     * @param result  results
     */
    public RequestResultBuilder(IAudioRequest request, Future<IFrameworkResult>[] result) {
        this.request = request;
        this.results = result;
    }

    /**
     * A Getter for the request.
     *
     * @return request
     */
    public IAudioRequest getRequest() {
        return request;
    }

    /**
     * Returns true if all futures are completed.
     *
     * @return true if all futures are completed.
     */
    public boolean isDone() {
        for (Future<IFrameworkResult> result : results) {
            if (!result.isDone()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Creates the {@linkplain IAudioRequestResult}. Waits if not all futures are done yet.
     *
     * @return result of the request.
     * @throws InterruptedException If the thread is interrupted during waiting.
     */
    public IAudioRequestResult createRequestResult() throws InterruptedException {
        List<Throwable> errors = new LinkedList<>();
        ResultStatus[] resultStatuses = new ResultStatus[results.length];
        List<ISpeechToTextServiceData> data = new ArrayList<>();
        for (int i = 0; i < results.length; i++) {
            try {
                data.add(results[i].get().getData());
                resultStatuses[i] = ResultStatus.PROCESSED;
            } catch (ExecutionException e) {
                data.add(null);
                errors.add(e.getCause());
                resultStatuses[i] = ResultStatus.NOT_PROCESSED;
            }
        }
        RequestResultStatus status = new RequestResultStatus(resultStatuses, errors);
        return new AudioRequestResult(data, request, status);
    }

    /**
     * Creates the {@linkplain IAudioRequestResult}. Waits the given time if not all futures are done yet.
     *
     * @param time the maximum time to wait
     * @param unit the timeunit
     * @return the result of the request
     * @throws TimeoutException     if wait timed out.
     * @throws InterruptedException if interrupted during waiting.
     */
    public IAudioRequestResult createRequestResult(long time, TimeUnit unit) throws TimeoutException, InterruptedException {
        long start = System.currentTimeMillis();

        List<Throwable> errors = new LinkedList<>();
        ResultStatus[] resultStatuses = new ResultStatus[results.length];
        List<ISpeechToTextServiceData> data = new ArrayList<>();
        for (int i = 0; i < results.length; i++) {
            try {
                long cur = System.currentTimeMillis();
                if (cur - start > time) {
                    throw new TimeoutException();
                }
                long timeout = time - (cur - start);
                data.add(results[i].get(timeout, unit).getData());
                resultStatuses[i] = ResultStatus.PROCESSED;
            } catch (ExecutionException e) {
                data.add(null);
                errors.add(e.getCause());
                resultStatuses[i] = ResultStatus.NOT_PROCESSED;
            }
        }
        RequestResultStatus status = new RequestResultStatus(resultStatuses, errors);
        return new AudioRequestResult(data, request, status);
    }
}
