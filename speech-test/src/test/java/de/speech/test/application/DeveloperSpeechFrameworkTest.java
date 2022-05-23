package de.speech.test.application;

import de.speech.core.application.DispatcherFailedStoppingException;
import de.speech.core.task.ITask;
import de.speech.core.task.implementation.Task;
import de.speech.core.task.result.ISpeechToTextServiceData;
import de.speech.core.task.result.implementation.FinalAudioRequestResultWithTac;
import de.speech.core.task.result.implementation.FinalTaskResultWithTac;
import de.speech.dev.application.DeveloperSpeechApplication;
import de.speech.dev.builder.taskbuilder.implementation.DeveloperTaskBuilderWithPath;
import de.speech.worker.Config;
import de.speech.worker.WorkerManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DeveloperSpeechFrameworkTest {

    private static DeveloperSpeechApplication application;

    private static WorkerManager workerManager;

    @BeforeAll
    public static void initializeTestSuit() throws InterruptedException {
        workerManager = new WorkerManager(new Config(3000, 30, "preprocesses", "src/test/resources/CMUSphinxService-0.1.jar"));

        Thread.sleep(1000);
        application = new DeveloperSpeechApplication() {
            @Override
            protected List<ITask> runTasksOnStart() {
                return new ArrayList<>();
            }
        };

        application.start();
    }

    @Test
    public final void testTargetActualComparison() throws InterruptedException, TimeoutException, ExecutionException {

        Task task = new DeveloperTaskBuilderWithPath("src/test/resources/clips/wavSet5/wav", "src/test/resources/clips/wavSet5/tac/tac_input.txt")
                            .addFrameworkConfiguration("CMUSphinxEN", "")
                            .addPostProcessFactory(() -> inputData -> inputData.get(0).getRecognizedSentence())
                            .buildTask();

        Future<FinalTaskResultWithTac> f = application.runTaskWithFutureAndTac(task);
        FinalTaskResultWithTac r = f.get(600000, TimeUnit.MILLISECONDS);

        for (FinalAudioRequestResultWithTac result : r.getResults()) {
            for (ISpeechToTextServiceData data : result.getResults()) {
                System.out.println(String.format("Request %d: %s", result.getRequest().getRequestId(), data.getRecognizedSentence()));
            }

            for (float data : result.getEqualityOfPostProcesses().values()) {
                System.out.println(String.format("TAC %d: %f", result.getRequest().getRequestId(), data));
            }
        }
    }

    @Test
    public final void testTargetActualComparison_WordWavs() throws InterruptedException, TimeoutException, ExecutionException {

        Task task = new DeveloperTaskBuilderWithPath("src/test/resources/clips/wavSet6/wav", "src/test/resources/clips/wavSet6/tac/tac_input.txt")
                .addFrameworkConfiguration("CMUSphinxEN", "")
                .addPostProcessFactory(() -> inputData -> inputData.get(0).getRecognizedSentence())
                .buildTask();

        Future<FinalTaskResultWithTac> f = application.runTaskWithFutureAndTac(task);
        FinalTaskResultWithTac r = f.get(600000, TimeUnit.MILLISECONDS);

        for (FinalAudioRequestResultWithTac result : r.getResults()) {
            for (ISpeechToTextServiceData data : result.getResults()) {
                System.out.println(String.format("Request %d: %s", result.getRequest().getRequestId(), data.getRecognizedSentence()));
            }

            for (float data : result.getEqualityOfPostProcesses().values()) {
                System.out.println(String.format("TAC %d: %f", result.getRequest().getRequestId(), data));
            }
        }
    }



    @AfterAll
    public static void cleanupTestSuit() throws DispatcherFailedStoppingException {
        application.stop();
        workerManager.shutdown();
    }

}
