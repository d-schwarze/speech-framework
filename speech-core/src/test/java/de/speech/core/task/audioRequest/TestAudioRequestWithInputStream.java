package de.speech.core.task.audioRequest;

import de.speech.core.task.implementation.audioRequest.AudioRequestWithInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestAudioRequestWithInputStream {
    private AudioRequestWithInputStream request;


    @BeforeEach
    void setup() {
        try {
            File audioFile = new File(System.getProperty("user.dir") + File.separator + "src/test/resources/audioRequests/AudioFiles/request1.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            request = new AudioRequestWithInputStream(0, audioStream);
        } catch (UnsupportedAudioFileException e1) {
            fail(e1.getMessage());
        } catch (IOException e2) {
            fail(e2.getMessage());
        }
    }

    @Test
    void testGetAudio() {
        AudioInputStream audioStream = request.getAudio();
    }
}
