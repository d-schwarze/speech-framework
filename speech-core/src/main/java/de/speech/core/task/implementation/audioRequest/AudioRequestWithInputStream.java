package de.speech.core.task.implementation.audioRequest;

import javax.sound.sampled.AudioInputStream;

/**
 * An implementation of AbstractAudioRequest that uses AudioInputStream Objects to calculate the getAudio() method
 */
public class AudioRequestWithInputStream extends AbstractAudioRequest{
    private AudioInputStream audioInputStream;

    /**
     * Builder for the AudioRequestWithInputStream
     * @param id the id of the Request
     * @param audioInputStream the inputStream of the Request
     */
    public AudioRequestWithInputStream(long id, AudioInputStream audioInputStream) {
        super(id);
        this.audioInputStream = audioInputStream;
    }

    /**
     * Returns the AudioInputStream of the Request
     * @return the AudioInputStream of the Task
     */
    @Override
    public AudioInputStream getAudio() {
        return audioInputStream;
    }
}
