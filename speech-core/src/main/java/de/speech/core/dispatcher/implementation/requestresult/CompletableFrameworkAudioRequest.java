package de.speech.core.dispatcher.implementation.requestresult;

import de.speech.core.dispatcher.ICompletableFrameworkAudioRequest;
import de.speech.core.dispatcher.IFrameworkResult;
import de.speech.core.dispatcher.IWorkerAudioRequest;

import java.util.concurrent.CompletableFuture;

/**
 * Implementation of a CompletableFrameworkAudioRequest
 */
public class CompletableFrameworkAudioRequest implements ICompletableFrameworkAudioRequest {

    private final CompletableFuture<IFrameworkResult> future;
    private final IWorkerAudioRequest request;

    public CompletableFrameworkAudioRequest(CompletableFuture<IFrameworkResult> future, IWorkerAudioRequest request) {
        this.request = request;
        this.future = future;
    }

    @Override
    public CompletableFuture<IFrameworkResult> getCompletableFuture() {
        return future;
    }

    @Override
    public IWorkerAudioRequest getWorkerAudioRequest() {
        return request;
    }
}
