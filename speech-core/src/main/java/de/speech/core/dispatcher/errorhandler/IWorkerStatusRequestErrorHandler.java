package de.speech.core.dispatcher.errorhandler;

import de.speech.core.dispatcher.IWorkerCore;

/**
 * Handles status request errors.
 *
 * @param <W> worker
 */
public interface IWorkerStatusRequestErrorHandler<W extends IWorkerCore, T extends Throwable> {

    /**
     * Handles a status request error.
     *
     * @param worker worker with error
     * @param error  error
     */
    void handleStatusError(W worker, T error);
}
