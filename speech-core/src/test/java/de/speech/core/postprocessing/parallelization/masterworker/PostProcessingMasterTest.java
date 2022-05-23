package de.speech.core.postprocessing.parallelization.masterworker;

import de.speech.core.postprocessing.IPostProcessFactory;
import de.speech.core.postprocessing.TestPostProcessFactory;
import de.speech.core.postprocessing.mocks.MockAudioRequestResult;
import de.speech.core.task.result.IAudioRequestResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PostProcessingMasterTest {

    private PostProcessingMaster master;

    private List<IPostProcessFactory> postProcessFactories;

    private IAudioRequestResult audioRequestResult;


    @BeforeEach
    public final void initializeTestSuit() {
        postProcessFactories = new ArrayList<>();
        postProcessFactories.add(new TestPostProcessFactory());

        audioRequestResult = new MockAudioRequestResult();

        master = new PostProcessingMaster(2, postProcessFactories);
    }

    @Test
    public final void testConstructor_WorkerCountZero() {
        assertThrows(IllegalArgumentException.class, () -> new PostProcessingMaster(0, postProcessFactories));
    }

    @Test
    public final void testSubmit() {
        master.submit(audioRequestResult);

        assertFalse(master.isComplete());
    }

    @Test
    public final void testSubmit_Null() {

        master.submit(null);

        assertTrue(master.isComplete());
    }


    @Test
    public final void testExecute_waitOnFinish() {
        master.submit(audioRequestResult);
        master.submit(audioRequestResult);

        master.execute();
        master.waitOnFinish();

        assertTrue(master.isComplete());
        assertEquals(2, master.getResults().size());
    }

    @Test
    public final void testIsComplete() {
        assertTrue(master.isComplete());

        master.submit(audioRequestResult);

        assertFalse(master.isComplete());

        master.execute();
        master.waitOnFinish();

        assertTrue(master.isComplete());
    }

}
