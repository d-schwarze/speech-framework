package de.speech.core.task.implementation.audioRequest;

import de.speech.core.task.IAudioRequest;

/**
 * Abstract class that implements the fundamental functions of every IAudioRequest
 */
public abstract class AbstractAudioRequest implements IAudioRequest {
    private long id;

    /**
     * Getter for the RequestId
     * @return the requestId
     */
    @Override
    public long getRequestId() {
        return id;
    }

    /**
     * Builder for an abstract AudioRequest
     * @param requestId the id of the newly created Request
     */
    public AbstractAudioRequest(long requestId) {
        this.id = requestId;
    }

}
