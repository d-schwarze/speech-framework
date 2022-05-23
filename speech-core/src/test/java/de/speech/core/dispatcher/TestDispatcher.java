package de.speech.core.dispatcher;

import de.speech.core.application.configuration.WorkerConfiguration;
import de.speech.core.application.configuration.json.JsonWorkerConfiguration;
import de.speech.core.dispatcher.http.HttpWorkerMock;
import de.speech.core.dispatcher.implementation.Dispatcher;
import de.speech.core.task.ITask;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ISpeechToTextServiceData;
import de.speech.core.task.result.ITaskResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestDispatcher {

    private final static int FRAMEWORKS = 2;
    private final static int WORKERS_PER_FRAMEWORK_MODEL_COMBINATION = 2;
    private final static int REQUEST_AMOUNT = 100;
    private final static int PROCESSING_TIME = 0;
    private final static int MOCK_STARTING_PORT = 3000;
    private static Dispatcher dispatcher;
    private static HttpWorkerMock[][] mock;

    @BeforeAll
    public static void setupWorkerMocks() {
        mock = new HttpWorkerMock[FRAMEWORKS][WORKERS_PER_FRAMEWORK_MODEL_COMBINATION];
        try {
            for (int f = 0; f < FRAMEWORKS; f++) {
                for (int i = 0; i < WORKERS_PER_FRAMEWORK_MODEL_COMBINATION; i++) {
                    mock[f][i] = new HttpWorkerMock(true, "framework" + f, "model0", MOCK_STARTING_PORT + f * WORKERS_PER_FRAMEWORK_MODEL_COMBINATION + i,
                            PROCESSING_TIME);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void terminate() throws Exception {
        for (HttpWorkerMock[] mockArray : mock) {
            for (HttpWorkerMock mock : mockArray) {
                mock.stop();
            }
        }

        dispatcher.stopAll();
    }

    @BeforeEach
    public void initializeDispatcher() {
        WorkerConfiguration[] configuration = new WorkerConfiguration[FRAMEWORKS * WORKERS_PER_FRAMEWORK_MODEL_COMBINATION];
        for (int i = 0; i < configuration.length; i++) {
            configuration[i] = new JsonWorkerConfiguration("http://localhost:" + (MOCK_STARTING_PORT + i));
        }

        dispatcher = new Dispatcher();

        dispatcher.initializeWorker(configuration);
    }

    @Test
    public void testDispatchTask() {
        ITask task = new TaskMockFactory().createTask(FRAMEWORKS, REQUEST_AMOUNT);
        Future<ITaskResult<IAudioRequestResult>> future = dispatcher.dispatchTask(task);

        ITaskResult<IAudioRequestResult> taskResult = null;
        try {
            taskResult = future.get(50000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("interrupted", e);
        } catch (ExecutionException e) {
            fail("exception during execution", e);
        } catch (TimeoutException e) {
            fail("timeout");
        }
        for (IAudioRequestResult result : taskResult.getResults()) {
            for (int f = 0; f < FRAMEWORKS; f++) {
                ISpeechToTextServiceData data = result.getResults().get(f);
                String model = task.getFrameworkConfigurations().get(f).getFramework().getModel();
                String identifier = task.getFrameworkConfigurations().get(f).getFramework().getIdentifier();
                if (!result.getStatus().getErrors().isEmpty()) {
                    result.getStatus().getErrors().get(0).printStackTrace();
                }
                assertEquals(model, data.getModel());
                assertEquals(identifier, data.getFramework());
                assertEquals(data.getRecognizedSentence(), "sentence");
            }
        }
    }
}
