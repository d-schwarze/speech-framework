package de.speech.core.dispatcher.implementation.requestresult;

import de.speech.core.dispatcher.IFrameworkResult;
import de.speech.core.task.result.ISpeechToTextServiceData;


public class FrameworkResult implements IFrameworkResult {

    private final ISpeechToTextServiceData result;
    private final long requestId;

    /**
     * Creates a new FrameworkResult with the requestId and the result.
     *
     * @param requestId id of the associated request
     * @param result    result fo the framework
     */
    public FrameworkResult(long requestId, ISpeechToTextServiceData result) {
        this.result = result;
        this.requestId = requestId;
    }

    @Override
    public long getRequestId() {
        return requestId;
    }

    @Override
    public ISpeechToTextServiceData getData() {
        return result;
    }
}
