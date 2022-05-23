package de.speech.core.task.audioRequest;

import de.speech.core.task.implementation.audioRequest.AudioRequestWithPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestAudioRequestWithPath {
    private String pathToFile = System.getProperty("user.dir") + File.separator + "src/test/resources/audioRequests/AudioFiles/request1.wav";
    private AudioRequestWithPath request;

    @BeforeEach
    void setUp() {
        request = new AudioRequestWithPath(0, pathToFile);
    }

    @Test
    void testGetAudio() {
        try {
            request.getAudio();
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }

    }
}
