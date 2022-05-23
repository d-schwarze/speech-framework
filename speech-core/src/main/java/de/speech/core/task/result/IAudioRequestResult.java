package de.speech.core.task.result;

import de.speech.core.task.IAudioRequest;
import de.speech.core.task.result.implementation.RequestResultStatus;

import java.util.List;

/**
 * This interface defines an object that stores all ISpeechToTextServiceData of one IAudioRequest.
 */
public interface IAudioRequestResult {

    /**
     * A getter for the request of the IAudioRequestResult
     * @return The IAudioRequest.
     */
    IAudioRequest getRequest();

    /**
     * A getter for the List of ISpechToTextServiceData.
     * @return The List of ISpeechToTextServiceData objects for the specific IAudioRequest.
     */
    List<ISpeechToTextServiceData> getResults();

    RequestResultStatus getStatus();
}
