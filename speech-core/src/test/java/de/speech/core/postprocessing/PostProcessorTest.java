package de.speech.core.postprocessing;

import de.speech.core.postprocessing.mocks.MockAudioRequestResult;
import de.speech.core.postprocessing.mocks.MockTaskResult;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ITaskResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostProcessorTest {

    private ITaskResult<IAudioRequestResult> taskResult;
    private PostProcessor postProcessor;
    private List<IPostProcessFactory> postProcessFactories;

    @BeforeEach
    public final void initializeTestSuit() {
        postProcessFactories = new ArrayList<>();
        postProcessFactories.add(new TestPostProcessFactory());
        taskResult = new MockTaskResult();
    }

    @Test
    public final void testConstructor_Null() {
        postProcessor = new PostProcessor(null);

        assertTrue(postProcessor.getPostProcessFactories().size() > 0);
    }

    @Test
    public final void testConstructor_EmptyList() {
        postProcessor = new PostProcessor(new ArrayList<>());

        assertTrue(postProcessor.getPostProcessFactories().size() > 0);
    }

    @Test
    public final void testProcessAll_EmptyTaskResult() {
        postProcessor = new PostProcessor(postProcessFactories);

        int expectedSize = taskResult.getResults().size();
        int actualSize = postProcessor.processAll(taskResult).getResults().size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    public final void testProcessAll_FilledTaskResult() {
        taskResult.getResults().add(new MockAudioRequestResult());
        taskResult.getResults().add(new MockAudioRequestResult());
        postProcessor = new PostProcessor(postProcessFactories);

        int expectedSize = taskResult.getResults().size();
        int actualSize = postProcessor.processAll(taskResult).getResults().size();

        assertEquals(expectedSize, actualSize);
    }




}
