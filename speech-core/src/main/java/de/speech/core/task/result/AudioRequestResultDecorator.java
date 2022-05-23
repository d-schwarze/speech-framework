package de.speech.core.task.result;

import de.speech.core.task.IAudioRequest;
import de.speech.core.task.result.implementation.RequestResultStatus;

import java.util.List;

/**
 * This is a decorator of the interface {@link IAudioRequestResult}
 * By Extending this class, functionality can be added to IAudioRequestResult.
 * Useful f.e. for postprocessing.
 */
public abstract class AudioRequestResultDecorator implements IAudioRequestResult {

    private final IAudioRequestResult audioRequestResultToBeDecorated;

    public AudioRequestResultDecorator(IAudioRequestResult audioRequestResultToBeDecorated) {
        this.audioRequestResultToBeDecorated = audioRequestResultToBeDecorated;
    }

    @Override
    public IAudioRequest getRequest() {
        return audioRequestResultToBeDecorated.getRequest();
    }

    @Override
    public List<ISpeechToTextServiceData> getResults() {
        return audioRequestResultToBeDecorated.getResults();
    }

    @Override
    public RequestResultStatus getStatus() {
        return audioRequestResultToBeDecorated.getStatus();
    }
}
