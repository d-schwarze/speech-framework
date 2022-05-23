package de.speech.core.dispatcher.errorhandler.implementation;

import de.speech.core.dispatcher.IWorkerCore;
import de.speech.core.dispatcher.errorhandler.IWorkerStatusRequestErrorHandler;
import de.speech.core.logging.SpeechLogging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The default statusRequestErrorHandler.
 */
public class DefaultAbstractWorkerStatusRequestErrorHandler<W extends IWorkerCore, T extends Throwable> implements IWorkerStatusRequestErrorHandler<W, T> {

    private final Logger logger = SpeechLogging.getLogger();

    /**
     * Terminates the worker
     *
     * @param worker abstractWorker
     */
    @Override
    public void handleStatusError(W worker, T error) {
        logger.warning("status request error " + error.getMessage());
        try {
            worker.stop();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.log(Level.WARNING, "cant terminate worker " + worker.getConfiguration().getLocation(), e);
        }
    }
}
