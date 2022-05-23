package de.speech.core.application;

import de.fraunhofer.iosb.spinpro.speechtotext.ISpeechToTextServiceMetadata;
import de.speech.core.dispatcher.DispatcherMock;
import de.speech.core.dispatcher.implementation.AbstractDispatcher;
import de.speech.core.framework.Framework;
import de.speech.core.framework.IFramework;
import de.speech.core.postprocessing.IPostProcess;
import de.speech.core.postprocessing.IPostProcessFactory;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.IFrameworkConfiguration;
import de.speech.core.task.ITask;
import de.speech.core.task.implementation.FrameworkConfiguration;
import de.speech.core.task.implementation.Task;
import de.speech.core.task.implementation.audioRequest.AudioRequestWithPath;
import de.speech.core.task.result.ISpeechToTextServiceData;
import de.speech.core.task.result.ITaskResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpeechApplicationTest {

    private static SpeechApplication application;

    private static ITask task;

    private static List<IFrameworkConfiguration> frameworkConfigurations = new ArrayList<>();

    private static List<IFramework> frameworks = new ArrayList<>();

    private static List<IPostProcessFactory> postProcessFactories = new ArrayList<>();

    private static List<IAudioRequest> audioRequests = new ArrayList<>();

    private static int tempProcessId;

    @BeforeAll
    public static void initializeTestSuit() {
        application = new SpeechApplicationMock();
        application.start();

        initializeFrameworks();
        initializeFrameworkConfigurations();
        initializePostProcessFactories();
        initializeAudioRequests();
        initializeTask();


        task = new Task(0, frameworkConfigurations, postProcessFactories, audioRequests);

    }

    public static void initializeFrameworks() {
        frameworks.add(new Framework("frameworkA", "modelA"));
        frameworks.add(new Framework("frameworkA", "modelB"));
        frameworks.add(new Framework("frameworkB", "modelA"));
    }

    public static void initializeFrameworkConfigurations() {
        for (IFramework framework : frameworks) {
            frameworkConfigurations.add(new FrameworkConfiguration(framework, new ArrayList<>()));
        }
    }


    public static void initializePostProcessFactories() {
        for (int i = 0; i < 3; i++) {
            tempProcessId = i;
            postProcessFactories.add(() -> new PostProcessMock());
        }
    }

    public static void initializeAudioRequests() {
        audioRequests.add(new AudioRequestWithPath(0, SpeechApplicationTest.class.getResource("/audio.wav").getPath()));
    }

    public static void initializeTask() {
        task = new Task(0, frameworkConfigurations, postProcessFactories, audioRequests);
    }



    @Test
    public void testRunTask() throws InterruptedException, ExecutionException {

        Future<ITaskResult<FinalAudioRequestResult>> future = application.runTaskWithFuture(task);
        ITaskResult<FinalAudioRequestResult> result = future.get();

        assertEquals(1, result.getResults().size());

        String expectedResult =
                frameworks
                        .stream()
                        .map(framework -> String.format("%d_%d_%s_%s", result.getTask().getTaskID(),
                                                                       result.getTask().getAudioRequests().get(0).getRequestId(),
                                                                       framework.getIdentifier(),
                                                                       framework.getModel()))
                        .collect(Collectors.joining(","));

        String actualResult = result.getResults().get(0).getPostProcessingResults().get(PostProcessMock.class.getName());

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testStopStart() throws ExecutionException, InterruptedException, DispatcherFailedStoppingException {
        Future<ITaskResult<FinalAudioRequestResult>> future = application.runTaskWithFuture(task);
        ITaskResult<FinalAudioRequestResult> result = future.get();

        assertEquals(1, result.getResults().size());

        application.stop();

        application.start();

        future = application.runTaskWithFuture(task);
        result = future.get();

        assertEquals(1, result.getResults().size());
    }


    static class SpeechApplicationMock extends SpeechApplication {

        @Override
        protected AbstractDispatcher createDispatcher() {
            return new DispatcherMock();
        }

    }

    static class SpeechToTextServiceDataMock implements ISpeechToTextServiceData {

        private String framework;

        private String model;

        private String recognizedSentence;

        public SpeechToTextServiceDataMock(String framework, String model, String recognizedSentence) {
            this.framework = framework;
            this.model = model;
            this.recognizedSentence = recognizedSentence;
        }

        @Override
        public List<String> getPreprocesses() {
            return null;
        }

        @Override
        public String getRecognizedSentence() {
            return recognizedSentence;
        }

        @Override
        public List<Map<String, Double>> getProbabilitiesPerRecognizedWord() {
            return null;
        }

        @Override
        public String getFramework() {
            return framework;
        }

        @Override
        public String getModel() {
            return model;
        }

        @Override
        public String getFrameworkDependentJson() {
            return null;
        }
    }

    static class PostProcessMock implements IPostProcess {

        @Override
        public String process(List<ISpeechToTextServiceData> inputData) {
            return inputData.stream().map(ISpeechToTextServiceMetadata::getRecognizedSentence).collect(Collectors.joining(","));
        }
    }




}
