package de.speech.core.task.result.implementation;

import de.speech.core.task.IAudioRequest;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ISpeechToTextServiceData;
import de.speech.core.task.result.ITaskResult;

import java.util.List;

public class AudioRequestResult implements IAudioRequestResult {

    private ITaskResult taskResult;
    private final List<ISpeechToTextServiceData> results;
    private final IAudioRequest request;
    private final RequestResultStatus status;

    public AudioRequestResult(List<ISpeechToTextServiceData> results, IAudioRequest request, RequestResultStatus status) {
        this.results = results;
        this.request = request;
        this.status = status;
    }

    @Override
    public IAudioRequest getRequest() {
        return request;
    }

    @Override
    public List<ISpeechToTextServiceData> getResults() {
        return results;
    }

    @Override
    public RequestResultStatus getStatus() {
        return status;
    }
}
