package de.speech.core.dispatcher.http;

import de.speech.core.application.configuration.WorkerConfiguration;
import de.speech.core.application.configuration.json.JsonWorkerConfiguration;
import de.speech.core.dispatcher.implementation.FrameWorkDispatcher;
import de.speech.core.dispatcher.implementation.WorkerStatus;
import de.speech.core.dispatcher.implementation.WorkerThread;
import de.speech.core.dispatcher.implementation.httpworker.HttpNetworkHandler;
import de.speech.core.dispatcher.implementation.httpworker.HttpServer;
import de.speech.core.dispatcher.implementation.httpworker.HttpWorker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestErrorHandlers {

    private static final int RESULT_TIMEOUT = 1000;

    @Test
    public void testInitialize() throws Exception {
        HttpServer server = new HttpServer(2999, 1, 1, 128);
        WorkerConfiguration configuration = new JsonWorkerConfiguration("http://127.0.0.1:3000");

        FrameWorkDispatcher dispatcher = new FrameWorkDispatcher(null);
        HttpNetworkHandler handler = new HttpNetworkHandler(100);
        HttpWorker worker = new HttpWorker(configuration, server, RESULT_TIMEOUT, handler);
        worker.setRequestSource(dispatcher);
        WorkerThread thread = new WorkerThread(worker);
        worker.addStatusChangeListener(thread);
        thread.start();
        dispatcher.addWorker(worker);
        Thread.sleep(500);
        Assertions.assertEquals(WorkerStatus.WORKER_STOPPED, worker.getStatus());
        worker.stop();
    }

    @Test
    public void testStatusRequest() throws Exception {
        HttpServer server = new HttpServer(2999, 1, 1, 128);
        WorkerConfiguration configuration = new JsonWorkerConfiguration("http://127.0.0.1:3000");

        FrameWorkDispatcher dispatcher = new FrameWorkDispatcher(null);
        HttpNetworkHandler handler = new HttpNetworkHandler(100);
        HttpWorker worker = new HttpWorker(configuration, server, RESULT_TIMEOUT, handler);
        worker.setRequestSource(dispatcher);
        dispatcher.addWorker(worker);
        worker.setEndpoint("endpoint");
        worker.statusRequest();
        Thread.sleep(500);
        Assertions.assertEquals(WorkerStatus.WORKER_STOPPED, worker.getStatus());
        worker.stop();
    }

    @Test
    public void testTimeout() throws Exception {
        HttpServer server = new HttpServer(2999, 1, 1, 128);
        WorkerConfiguration configuration = new JsonWorkerConfiguration("http://127.0.0.1:3000");

        FrameWorkDispatcher dispatcher = new FrameWorkDispatcher(null);
        HttpNetworkHandler handler = new HttpNetworkHandler(100);
        HttpWorker worker = new HttpWorker(configuration, server, RESULT_TIMEOUT, handler);
        worker.setRequestSource(dispatcher);
        WorkerThread thread = new WorkerThread(worker);
        worker.addStatusChangeListener(thread);
        thread.start();
        dispatcher.addWorker(worker);
        Thread.sleep(1000);
        Assertions.assertEquals(WorkerStatus.WORKER_STOPPED, worker.getStatus());
        worker.stop();
    }
}
