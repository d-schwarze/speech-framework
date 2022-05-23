package de.speech.core.dispatcher.errorhandler;

import de.speech.core.dispatcher.IWorkerCore;

/**
 * Handles initialization errors.
 *
 * @param <W> worker
 * @param <T> error
 */
public interface IWorkerInitializeErrorHandler<W extends IWorkerCore, T extends Throwable> {

    /**
     * Handles initialization errors
     *
     * @param worker worker with error
     * @param error  error
     */
    void handleError(W worker, T error);
}
