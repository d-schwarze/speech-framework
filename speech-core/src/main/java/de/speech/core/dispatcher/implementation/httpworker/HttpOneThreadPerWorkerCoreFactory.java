package de.speech.core.dispatcher.implementation.httpworker;

import de.speech.core.application.configuration.WorkerConfiguration;
import de.speech.core.dispatcher.IWorkerCoreFactory;
import de.speech.core.dispatcher.implementation.WorkerThread;
import de.speech.core.logging.SpeechLogging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory creates http worker. Adds the worker to a httpserver.
 */
public class HttpOneThreadPerWorkerCoreFactory implements IWorkerCoreFactory {

    private static final Logger logger = SpeechLogging.getLogger();

    private HttpNetworkHandler handler;

    private HttpServer server;
    private long resultTimeout;

    /**
     * Create a new factory.
     *
     * @param resultTimeout maximum time
     */
    public HttpOneThreadPerWorkerCoreFactory(long resultTimeout, long httpRequestTimeout, int port, int acceptors, int selectors, int queueSize) {
        try {
            server = new HttpServer(port, acceptors, selectors, queueSize);
            handler = new HttpNetworkHandler(httpRequestTimeout);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not start", e);
        }
        this.resultTimeout = resultTimeout;
    }

    /**
     * A setter for the result timeout.
     *
     * @param timeout maximum time
     */
    public void setResultTimeout(long timeout) {
        this.resultTimeout = timeout;
    }

    /**
     * Creates a new httpWorker and a new thread, that handles the httpWorker.
     *
     * @param config location of the worker
     * @return new worker
     */
    @Override
    public HttpWorker createWorkerCore(WorkerConfiguration config) {
        HttpWorker worker = new HttpWorker(config, server, resultTimeout, handler);
        WorkerThread thread = new WorkerThread(worker);
        worker.addStatusChangeListener(handler);
        thread.start();
        handler.addHttpWorker(worker);
        return worker;
    }
}
