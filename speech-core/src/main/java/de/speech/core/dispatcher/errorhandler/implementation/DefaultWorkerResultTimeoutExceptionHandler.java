package de.speech.core.dispatcher.errorhandler.implementation;

import de.speech.core.dispatcher.ICompletableFrameworkAudioRequest;
import de.speech.core.dispatcher.IWorkerCore;
import de.speech.core.dispatcher.errorhandler.IWorkerResultTimeoutErrorHandler;

import java.util.concurrent.TimeoutException;

/**
 * The default error handler for timeouts. Completes the request with a {@linkplain TimeoutException}.
 */
public class DefaultWorkerResultTimeoutExceptionHandler implements IWorkerResultTimeoutErrorHandler {


    /**
     * Completes the requests with a {@linkplain TimeoutException}.
     *
     * @param request request, which result is not finished
     * @param worker  worker with the request
     */
    @Override
    public void handleTimeoutError(ICompletableFrameworkAudioRequest request, IWorkerCore worker) {
        request.getCompletableFuture().completeExceptionally(new TimeoutException());
    }
}
