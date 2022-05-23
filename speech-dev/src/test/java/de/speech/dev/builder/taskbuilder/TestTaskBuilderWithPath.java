package de.speech.dev.builder.taskbuilder;

import de.speech.core.task.implementation.Task;
import de.speech.dev.builder.taskbuilder.implementation.TaskBuilderWithPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestTaskBuilderWithPath {
    private TaskBuilderWithPath builder;
    private String userDir = System.getProperty("user.dir") + File.separator;

    @BeforeEach
    void setup() {
        this.builder = new TaskBuilderWithPath(userDir+File.separator+"src/test/resources/AudioFiles");
    }

    @Test
    void testAddingSingleAudioRequest() {
        builder.setAudioRequestPath(userDir + "src/test/resources/AudioFiles/request1.wav");
        Task task = builder.buildTask();
        assert (task.getAudioRequests().size() == 1);
    }

    @Test
    void testNormalCreation() {
        Task task = builder.buildTask();
        assert (task.getAudioRequests().size() == 6);
    }

    @Test
    void testSetPath() {
        builder.setAudioRequestPath(userDir+File.separator+"src/test/resources/AudioFiles/SubDir2");
        Task task = builder.buildTask();
        assert(task.getAudioRequests().size() == 1);
    }
}
