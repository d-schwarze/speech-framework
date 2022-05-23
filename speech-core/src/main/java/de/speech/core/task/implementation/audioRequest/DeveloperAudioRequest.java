package de.speech.core.task.implementation.audioRequest;

import de.speech.core.task.IAudioRequest;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

/**
 * A sort of Decorator for an AudioRequest.
 * Extends the AudioRequest to hold a String that defines what the text of the AudioInputStream is.
 */
public class DeveloperAudioRequest extends AbstractAudioRequest {
    private IAudioRequest audioRequest;
    private String actualText;

    /**
     * Builder for the AudioRequestWithInputStream
     * @param audioRequest the AudioRequest that should be decorated
     * @param actualText the String that defines what the AudioInputStream of the audioRequest says.
     */
    public DeveloperAudioRequest(IAudioRequest audioRequest, String actualText) {
        super(audioRequest.getRequestId());
        this.audioRequest = audioRequest;
        this.actualText = actualText;
    }

    /**
     * Getter for the actual text
     * @return the actual text of the AudioInputStream of the Request
     */
    public String getActualText() {
        return actualText;
    }


    /**
     * Calculates the AudioInputStream of the Request
     * @return the AudioInputStream of the Request
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    @Override
    public AudioInputStream getAudio() throws IOException, UnsupportedAudioFileException {
        return audioRequest.getAudio();
    }
}
