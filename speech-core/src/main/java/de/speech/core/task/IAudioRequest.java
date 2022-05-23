package de.speech.core.task;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

/**
 * This interface defines an object which stores an AudioInputStream and identifier to distinguish which ITask the request is part of.
 */
public interface IAudioRequest {

    /**
     * A getter for the AudioInputStream that should be analysed later.
     * @return The AudioInputStream of the instance.
     */
    AudioInputStream getAudio() throws IOException, UnsupportedAudioFileException;

    /**
     * A getter for the id of the instance (only unique for the task).
     * @return The id of the instance.
     */
    long getRequestId();
}