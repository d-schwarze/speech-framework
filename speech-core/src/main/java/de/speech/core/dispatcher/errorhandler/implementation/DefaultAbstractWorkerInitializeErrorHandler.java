package de.speech.core.dispatcher.errorhandler.implementation;

import de.speech.core.dispatcher.IWorkerCore;
import de.speech.core.dispatcher.errorhandler.IWorkerInitializeErrorHandler;
import de.speech.core.dispatcher.implementation.AbstractWorker;
import de.speech.core.logging.SpeechLogging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The default initialization error handler for {@linkplain AbstractWorker}s. Terminates the worker.
 */
public class DefaultAbstractWorkerInitializeErrorHandler<W extends IWorkerCore, T extends Throwable> implements IWorkerInitializeErrorHandler<W, T> {

    private final Logger logger = SpeechLogging.getLogger();

    @Override
    public void handleError(W worker, T error) {
        logger.warning("error occurred during worker initialization: " + error.getMessage());
        try {
            worker.stop();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.log(Level.WARNING, "cant terminate worker " + worker.getConfiguration().getLocation(), e);
        }
    }
}
