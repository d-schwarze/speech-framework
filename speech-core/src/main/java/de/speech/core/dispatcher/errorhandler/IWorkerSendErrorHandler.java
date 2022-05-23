package de.speech.core.dispatcher.errorhandler;

import de.speech.core.dispatcher.ICompletableFrameworkAudioRequest;
import de.speech.core.dispatcher.IWorkerCore;

/**
 * Handles Errors during sending of workers.
 */
public interface IWorkerSendErrorHandler<W extends IWorkerCore, T extends Throwable> {

    /**
     * Handles a error occurring during a send operation.
     *
     * @param worker   The worker with the error.
     * @param requests The requests in the queue of the worker.
     * @param error    The error.
     */
    void errorOnSend(W worker, ICompletableFrameworkAudioRequest requests, T error);
}
