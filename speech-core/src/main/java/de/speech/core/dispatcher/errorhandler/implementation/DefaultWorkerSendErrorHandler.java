package de.speech.core.dispatcher.errorhandler.implementation;

import de.speech.core.dispatcher.ICompletableFrameworkAudioRequest;
import de.speech.core.dispatcher.IWorkerCore;
import de.speech.core.dispatcher.errorhandler.IWorkerSendErrorHandler;
import de.speech.core.logging.SpeechLogging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This errorhandler completes the {@linkplain de.speech.core.dispatcher.IWorkerAudioRequest} currently processed by this worker with the given exception.
 */
public class DefaultWorkerSendErrorHandler<W extends IWorkerCore, T extends Throwable> implements IWorkerSendErrorHandler<W, T> {

    private final Logger logger = SpeechLogging.getLogger();

    /**
     * Completes the request with the given error.
     *
     * @param worker  worker
     * @param request request
     * @param error   error
     */
    @Override
    public void errorOnSend(W worker, ICompletableFrameworkAudioRequest request, T error) {
        logger.log(Level.WARNING, "error on send requestId: "+request.getWorkerAudioRequest().getRequest().getRequestId(), error);
        request.getCompletableFuture().completeExceptionally(error);
    }
}