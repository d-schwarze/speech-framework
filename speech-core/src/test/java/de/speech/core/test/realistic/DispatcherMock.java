package de.speech.core.test.realistic;

import de.speech.core.application.configuration.WorkerConfiguration;
import de.speech.core.dispatcher.implementation.AbstractDispatcher;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.ITask;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ISpeechToTextServiceData;
import de.speech.core.task.result.ITaskResult;
import de.speech.core.task.result.implementation.AudioRequestResult;
import de.speech.core.task.result.implementation.TaskResult;
import de.speech.core.test.expected.ExpectedResult;
import de.speech.core.test.expected.ExpectedResultFinder;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class DispatcherMock extends AbstractDispatcher {

    private List<ExpectedResult> expectedResults;

    public DispatcherMock() throws FileNotFoundException {
        ExpectedResultFinder finder = new ExpectedResultFinder();
        expectedResults = finder.getExpectedResults();
    }

    @Override
    public void initializeWorker(WorkerConfiguration... workers) {
        throw new IllegalCallerException();
    }

    @Override
    public void stopWorker(WorkerConfiguration config) {
        throw new IllegalCallerException();
    }

    @Override
    public void stopAll() {
        throw new IllegalCallerException();
    }

    @Override
    public Future<ITaskResult<IAudioRequestResult>> dispatchTask(ITask task) {


        List<IAudioRequestResult> results = new ArrayList<>();

        for (IAudioRequest request : task.getAudioRequests()) {
            for (ExpectedResult expectedResult : expectedResults) {
                if (request.getRequestId() == expectedResult.getId()) {
                    results.add(new AudioRequestResult(
                            Arrays.asList(new SpeechToTextServiceDataMock(
                                    expectedResult.getActual(),
                                    task.getFrameworkConfigurations().get(0).getFramework().getIdentifier(),
                                    task.getFrameworkConfigurations().get(0).getFramework().getModel())),
                            request,
                            null)
                    );
                }
            }
        }

        CompletableFuture<ITaskResult<IAudioRequestResult>> future = new CompletableFuture<>();
        future.complete(new TaskResult(task, results));
        return future;


    }

    class SpeechToTextServiceDataMock implements ISpeechToTextServiceData {

        private String recognizedSentence;

        private String framework;

        private String model;

        public SpeechToTextServiceDataMock(String recognizedSentence, String framework, String model) {
            this.recognizedSentence = recognizedSentence;
            this.framework = framework;
            this.model = model;
        }

        @Override
        public List<String> getPreprocesses() {
            return new ArrayList<>();
        }

        @Override
        public String getRecognizedSentence() {
            return this.recognizedSentence;
        }

        @Override
        public List<Map<String, Double>> getProbabilitiesPerRecognizedWord() {
            return null;
        }

        @Override
        public String getFramework() {
            return this.framework;
        }

        @Override
        public String getModel() {
            return this.model;
        }

        @Override
        public String getFrameworkDependentJson() {
            return null;
        }
    }
}
