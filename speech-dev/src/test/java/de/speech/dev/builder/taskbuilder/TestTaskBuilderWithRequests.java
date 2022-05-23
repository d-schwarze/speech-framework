package de.speech.dev.builder.taskbuilder;

import de.speech.core.task.implementation.Task;
import de.speech.core.task.implementation.audioRequest.AudioRequestWithInputStream;
import de.speech.dev.builder.taskbuilder.implementation.TaskBuilderWithRequests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestTaskBuilderWithRequests {
    private TaskBuilderWithRequests builder;
    private String userDir = System.getProperty("user.dir") + File.separator;

    @BeforeEach
    void setup() {
        this.builder = new TaskBuilderWithRequests();
    }

    @Test
    void testAddAudioInputStream() throws IOException, UnsupportedAudioFileException {
        AudioInputStream audio1 = AudioSystem.getAudioInputStream(new File(userDir + "src/test/resources/AudioFiles/request1.wav"));

        Task task = builder.addAudioInputStream(audio1).buildTask();
        assert(!task.getAudioRequests().isEmpty());
    }

    @Test
    void testBuildAudioRequests() throws IOException, UnsupportedAudioFileException {
        AudioInputStream audio1 = AudioSystem.getAudioInputStream(new File(userDir + "src/test/resources/AudioFiles/request1.wav"));
        AudioInputStream audio2 = AudioSystem.getAudioInputStream(new File(userDir + "src/test/resources/AudioFiles/request2.wav"));

        Task task = builder.addAudioInputStream(audio1).addAudioInputStream(audio2).buildTask();
        assert(task.getAudioRequests().get(0).getClass().equals(AudioRequestWithInputStream.class));
        assert(task.getAudioRequests().get(1).getClass().equals(AudioRequestWithInputStream.class));
    }
}
