package de.speech.core.dispatcher.http;

import de.speech.core.application.configuration.WorkerConfiguration;
import de.speech.core.application.configuration.json.JsonWorkerConfiguration;
import de.speech.core.dispatcher.IFrameworkResult;
import de.speech.core.dispatcher.IWorkerAudioRequest;
import de.speech.core.dispatcher.implementation.FrameWorkDispatcher;
import de.speech.core.dispatcher.implementation.RequestUtilsTest;
import de.speech.core.dispatcher.implementation.WorkerStatus;
import de.speech.core.dispatcher.implementation.WorkerThread;
import de.speech.core.dispatcher.implementation.exception.NoWorkerException;
import de.speech.core.dispatcher.implementation.httpworker.HttpServer;
import de.speech.core.dispatcher.implementation.httpworker.HttpWorker;
import de.speech.core.dispatcher.implementation.requestresult.WorkerAudioRequest;
import de.speech.core.task.implementation.audioRequest.AudioRequestWithInputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.fail;

public class TestHttpWorker {

    private final static int QUEUE_SIZE = 10;
    private final static int PROCESSING_TIME = 0;
    private final static int RESULT_TIMEOUT = 3000;
    private final static String SAMPLE_FILE = "/audio.wav";

    private static HttpWorker worker;
    private static FrameWorkDispatcher dispatcher;
    private static HttpWorkerMock mock;

    private static HttpServer server;

    private static WorkerThread thread;

    @BeforeEach
    public void setupExternWorker() {
        try {
            mock = new HttpWorkerMock(true, "framework_0", "", 3000, PROCESSING_TIME);
            server = new HttpServer(2999, 1, 1, 128);
        } catch (Exception e) {
            fail("failed to start workerMock and HttpServer");
        }
    }

    @AfterEach
    public void stopMock() {
        try {
            mock.stop();
        } catch (Exception e) {
            fail("failed to stop workerMock");
        }
    }

    @BeforeEach
    public void initializeWorker() {
        mock.clearRequests();
        mock.setProcessing(true);
        mock.setQueueSize(10);

        WorkerConfiguration configuration = new JsonWorkerConfiguration("http://127.0.0.1:3000");

        dispatcher = new FrameWorkDispatcher(null);

        worker = new HttpWorker(configuration, server, RESULT_TIMEOUT);
        worker.setRequestSource(dispatcher);
        thread = new WorkerThread(worker);
        worker.addStatusChangeListener(thread);
        thread.start();
        dispatcher.addWorker(worker);
    }

    @AfterEach
    public void terminateWorker() {
        try {
            worker.stop();
        } catch (Exception e) {
            fail("failed to stop worker");
        }
    }

    @Test
    public void testSendRequest() {
        int request_amount = 150;

        IWorkerAudioRequest[] requests = new IWorkerAudioRequest[request_amount];
        Future<IFrameworkResult>[] results = new Future[request_amount];
        for (int i = 0; i < request_amount; i++) {
            AudioInputStream stream = null;
            try {
                stream = AudioSystem.getAudioInputStream(RequestUtilsTest.class.getResource(SAMPLE_FILE));
            } catch (IOException | UnsupportedAudioFileException e) {
                fail("audio file could not loaded", e);
            }

            requests[i] = new WorkerAudioRequest(new AudioRequestWithInputStream(i, stream), new LinkedList<>());
            results[i] = dispatcher.dispatchRequest(requests[i]);
        }

        for (int i = 0; i < request_amount; i++) {
            try {
                Assertions.assertEquals(results[i].get().getRequestId(), requests[i].getId());
            } catch (InterruptedException e) {
                fail("interrupted", e);
            } catch (ExecutionException e) {
                fail("exception", e);
            }
        }
    }

    @Test
    public void testWorkerNeverFinishesRequest() {
        mock.setProcessing(false);
        AudioInputStream stream = null;
        try {
            stream = AudioSystem.getAudioInputStream(RequestUtilsTest.class.getResource(SAMPLE_FILE));
        } catch (IOException | UnsupportedAudioFileException e) {
            fail("audio file could not loaded", e);
        }

        IWorkerAudioRequest request = new WorkerAudioRequest(new AudioRequestWithInputStream(0, stream), new LinkedList<>());

        Future<IFrameworkResult> result = dispatcher.dispatchRequest(request);

        boolean exception = false;

        try {
            result.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            exception = true;
            Assertions.assertTrue(e.getCause() instanceof TimeoutException);
        }

        Assertions.assertTrue(exception);
    }

    @Test
    public void queueSizeAfterRequest() {
        mock.setProcessing(false);
        AudioInputStream stream = null;
        try {
            stream = AudioSystem.getAudioInputStream(RequestUtilsTest.class.getResource(SAMPLE_FILE));
        } catch (IOException | UnsupportedAudioFileException e) {
            fail("audio file could not loaded", e);
        }
        IWorkerAudioRequest request = new WorkerAudioRequest(new AudioRequestWithInputStream(0, stream), new LinkedList<>());
        dispatcher.dispatchRequest(request);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            fail("interrupted", e);
        }

        Assertions.assertEquals(QUEUE_SIZE - 1, worker.getQueuePlacesFree());
    }

    @Test
    public void interrupt() {
        try {
            worker.stop();
        } catch (Exception e) {
            fail("exception during stopping", e);
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            fail("interrupted", e);
        }
        Assertions.assertEquals(worker.getStatus(), WorkerStatus.WORKER_STOPPED);
        Assertions.assertFalse(thread.isAlive());
    }

    @Test
    public void testStatusRequest() {
        mock.setQueueSize(20);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            fail("interrupted", e);
        }
        worker.statusRequest();
        Assertions.assertEquals(worker.getQueueItemsAmount() + worker.getQueuePlacesFree(), 20);
    }

    @Test
    public void workerStopsAfterQueueSizeFull() {
        mock.setProcessingTime(2000);

        int request_amount = 50;

        IWorkerAudioRequest[] requests = new IWorkerAudioRequest[request_amount];
        Future<IFrameworkResult>[] results = new Future[request_amount];
        for (int i = 0; i < request_amount; i++) {
            AudioInputStream stream = null;
            try {
                stream = AudioSystem.getAudioInputStream(RequestUtilsTest.class.getResource(SAMPLE_FILE));
            } catch (IOException | UnsupportedAudioFileException e) {
                fail("audio file could not loaded", e);
            }

            requests[i] = new WorkerAudioRequest(new AudioRequestWithInputStream(i, stream), new LinkedList<>());
            results[i] = dispatcher.dispatchRequest(requests[i]);
        }

        try {
            Thread.sleep(2000);
            mock.stop();

            for (Future<IFrameworkResult> result : results) {
                try {
                    result.get();
                } catch (ExecutionException e) {
                    Assertions.assertTrue(e.getCause() instanceof TimeoutException ||
                            e.getCause() instanceof NoWorkerException);
                }
            }
        } catch (InterruptedException e) {
            fail("interrupted", e);
        } catch (Exception e) {
            fail("failed to stop mock", e);
        }
    }
}
