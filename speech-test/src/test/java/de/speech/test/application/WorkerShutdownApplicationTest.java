package de.speech.test.application;

import de.speech.core.application.DispatcherFailedStoppingException;
import de.speech.core.application.SpeechApplication;
import de.speech.core.application.configuration.WorkerConfiguration;
import de.speech.core.application.configuration.json.JsonWorkerConfiguration;
import de.speech.core.task.ITask;
import de.speech.core.task.result.ITaskResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;
import de.speech.dev.builder.taskbuilder.implementation.TaskBuilderWithPath;
import de.speech.worker.Config;
import de.speech.worker.WorkerManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WorkerShutdownApplicationTest {

    private static SpeechApplication application;

    private static WorkerManager workerManager;

    @BeforeAll
    public static void initializeTestSuit() throws InterruptedException {
        initializeWorkerManager();
        Thread.sleep(1000);
        initializeSpeechApplication();
    }

    private static void initializeWorkerManager() {
        workerManager = new WorkerManager(new Config(3000,10,"preprocesses" ,"src/test/resources/SpeechToText0Waiting.jar"));
    }

    private static void initializeSpeechApplication() {
        List<WorkerConfiguration> workerConfigs = new LinkedList();
        workerConfigs.add(new JsonWorkerConfiguration("http://127.0.0.1:3000"));

        application = new CustomSpeechApplication() {
            @Override
            public CustomSpeechConfiguration setupConfiguration() {
                return new CustomSpeechConfiguration(workerConfigs, 5000,3000, 2999, 1, 1, 124);
            }
        };
        application.start();
    }

    @Test
    public final void testError() throws InterruptedException, ExecutionException, TimeoutException {
        TaskBuilderWithPath taskBuilderWithPath = new TaskBuilderWithPath("src/test/resources/clips/waveSet2/");
        taskBuilderWithPath.addFrameworkConfiguration("framework0", "model0");
        ITask task = taskBuilderWithPath.buildTask();

        Future<ITaskResult<FinalAudioRequestResult>> f = application.runTaskWithFuture(task);
        Thread.sleep(1000);
        workerManager.shutdown();
        ITaskResult<FinalAudioRequestResult> r = f.get(20000, TimeUnit.MILLISECONDS);
        for (FinalAudioRequestResult result : r.getResults()) {
            Assertions.assertFalse(result.getStatus().getErrors().isEmpty());
        }
    }

    @AfterAll
    public static void cleanupTestSuit() throws DispatcherFailedStoppingException {
        application.stop();
    }
}
