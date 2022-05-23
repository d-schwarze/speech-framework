package de.speech.dev.builder.taskbuilder;

import de.speech.core.task.IAudioRequest;
import de.speech.core.task.implementation.Task;
import de.speech.core.task.implementation.audioRequest.DeveloperAudioRequest;
import de.speech.dev.builder.taskbuilder.implementation.DeveloperTaskBuilderWithPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestDeveloperTaskBuilderWithPath {
    private DeveloperTaskBuilderWithPath builder;
    private String userDir = System.getProperty("user.dir") + File.separator;


    @BeforeEach
    void setup() {
        this.builder = new DeveloperTaskBuilderWithPath("src/test/resources/AudioFiles", userDir + "src/test/resources/ActualTexts/requestTexts.txt");
    }

    @Test
    void testSetTextPath() {
        builder.setTextPath(userDir + "src/test/resources/ActualTexts/requestTextsCopy.txt");
        Task task = builder.buildTask();
        assert(task.getAudioRequests().size() == 6);
    }

    @Test
    void testAddingRequestWithoutActualText() {
        Task task = builder.buildTask();
        List<IAudioRequest> requests = task.getAudioRequests();
        boolean hasNoActualText = false;
        for(IAudioRequest request: requests) {
            DeveloperAudioRequest devRequest = (DeveloperAudioRequest) request;
            if (devRequest.getActualText().equals("")) {
                hasNoActualText = true;
                break;
            }
        }
        assert(hasNoActualText);
    }

    @Test
    void testSetAudioFilePath() {
        builder.setAudioRequestPath(userDir + "src/test/resources/AudioFiles/SubDir2");

        Task task = builder.buildTask();
        assert(task.getAudioRequests().size() == 1);
    }

    @Test
    void testAddingSingleAudioRequest() {
        builder.setAudioRequestPath(userDir + "src/test/resources/AudioFiles/request1.wav");
        Task task = builder.buildTask();
        assert (task.getAudioRequests().size() == 1);
    }
}
