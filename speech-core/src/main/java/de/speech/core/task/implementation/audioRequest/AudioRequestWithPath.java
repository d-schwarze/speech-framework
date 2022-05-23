package de.speech.core.task.implementation.audioRequest;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * An implementation of AbstractAudioRequest that uses a Path to a File to compile a AudioInputStream
 */
public class AudioRequestWithPath extends AbstractAudioRequest{
    private String path;

    /**
     * Builder for the AudioRequestWithPath
     * @param id the id of the Request
     * @param path the String that defines the Path to the File that is the AudioInputStream of the Request
     */
    public AudioRequestWithPath(long id, String path) {
        super(id);
        this.path = path;
    }

    /**
     * Calculates the AudioInputStream of the Request
     * @return the AudioInputStream of teh Request
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    @Override
    public AudioInputStream getAudio() throws IOException, UnsupportedAudioFileException {
        File file = new File(path);
        return AudioSystem.getAudioInputStream(file);
    }
}
