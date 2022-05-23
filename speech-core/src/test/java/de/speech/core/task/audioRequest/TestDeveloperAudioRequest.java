package de.speech.core.task.audioRequest;

import de.speech.core.task.implementation.audioRequest.AudioRequestWithInputStream;
import de.speech.core.task.implementation.audioRequest.AudioRequestWithPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestDeveloperAudioRequest {

    @Test
    void testGetAudioWithBothTypes() {
        String userDir = System.getProperty("user.dir") + File.separator;
        String path ="src/test/resources/audioRequests/AudioFiles/request1.wav";
        AudioRequestWithPath requestWithPath = new AudioRequestWithPath(0, path);
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(path));
            AudioRequestWithInputStream requestWithInputStream = new AudioRequestWithInputStream(1, audio);
            assert(requestWithInputStream.getRequestId() == 1);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }

        assertEquals(0, requestWithPath.getRequestId());
    }
}
