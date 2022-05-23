package de.speech.test.application;

import de.speech.core.application.DispatcherFailedStoppingException;
import de.speech.core.application.SpeechApplication;
import de.speech.core.task.ITask;
import de.speech.core.task.result.ISpeechToTextServiceData;
import de.speech.core.task.result.ITaskResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;
import de.speech.dev.builder.taskbuilder.implementation.TaskBuilderWithPath;
import de.speech.test.expected.ExpectedResult;
import de.speech.test.expected.ExpectedResultFinder;
import de.speech.worker.Config;
import de.speech.worker.WorkerManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SpeechFrameworkWithCMUTest {

    private static SpeechApplication application;

    private static WorkerManager workerManager;

    @BeforeAll
    public static void initializeTestSuit() throws InterruptedException {
        workerManager = new WorkerManager(new Config(3000, 30, "preprocesses", "src/test/resources/CMUSphinxService-0.1.jar"));

        Thread.sleep(1000);
        application = new SpeechApplication();

        application.start();
    }

    @Test
    public final void testSpeechFramework_CMUSpeechToTextService_SingleAudioRequest() {
        ITask task = new TaskBuilderWithPath("src/test/resources/clips/wavSet4/wav").addFrameworkConfiguration("CMUSphinxEN", "").buildTask();

        Future<ITaskResult<FinalAudioRequestResult>> f = application.runTaskWithFuture(task);
        ITaskResult<FinalAudioRequestResult> r = null;

        try {
            r = f.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail("ExecutionException");
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Interrupt");
        }

        for (FinalAudioRequestResult result : r.getResults()) {
            for (ISpeechToTextServiceData data : result.getResults()) {
                System.out.println(data.getRecognizedSentence());
            }
        }

        assertEquals(task.getAudioRequests().size(), r.getResults().size());
    }

    @Test
    public final void testSpeechFramework_CMUSpeechToTextService_MultipleAudioRequest() {
        ITask task = new TaskBuilderWithPath("src/test/resources/clips/wavSet5/wav").addFrameworkConfiguration("CMUSphinxEN", "").buildTask();

        Future<ITaskResult<FinalAudioRequestResult>> f = application.runTaskWithFuture(task);
        ITaskResult<FinalAudioRequestResult> r = null;

        try {
            r = f.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail("ExecutionException");
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Interrupt");
        }

        for (FinalAudioRequestResult result : r.getResults()) {
            for (ISpeechToTextServiceData data : result.getResults()) {
                System.out.println(data.getRecognizedSentence());
            }
        }

        assertEquals(task.getAudioRequests().size(), r.getResults().size());
    }

    @Test
    public final void testSpeechFramework_CMUSpeechToTextService_MultipleTasks() throws FileNotFoundException {
        ITask task1 = new TaskBuilderWithPath("src/test/resources/clips/wavSet5/wav").addFrameworkConfiguration("CMUSphinxEN", "").buildTask();
        ITask task2 = new TaskBuilderWithPath("src/test/resources/clips/wavSet4/wav").addFrameworkConfiguration("CMUSphinxEN", "").buildTask();
        List<ExpectedResult> results1 = new ExpectedResultFinder("src/test/resources/clips/wavSet5/actual.json").getExpectedResults();
        List<ExpectedResult> results2 = new ExpectedResultFinder("src/test/resources/clips/wavSet4/actual.json").getExpectedResults();
        Future<ITaskResult<FinalAudioRequestResult>> f1 = application.runTaskWithFuture(task1);
        Future<ITaskResult<FinalAudioRequestResult>> f2 = application.runTaskWithFuture(task2);
        ITaskResult<FinalAudioRequestResult> r1 = null;
        ITaskResult<FinalAudioRequestResult> r2 = null;

        try {
            r1 = f1.get();
            r2 = f2.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail("ExecutionException");
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Interrupt");
        }

        System.out.println("Task 1 - Expected");
        for (ExpectedResult result : results1) {
            System.out.println(result.getActual());
        }

        System.out.println("Task 1 - Results");
        for (FinalAudioRequestResult result : r1.getResults()) {
            for (ISpeechToTextServiceData data : result.getResults()) {
                System.out.println(data.getRecognizedSentence());
            }
        }

        System.out.println("Task 2 - Expected");
        for (ExpectedResult result : results2) {
            System.out.println(result.getActual());
        }

        System.out.println("Task 2 - Results");
        for (FinalAudioRequestResult result : r2.getResults()) {
            for (ISpeechToTextServiceData data : result.getResults()) {
                System.out.println(data.getRecognizedSentence());
            }
        }

        assertEquals(task1.getAudioRequests().size(), r1.getResults().size());
        assertEquals(task2.getAudioRequests().size(), r2.getResults().size());
    }

    @AfterAll
    public static void cleanupTestSuit() throws DispatcherFailedStoppingException {
        application.stop();
        workerManager.shutdown();
    }

}
