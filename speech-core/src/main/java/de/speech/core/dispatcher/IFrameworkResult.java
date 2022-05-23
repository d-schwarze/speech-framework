package de.speech.core.dispatcher;

import de.speech.core.task.result.ISpeechToTextServiceData;

/**
 * This interfaces defines an object that stores the result of one ITextToSpeechService for one IAudioRequest.
 * Used to transfer data from Worker to Core.
 */
public interface IFrameworkResult {

    /**
     * A getter for the id of the IAudioRequest.
     *
     * @return The id of the request.
     */
    long getRequestId();

    /**
     * A getter for the ISpeechToTextServiceData.
     *
     * @return The metadata for the request.
     */
    ISpeechToTextServiceData getData();
}
