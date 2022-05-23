package de.speech.core.dispatcher.http;

import de.speech.core.dispatcher.IWorkerAudioRequest;
import de.speech.core.dispatcher.WorkerInformation;
import de.speech.core.dispatcher.implementation.RequestUtilsTest;
import de.speech.core.dispatcher.implementation.httpworker.HttpNetworkHandler;
import de.speech.core.dispatcher.implementation.requestresult.WorkerAudioRequest;
import de.speech.core.task.implementation.audioRequest.AudioRequestWithInputStream;
import org.junit.jupiter.api.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.fail;


public class TestHttpNetworkHandler {

    private static final int PROCESSING_TIME = 100;
    private static final int QUEUE_SIZE = 10;
    private static final String MODEL = "model_0";
    private static final String FRAMEWORK = "framework_0";
    private static final int PORT = 3000;
    private static final String WORKER_LOCATION = "http://127.0.0.1:3000";

    private static HttpWorkerMock mock;

    private HttpNetworkHandler handler;

    @BeforeAll
    public static void setupExternWorker() {
        try {
            mock = new HttpWorkerMock(true, FRAMEWORK, MODEL, PORT, PROCESSING_TIME);
        } catch (Exception e) {
            fail("failed to start workerMock");
        }
        mock.setQueueSize(QUEUE_SIZE);
    }

    @AfterAll
    public static void stopMock() {
        try {
            mock.stop();
        } catch (Exception e) {
            fail("failed to stop worker mock");
        }
    }

    @BeforeEach
    public void setupHandler() {
        try {
            handler = new HttpNetworkHandler();
        } catch (Exception e) {
            fail("failed to start HTTPNetworkHandler");
        }

        mock.setProcessing(true);
    }

    @AfterEach
    public void clearHandler() {
        try {
            handler.stop();
        } catch (Exception e) {
            fail("failed to stop HTTPNetworkHandler");
        }
    }

    @Test
    public void testSendRequests() {
        mock.setProcessing(false);
        AudioInputStream stream = null;
        try {
            stream = AudioSystem.getAudioInputStream(RequestUtilsTest.class.getResource("/audio.wav"));
        } catch (UnsupportedAudioFileException | IOException e) {
            fail("failed to load audioFile");
        }
        IWorkerAudioRequest request = new WorkerAudioRequest(new AudioRequestWithInputStream(0, stream), new LinkedList<>());

        try {
            handler.initializeRequest(WORKER_LOCATION, "worker_1", 2999);
            WorkerInformation info = handler.statusRequest(WORKER_LOCATION);
            handler.sendRequest(request, WORKER_LOCATION);
            handler.sendRequest(request, WORKER_LOCATION);
            Assertions.assertEquals(info.getMaxQueueSize(), QUEUE_SIZE);
            Assertions.assertEquals(info.getModel(), MODEL);
            Assertions.assertEquals(info.getFrameworkName(), FRAMEWORK);
        } catch (InterruptedException e) {
            fail("interrupted");
        } catch (ExecutionException e) {
            e.getCause().printStackTrace();
            fail("executionException");
        } catch (TimeoutException e) {
            fail("timed out");
        }
    }
}
