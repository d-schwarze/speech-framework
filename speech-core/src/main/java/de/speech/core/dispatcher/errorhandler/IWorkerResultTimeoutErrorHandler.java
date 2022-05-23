package de.speech.core.dispatcher.errorhandler;


import de.speech.core.dispatcher.ICompletableFrameworkAudioRequest;
import de.speech.core.dispatcher.IWorkerCore;

/**
 * Handles result timeout errors.
 */
public interface IWorkerResultTimeoutErrorHandler {

    /**
     * Handles a timeout error, when a result is not finished in time.
     *
     * @param request request, which result is not finished
     * @param worker  worker with the request
     */
    void handleTimeoutError(ICompletableFrameworkAudioRequest request, IWorkerCore worker);
}
