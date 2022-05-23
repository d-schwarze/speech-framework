package de.speech.core.postprocessing.parallelization.masterworker;

import de.speech.core.postprocessing.IPostProcessFactory;
import de.speech.core.postprocessing.TestPostProcessFactory;
import de.speech.core.postprocessing.mocks.MockAudioRequestResult;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParallelizationWithMasterWorkerTest {
    private final List<IPostProcessFactory> postProcessFactories = new ArrayList<>();
    private ParallelizationWithMasterWorker parallelizationWithMasterWorker;
    private final List<IAudioRequestResult> audioRequestResults = new ArrayList<>();


    @BeforeEach
    public final void initializeTestSuit() {

        postProcessFactories.clear();
        audioRequestResults.clear();

        parallelizationWithMasterWorker = new ParallelizationWithMasterWorker();
    }

    @Test
    public final void testExecutePostProcessing_EmptyLists() {
        int expectedSize = audioRequestResults.size();

        int actualSize = parallelizationWithMasterWorker.executePostProcessing(audioRequestResults, postProcessFactories).size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    public final void testExecutePostProcessing_FilledLists() {
        audioRequestResults.add(new MockAudioRequestResult());
        postProcessFactories.add(new TestPostProcessFactory());

        int expectedSize = audioRequestResults.size();

        int actualSize = parallelizationWithMasterWorker.executePostProcessing(audioRequestResults, postProcessFactories).size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    public final void testExecutePostProcessing_BigLists() {
        int numberOfResults = 100000;

        for (int i = 0; i < numberOfResults; i++) {
            audioRequestResults.add(new MockAudioRequestResult());
        }

        postProcessFactories.add(new TestPostProcessFactory());
        postProcessFactories.add(new TestPostProcessFactory());

        int expectedSize = audioRequestResults.size();

        List<FinalAudioRequestResult> finalAudioRequestResults = parallelizationWithMasterWorker.executePostProcessing(audioRequestResults, postProcessFactories);

        int actualSize = finalAudioRequestResults.size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    public final void testExecutePostProcessing_WithExample() {
        IPostProcessFactory factory = new TestPostProcessFactory();
        postProcessFactories.add(factory);

        IAudioRequestResult audioRequestResult = new MockAudioRequestResult();
        audioRequestResults.add(audioRequestResult);

        List<FinalAudioRequestResult> finalAudioRequestResults =
                parallelizationWithMasterWorker.executePostProcessing(audioRequestResults, postProcessFactories);


        assertEquals(factory.createPostProcess().process(audioRequestResult.getResults()),
                finalAudioRequestResults.get(0).getPostProcessingResults().get(factory.createPostProcess().getName()));

    }
}
