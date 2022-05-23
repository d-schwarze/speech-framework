package de.speech.core.dispatcher;

import de.speech.core.dispatcher.implementation.RequestUtilsTest;
import de.speech.core.task.IAudioRequest;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class AudioRequestMock implements IAudioRequest {

    private final String file;
    private final int id;

    public AudioRequestMock(String file, int id) {
        this.id = id;
        this.file = file;
    }

    @Override
    public AudioInputStream getAudio() throws IOException, UnsupportedAudioFileException {
        return AudioSystem.getAudioInputStream(RequestUtilsTest.class.getResource(file));
    }

    @Override
    public long getRequestId() {
        return id;
    }
}
