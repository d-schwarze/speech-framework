package de.speech.worker.local;

import de.speech.core.dispatcher.IFrameworkResult;

/**
 * The handler interface for receiving the result of a {@linkplain de.speech.core.dispatcher.IWorkerAudioRequest}
 */
public interface IResultHandler {

    /**
     * Invoked when a result is ready
     *
     * @param result the finished result
     */
    void handleResult(IFrameworkResult result);

}
