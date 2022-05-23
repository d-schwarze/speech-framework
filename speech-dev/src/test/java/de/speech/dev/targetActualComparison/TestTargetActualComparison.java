package de.speech.dev.targetActualComparison;

import de.speech.core.task.IAudioRequest;
import de.speech.core.task.ITask;
import de.speech.core.task.implementation.audioRequest.DeveloperAudioRequest;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ISpeechToTextServiceData;
import de.speech.core.task.result.ITaskResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;
import de.speech.core.task.result.implementation.FinalTaskResultWithTac;
import de.speech.core.task.result.implementation.RequestResultStatus;
import de.speech.dev.targetActualComparison.implementation.TargetActualComparison;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestTargetActualComparison {
    private TargetActualComparison targetActualComparison;
    private final String userDir = System.getProperty("user.dir") + File.separator;
    private ITaskResult<FinalAudioRequestResult> mockFinalTaskResult;

    @BeforeAll
    void setUp() {
        cleanUp();

        ArrayList<IAudioRequest> audioRequests = getAudioRequests();
        ArrayList<MockRequestResult> requestResults = getRequestResults(audioRequests);
        HashMap<String, String> postResults = getHashMap();
        ArrayList<FinalAudioRequestResult> finalRequestResults = getFinalRequestResults(requestResults, postResults);
        mockFinalTaskResult = new MockFinalTaskResult(finalRequestResults);
        targetActualComparison = new TargetActualComparison();
    }

    @Test
    void testTargetActualComparisonWithFile() {
        cleanUp();
        File file = targetActualComparison.compare(mockFinalTaskResult, "targetActualComparisonResult.txt");
    }

    @Test
    void testErrorAlreadyExistingFile() {
        File testFile = new File(userDir + "src/test/resources/targetActualComparisonResult/targetActualComparisonResult.txt");
        try {
            testFile.createNewFile();
            targetActualComparison.compare(mockFinalTaskResult, "targetActualComparisonResult.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void testTACWithObject() {
        FinalTaskResultWithTac finalTaskResultWithTac = targetActualComparison.compare(mockFinalTaskResult);
        assert (finalTaskResultWithTac != null);
    }

    @Test
    void testCompareWithNoFileName() {
        File file = targetActualComparison.compare(mockFinalTaskResult, "");
        assert(file.exists());
    }






    private void cleanUp() {
        File[] files = (new File(userDir + "src/test/resources/targetActualComparisonResult")).listFiles();
        if((files != null) && (files.length != 0)) {
            for(File file: files) {
                try {
                    Files.delete(file.toPath());
                } catch (IOException e) {
                    System.out.println("Files konnten nicht gelöscht werden");
                    e.printStackTrace();
                }
            }
        }
    }

    private ArrayList<IAudioRequest> getAudioRequests() {
        ArrayList<IAudioRequest> requests = new ArrayList<>();
        try {
            DeveloperAudioRequest request1 = new DeveloperAudioRequest(new MockAudioRequest(0, AudioSystem.getAudioInputStream(new File(userDir + "src/test/resources/AudioFiles/request1.wav"))), "Ende gut, alles gut.");
            DeveloperAudioRequest request2 = new DeveloperAudioRequest(new MockAudioRequest(1, AudioSystem.getAudioInputStream(new File(userDir + "src/test/resources/AudioFiles/request2.wav"))), "");
            DeveloperAudioRequest request3 = new DeveloperAudioRequest(new MockAudioRequest(2, AudioSystem.getAudioInputStream(new File(userDir + "src/test/resources/AudioFiles/SubDir1/request1_subDir.wav"))), "das ist ein Test");
            requests.add(request1);
            requests.add(request2);
            requests.add(request3);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }

        return requests;
    }

    private ArrayList<MockRequestResult> getRequestResults(ArrayList<IAudioRequest> requests) {
        ArrayList<MockRequestResult> requestResults = new ArrayList<>();
        for (IAudioRequest request: requests) {
            requestResults.add(new MockRequestResult(request));
        }
        return requestResults;
    }

    private HashMap<String, String> getHashMap() {
        HashMap<String, String> postResults = new HashMap<>();
        postResults.put("postProcess1", "das ist ein Test");
        postResults.put("postProcess2", "Das ist das result vom zweiten PostProcess");

        return postResults;
    }

    private ArrayList<FinalAudioRequestResult> getFinalRequestResults(ArrayList<MockRequestResult> requestResults, HashMap<String, String> postResults) {
        ArrayList<FinalAudioRequestResult> finalAudioRequestResults = new ArrayList<>();
        for (MockRequestResult requestResult: requestResults) {
            finalAudioRequestResults.add(new FinalAudioRequestResult(requestResult, postResults));
        }

        return finalAudioRequestResults;
    }

    private static class MockFinalTaskResult implements ITaskResult<FinalAudioRequestResult> {
        private final ArrayList<FinalAudioRequestResult> finalAudioRequestResults;

        MockFinalTaskResult(ArrayList<FinalAudioRequestResult> finalAudioRequestResults) {
            this.finalAudioRequestResults = finalAudioRequestResults;
        }

        @Override
        public ITask getTask() {
            return null;
        }

        @Override
        public List<FinalAudioRequestResult> getResults() {
            return finalAudioRequestResults;
        }
    }

    private static class MockRequestResult implements IAudioRequestResult {
        IAudioRequest request;

        public MockRequestResult(IAudioRequest request) {
            this.request = request;
        }

        @Override
        public IAudioRequest getRequest() {
            return request;
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

    private static class MockAudioRequest implements IAudioRequest {
        private final AudioInputStream audio;
        private final int id;

        public MockAudioRequest(int id, AudioInputStream audio) {
            this.id = id;
            this.audio = audio;
        }

        @Override
        public AudioInputStream getAudio() {
            return audio;
        }

        @Override
        public long getRequestId() {
            return id;
        }
    }
}
