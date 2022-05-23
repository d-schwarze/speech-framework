package de.speech.dev.builder.taskbuilder;

import de.speech.core.task.implementation.Task;
import de.speech.core.task.implementation.audioRequest.DeveloperAudioRequest;
import de.speech.dev.builder.taskbuilder.implementation.DeveloperTaskBuilderWithRequests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestDeveloperTaskBuilderWithRequests {
    private DeveloperTaskBuilderWithRequests builder;
    private String userDir = System.getProperty("user.dir");

    @BeforeEach
    void setup() {
        builder = new DeveloperTaskBuilderWithRequests();
    }

    @Test
    void testAddAudioInputStreamWithText() {
        File audioFile = new File(userDir + File.separator + "src/test/resources/AudioFiles/SubDir2/request1_subDir2.wav");
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(audioFile);
            Task task = builder.addAudioInputStreamWithActualText(audio, "test").buildTask();
            DeveloperAudioRequest devAudio = (DeveloperAudioRequest) task.getAudioRequests().get(0);

            assert(devAudio.getActualText().equals("test"));
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testAddAudioInputStream() {
        File audioFile = new File(userDir + File.separator + "src/test/resources/AudioFiles/SubDir2/request1_subDir2.wav");
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(audioFile);
            Task task = builder.addAudioInputStream(audio).buildTask();
            DeveloperAudioRequest devAudio = (DeveloperAudioRequest) task.getAudioRequests().get(0);

            assert(devAudio.getActualText().equals(""));
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }
}
