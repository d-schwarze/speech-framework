package de.speech.core.dispatcher.implementation.httpworker;

import de.speech.core.application.configuration.WorkerConfiguration;
import de.speech.core.dispatcher.ICompletableFrameworkAudioRequest;
import de.speech.core.dispatcher.IWorkerAudioRequest;
import de.speech.core.dispatcher.WorkerInformation;
import de.speech.core.dispatcher.errorhandler.IWorkerInitializeErrorHandler;
import de.speech.core.dispatcher.errorhandler.IWorkerSendErrorHandler;
import de.speech.core.dispatcher.errorhandler.IWorkerStatusRequestErrorHandler;
import de.speech.core.dispatcher.errorhandler.implementation.DefaultAbstractWorkerInitializeErrorHandler;
import de.speech.core.dispatcher.errorhandler.implementation.DefaultAbstractWorkerStatusRequestErrorHandler;
import de.speech.core.dispatcher.errorhandler.implementation.DefaultWorkerSendErrorHandler;
import de.speech.core.dispatcher.implementation.AbstractWorker;
import de.speech.core.dispatcher.implementation.WorkerStatus;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HttpWorker extends AbstractWorker {

    protected final HttpServer server;
    /**
     * this handler handles errors occurring during sending requests.
     */
    protected IWorkerSendErrorHandler<HttpWorker, Throwable> sendErrorHandler = new DefaultWorkerSendErrorHandler();
    /**
     * this handler handles errors occurring during status requests.
     */
    protected IWorkerStatusRequestErrorHandler<HttpWorker, Throwable> statusRequestErrorHandler = new DefaultAbstractWorkerStatusRequestErrorHandler();
    /**
     * this handler handles errors during initialization.
     */
    protected IWorkerInitializeErrorHandler<HttpWorker, Throwable> initializeErrorHandler = new DefaultAbstractWorkerInitializeErrorHandler();
    private String endpoint;
    private HttpNetworkHandler networkHandler;

    /**
     * Creates a new httpWorker.
     *
     * @param config        the configuration of the worker.
     * @param server        the http server for incoming results.
     * @param resultTimeout the maximum time for the time between sending a request and receiving the result.
     */
    public HttpWorker(WorkerConfiguration config, HttpServer server, long resultTimeout) {
        super(config, resultTimeout);
        this.server = server;
    }

    public HttpWorker(WorkerConfiguration config, HttpServer server, long resultTimeout, HttpNetworkHandler networkHandler) {
        super(config, resultTimeout);
        this.server = server;
        this.networkHandler = networkHandler;
    }

    /**
     * A setter for the endpoint of this worker. Endpoint is used for receiving results.
     *
     * @param endpoint endpoint of this worker
     */
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    /**
     * A setter for the statusErrorHandler.
     *
     * @param handler handler
     */
    public void setStatusRequestErrorHandler(IWorkerStatusRequestErrorHandler<HttpWorker, Throwable> handler) {
        this.statusRequestErrorHandler = handler;
    }

    /**
     * A setter for the initializeErrorHandler
     *
     * @param handler handler
     */
    public void setInitializeErrorHandler(IWorkerInitializeErrorHandler<HttpWorker, Throwable> handler) {
        this.initializeErrorHandler = handler;
    }

    /**
     * A setter for the sendErrorHandler
     *
     * @param handler handler
     */
    public void setSendErrorHandler(IWorkerSendErrorHandler<HttpWorker, Throwable> handler) {
        this.sendErrorHandler = handler;
    }

    /**
     * Creates the {@linkplain HttpNetworkHandler}. After that it send a statusRequest and updates
     * the status of the worker. If an exception occurs the {@linkplain IWorkerInitializeErrorHandler}
     * handles the exception.
     */
    @Override
    public synchronized void initialize() {
        try {
            server.addWorker(this);
            if (networkHandler == null)
                networkHandler = new HttpNetworkHandler();

            WorkerInformation info = networkHandler.initializeRequest(config.getLocation(), endpoint, server.getPort());
            changeStatus(info);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("worker interrupted during initialization");
        } catch (Exception e) {
            initializeErrorHandler.handleError(this, e);
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        server.removeWorker(this);
    }

    /**
     * Sends a status request via the {@linkplain HttpNetworkHandler}. The {@linkplain IWorkerStatusRequestErrorHandler} handles
     * errors occurring.
     */
    public void statusRequest() {
        assert (endpoint != null);

        try {
            WorkerInformation info = networkHandler.statusRequest(config.getLocation());
            changeStatus(info);
        } catch (InterruptedException e) {
            logger.warning("Worker interrupted during status request. location: " + config.getLocation());
            Thread.currentThread().interrupt();
        } catch (TimeoutException | ExecutionException e) {
            if (status != WorkerStatus.CONNECTION_FAILED) notifyStatusChangeListeners();
            status = WorkerStatus.CONNECTION_FAILED;
            statusRequestErrorHandler.handleStatusError(this, e);
        }
    }

    /**
     * Send the next request. Waits if the requestSource is empty. Errors occurring during
     * sending are handled by the {@linkplain IWorkerSendErrorHandler}.
     */
    public void sendRequest() {
        ICompletableFrameworkAudioRequest request = null;
        try {
            request = source.next(nextWaitingTime(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (request == null) return;

        synchronized (this) {
            try {
                IWorkerAudioRequest workerAudioRequest = request.getWorkerAudioRequest();

                networkHandler.sendRequest(workerAudioRequest, config.getLocation());

                requestTimes.add(System.currentTimeMillis());
                requests.add(request);
                currentQueueSize++;

                changeStatus();

            } catch (TimeoutException e) {
                if (status != WorkerStatus.CONNECTION_FAILED) notifyStatusChangeListeners();
                status = WorkerStatus.CONNECTION_FAILED;
                sendErrorHandler.errorOnSend(this, request, e);
            } catch (ExecutionException e) {
                if (status != WorkerStatus.CONNECTION_FAILED) notifyStatusChangeListeners();
                status = WorkerStatus.CONNECTION_FAILED;
                sendErrorHandler.errorOnSend(this, request, e.getCause());
            } catch (InterruptedException e) {
                sendErrorHandler.errorOnSend(this, request, e.getCause());
                logger.warning("Worker interrupted during sending request.");
                Thread.currentThread().interrupt();
            }
        }
    }
}
