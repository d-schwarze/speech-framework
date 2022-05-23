package de.speech.core.task;

import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ISpeechToTextServiceData;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResultWithTac;
import de.speech.core.task.result.implementation.RequestResultStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestFinalAudioRequestResultWithTac {
    private FinalAudioRequestResultWithTac result;

    @BeforeEach
    void setUp() {
        Map<String, Float> equalityMap = new HashMap<>();
        equalityMap.put("postProcess1", 0.60F);
        equalityMap.put("postProcess2", 1.00F);
        result = new FinalAudioRequestResultWithTac(new FinalAudioRequestResult(new MockResult(), null), equalityMap);
    }

    @Test
    void testGetEqualityMap() {
        assert(result.getEqualityOfPostProcesses().size() == 2);
    }

    private class MockResult implements IAudioRequestResult {

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
