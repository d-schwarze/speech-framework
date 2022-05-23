package de.speech.core.task;

import de.speech.core.postprocessing.IPostProcessFactory;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ISpeechToTextServiceData;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResultWithTac;
import de.speech.core.task.result.implementation.FinalTaskResultWithTac;
import de.speech.core.task.result.implementation.RequestResultStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestFinalTaskResultWithTac {
    private FinalTaskResultWithTac taskResult;

    @BeforeEach
    void setUp() {
        List<FinalAudioRequestResultWithTac> requestResults = new ArrayList<>();
        requestResults.add(new FinalAudioRequestResultWithTac(new FinalAudioRequestResult(new MockRequestResult(), null), null));
        requestResults.add(new FinalAudioRequestResultWithTac(new FinalAudioRequestResult(new MockRequestResult(), null), null));
        requestResults.add(new FinalAudioRequestResultWithTac(new FinalAudioRequestResult(new MockRequestResult(), null), null));
        taskResult = new FinalTaskResultWithTac(new Task(), requestResults);
    }


    @Test
    void testGetResults() {
        assert (taskResult.getResults().size() == 3);
    }

    @Test
    void testGetTask() {
        assert (taskResult.getTask() != null);
    }

    private static class Task implements ITask {

        @Override
        public List<IAudioRequest> getAudioRequests() {
            return null;
        }

        @Override
        public int getTaskID() {
            return 0;
        }

        @Override
        public List<IFrameworkConfiguration> getFrameworkConfigurations() {
            return null;
        }

        @Override
        public List<IPostProcessFactory> getPostProcessFactories() {
            return null;
        }
    }

    private static class MockRequestResult implements IAudioRequestResult {

        @Override
        public IAudioRequest getRequest() {
            return null;
        }

        @Override
        public List<ISpeechToTextServiceData> getResults() {
            return null;
        }

        @Override
        public RequestResultStatus getStatus() {
            return null;
        }
    }
}
