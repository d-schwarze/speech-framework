package de.speech.core.dispatcher;

import de.speech.core.task.IAudioRequest;

import java.util.List;

/**
 * A request for one worker.
 */
public interface IWorkerAudioRequest {

    /**
     * A getter for the associated {@linkplain IAudioRequest}.
     * @return request
     */
    IAudioRequest getRequest();

    /**
     * A getter for the list of preprocesses.
     * @return list with preprocesses.
     */
    List<String> getPreProcesses();

    /**
     * A getter for the unique id.
     * @return id
     */
    long getId();
}
