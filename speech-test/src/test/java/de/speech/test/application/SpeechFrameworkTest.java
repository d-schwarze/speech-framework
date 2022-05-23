package de.speech.test.application;

import de.speech.core.application.DispatcherFailedStoppingException;
import de.speech.core.application.SpeechApplication;
import de.speech.core.application.configuration.WorkerConfiguration;
import de.speech.core.application.execution.ExecutionErrorException;
import de.speech.core.dispatcher.implementation.exception.NoWorkerException;
import de.speech.core.dispatcher.implementation.exception.PreProcessesMissingException;
import de.speech.core.framework.IFramework;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.ITask;
import de.speech.core.task.result.ISpeechToTextServiceData;
import de.speech.core.task.result.ITaskResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;
import de.speech.test.expected.ExpectedResult;
import de.speech.test.expected.ExpectedResultFinder;
import de.speech.test.generator.Generator;
import de.speech.worker.WorkerManager;
import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpeechFrameworkTest {

    private SpeechApplication application;

    private WorkerManager workerManager;

    private List<ExpectedResult> expectedResults;

    private List<IAudioRequest> audioRequests;

    private List<ITask> tasks;

    private Generator generator;



    @BeforeEach
    public final void initializeTest() throws FileNotFoundException, InterruptedException {
        workerManager = new WorkerManager();
        Thread.sleep(1000);

        ExpectedResultFinder finder = new ExpectedResultFinder();
        expectedResults = finder.getExpectedResults();

        generator = new Generator(expectedResults, 4, 2, 1, 1, new ArrayList<>());
        tasks = generator.getTasks();
        audioRequests = generator.getRequests();
    }

    @Test
    @Order(3)
    public final void testSpeechFramework() throws InterruptedException, ExecutionException, TimeoutException {
        application = new SpeechApplication();

        application.start();

        ITask task = tasks.get(0);
        assertEquals(1, task.getFrameworkConfigurations().size());
        IFramework framework = task.getFrameworkConfigurations().get(0).getFramework();
        assertEquals("framework0", framework.getIdentifier());
        assertEquals("model0", framework.getModel());

        Future<ITaskResult<FinalAudioRequestResult>> f = application.runTaskWithFuture(tasks.get(0));
        ITaskResult<FinalAudioRequestResult> r = f.get(15000, TimeUnit.MILLISECONDS);

        for (FinalAudioRequestResult result : r.getResults()) {
            for (ISpeechToTextServiceData data : result.getResults()) {
                System.out.println(String.format("Request %d: %s", result.getRequest().getRequestId(), data.getRecognizedSentence()));
            }
        }
    }

    @RepeatedTest(3)
    @Order(2)
    public final void testPreProcess_NotFound() throws InterruptedException {
        application = new SpeechApplication();

        application.start();

        ITask task = tasks.get(0);
        task.getFrameworkConfigurations().get(0).getPreprocesses().add("unknownPreProcess");

        Future<ITaskResult<FinalAudioRequestResult>> f = application.runTaskWithFuture(tasks.get(0));

        try {
            ITaskResult<FinalAudioRequestResult> r = f.get(30000, TimeUnit.MILLISECONDS);
            for (FinalAudioRequestResult result : r.getResults()) {
                if (result.getRequest().getRequestId() == 0) {
                    Assertions.assertTrue(result.getStatus().getErrors().get(0) instanceof PreProcessesMissingException);
                }
            }

            return;
        } catch (InterruptedException e) {
            fail("Interrupt");
        } catch (ExecutionException e) {
            fail("executionException");
        } catch (TimeoutException e) {
            fail("Timeout");
        }

        fail();
    }

    @Test
    @Order(1)
    public final void testWorkerConfiguration_WorkerNotAvailable() throws DispatcherFailedStoppingException {
        application = new CustomSpeechApplication() {
            @Override
            public CustomSpeechConfiguration setupConfiguration() {
                List<WorkerConfiguration> workerConfigurations = new ArrayList<>();
                workerConfigurations.add(() -> "http://127.0.0.1:34");
                return new CustomSpeechConfiguration(workerConfigurations, 1000, 6000, 3000, 10, 10, 10);
            }
        };

        application.start();

        ITask task = tasks.get(0);

        Future<ITaskResult<FinalAudioRequestResult>> f = application.runTaskWithFuture(tasks.get(0));
        try {
            ITaskResult<FinalAudioRequestResult> r = f.get(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Interrupt");
        } catch (ExecutionException e) {
            ExecutionErrorException error = (ExecutionErrorException) e.getCause();
            assertEquals(error.getOccurredExceptions().get("dispatcherPart").getClass(), NoWorkerException.class);
            return;
        } catch (TimeoutException e) {
            fail("Timeout");
        }

        fail();
    }


    @AfterEach
    public void cleanupTestSuit() {
        try {
            application.stop();
        } catch (DispatcherFailedStoppingException e) {
            e.printStackTrace();
            fail("Dispatcher could not be stopped");
        }
        workerManager.shutdown();
    }
}
