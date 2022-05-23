package de.speech.core.postprocessing.mocks;

import de.speech.core.task.IAudioRequest;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ISpeechToTextServiceData;
import de.speech.core.task.result.implementation.RequestResultStatus;

import java.util.List;

public class MockAudioRequestResult implements IAudioRequestResult {

    @Override
    public IAudioRequest getRequest() {
        return null;
    }

    @Override
    public List<ISpeechToTextServiceData> getResults() {
        return null;
    }

    @Override
    public RequestResultStatus getStatus() {
        return null;
    }
}
