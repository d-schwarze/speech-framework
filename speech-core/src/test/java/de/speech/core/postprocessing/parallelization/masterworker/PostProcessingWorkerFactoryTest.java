package de.speech.core.postprocessing.parallelization.masterworker;

import de.speech.core.postprocessing.IPostProcessFactory;
import de.speech.core.postprocessing.TestPostProcessFactory;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostProcessingWorkerFactoryTest {

    private List<IPostProcessFactory> postProcessesFactories;
    private BlockingQueue<IAudioRequestResult> queue;
    private List<FinalAudioRequestResult> finalAudioRequestResults;


    @BeforeEach
    public final void initializeTestSuit() {
        postProcessesFactories = new ArrayList<>();
        postProcessesFactories.add(new TestPostProcessFactory());

        queue = new LinkedBlockingQueue<>();

        finalAudioRequestResults = new ArrayList<>();
    }

    @Test
    public final void testCreateWorker() {
        PostProcessingWorker worker = PostProcessingWorkerFactory.createWorker(queue, finalAudioRequestResults, postProcessesFactories);
        assertEquals(postProcessesFactories.get(0).createPostProcess().getName(),
                worker.getPostProcesses().get(0).getName());
    }

}
