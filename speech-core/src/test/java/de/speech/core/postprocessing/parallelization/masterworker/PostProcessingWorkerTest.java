package de.speech.core.postprocessing.parallelization.masterworker;

import de.speech.core.postprocessing.IPostProcess;
import de.speech.core.postprocessing.TestPostProcess;
import de.speech.core.postprocessing.mocks.MockAudioRequestResult;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostProcessingWorkerTest {

    private PostProcessingWorker worker;
    private List<IPostProcess> postProcesses = new ArrayList<>();
    private BlockingQueue<IAudioRequestResult> queue = new LinkedBlockingQueue<>();
    private List<FinalAudioRequestResult> finalAudioRequestResults = new ArrayList<>();


    @BeforeEach
    public final void initializeTestSuit() {
        finalAudioRequestResults.clear();
        queue.clear();
        postProcesses.clear();
        postProcesses.add(new TestPostProcess());
        postProcesses.add(new TestPostProcess());
    }


    @Test
    public final void testRun() {
        queue.add(new MockAudioRequestResult());
        queue.add(new MockAudioRequestResult());

        worker = new PostProcessingWorker(queue, finalAudioRequestResults, postProcesses);

        int expectedSize = queue.size();

        worker.run();

        int actualSize = finalAudioRequestResults.size();

        assertEquals(expectedSize, actualSize);

        assertTrue(queue.isEmpty());
    }

    @Test
    public final void testRun_EmptyQueue() {
        worker = new PostProcessingWorker(queue, finalAudioRequestResults, postProcesses);

        worker.run();

        assertTrue(finalAudioRequestResults.isEmpty());
    }


    @Test
    public final void testRun_NoPostProcesses() {
        postProcesses.clear();
        queue.add(new MockAudioRequestResult());

        worker = new PostProcessingWorker(queue, finalAudioRequestResults, postProcesses);

        int expectedSize = queue.size();

        worker.run();

        int actualSize = finalAudioRequestResults.size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    public final void testRun_Example() {
        postProcesses.clear();
        IPostProcess postProcess = new TestPostProcess();
        postProcesses.add(postProcess);

        IAudioRequestResult audioRequestResult = new MockAudioRequestResult();
        queue.add(audioRequestResult);

        worker = new PostProcessingWorker(queue, finalAudioRequestResults, postProcesses);

        worker.run();

        assertEquals(postProcess.process(audioRequestResult.getResults()),
                finalAudioRequestResults.get(0).getPostProcessingResults().get(postProcess.getName()));
    }
}
