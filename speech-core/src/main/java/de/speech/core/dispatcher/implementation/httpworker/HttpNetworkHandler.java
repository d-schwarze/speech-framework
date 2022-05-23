package de.speech.core.dispatcher.implementation.httpworker;

import com.google.gson.Gson;
import de.speech.core.dispatcher.IStatusChangeListener;
import de.speech.core.dispatcher.IWorkerAudioRequest;
import de.speech.core.dispatcher.IWorkerCore;
import de.speech.core.dispatcher.WorkerInformation;
import de.speech.core.dispatcher.implementation.RequestUtils;
import de.speech.core.dispatcher.implementation.WorkerStatus;
import de.speech.core.dispatcher.implementation.exception.*;
import de.speech.core.logging.SpeechLogging;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpNetworkHandler implements IStatusChangeListener {

    private static final Logger logger = SpeechLogging.getLogger();

    /**
     * Init endpoint on the worker.
     */
    public static final String INIT_ENDPOINT = "/init";

    /**
     * Info endpoint on the worker.
     */
    public static final String STATUS_ENDPOINT = "/info";

    /**
     * Request endpoint on the worker.
     */
    public static final String REQUEST_ENDPOINT = "/request";

    /**
     * Request timeout.
     */
    public static final int DEFAULT_REQUEST_TIMEOUT = 50000;

    /**
     * Name of the endpoint parameter.
     */
    public static final String ENDPOINT_PARAMETER = "endpoint";

    /**
     * Name of the port parameter. It is the port of the server on the core side.
     */
    public static final String PORT_PARAMETER = "port";

    /**
     * Port or endpoint parameter is missing.
     */
    public static final int BAD_REQUEST = 402;
    /**
     * Exception on client init.
     */
    public static final int HTTP_CLIENT_EXCEPTION = 500;

    /**
     * Worker can not parse the request.
     */
    public static final int PARSING_ERROR = 500;

    /**
     * Service unavailable. Queue full
     */
    public static final int SERVICE_UNAVAILABLE = 503;

    /**
     * The worker has not all preprocesses.
     */
    public static final int PREPROCESS_MISSING = 400;

    private final HttpClient client;

    private final long requestTimeout;

    private final List<IWorkerCore> workerList = new LinkedList<>();

    public void addHttpWorker(HttpWorker worker) {
        this.workerList.add(worker);
    }

    /**
     * Creates a new networkHandler.
     *
     * @throws Exception if an exception occurs during starting of a httpclient.
     */
    public HttpNetworkHandler() throws Exception {
        requestTimeout = DEFAULT_REQUEST_TIMEOUT;

        client = new HttpClient();
        client.setFollowRedirects(false);

        client.start();
    }

    /**
     * Creates a new networkHandler.
     *
     * @param requestTimeout requestTimeout
     * @throws Exception if an exception occurs during starting of a httpclient.
     */
    public HttpNetworkHandler(long requestTimeout) throws Exception {
        this.requestTimeout = requestTimeout;

        client = new HttpClient();
        client.setFollowRedirects(false);

        client.start();
    }

    /**
     * Stops the networkHandler.
     *
     * @throws Exception if it fails to stop
     */
    public void stop() throws Exception {
        if (!client.isStopped())
            client.stop();
    }

    /**
     * Send an initialization request. Returns the information of the worker.
     *
     * @param location location of the worker
     * @param endpoint the endpoint of the worker
     * @param port     the port of the server for results.
     * @return information of the worker
     * @throws ExecutionException   if an error occurs during the request.
     * @throws InterruptedException if interrupted during sending
     */
    public WorkerInformation initializeRequest(String location, String endpoint, int port) throws ExecutionException, InterruptedException, TimeoutException {
        ContentResponse response = client.POST(location + INIT_ENDPOINT)
                .param(ENDPOINT_PARAMETER, endpoint)
                .param(PORT_PARAMETER, String.valueOf(port))
                .timeout(requestTimeout, TimeUnit.MILLISECONDS)
                .send();

        try {
            int status = response.getStatus();

            if (status == 200) {
                String string = response.getContentAsString();
                return new Gson().fromJson(string, WorkerInformation.class);
            } else if (status == BAD_REQUEST) {
                throw new IllegalArgumentException("Worker received not all arguments");
            } else if (status == HTTP_CLIENT_EXCEPTION) {
                throw new WorkerInitializationException("Worker could not initialize client");
            } else {
                throw new UnknownStatusCodeException("False status code " + status);
            }

        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

    /**
     * Sends a status request to the worker.
     *
     * @param location location of the worker
     * @return the information of this worker
     * @throws InterruptedException if interrupted during the request.
     * @throws ExecutionException   if an exception occurs during sending.
     */
    public WorkerInformation statusRequest(String location) throws ExecutionException, InterruptedException, TimeoutException {
        ContentResponse response = client.POST(location + STATUS_ENDPOINT)
                .timeout(requestTimeout, TimeUnit.MILLISECONDS)
                .send();

        try {
            int status = response.getStatus();

            if (status == 200) {
                String string = response.getContentAsString();
                return new Gson().fromJson(string, WorkerInformation.class);
            } else {
                throw new UnknownStatusCodeException("False status code");
            }

        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

    /**
     * Sends a request to the worker.
     *
     * @param request  request
     * @param location location of the worker
     * @throws InterruptedException if interrupted during sending
     * @throws ExecutionException   if an error occurs during sending
     * @throws TimeoutException     if the request times out
     */
    public void sendRequest(IWorkerAudioRequest request, String location) throws InterruptedException, ExecutionException, TimeoutException {
        ContentResponse response;
        try {
            response = RequestUtils.iWorkerAudioRequestToRequest(request, client, location + REQUEST_ENDPOINT).
                    timeout(requestTimeout, TimeUnit.MILLISECONDS)
                    .send();
        } catch (IOException | UnsupportedAudioFileException e) {
            throw new ExecutionException(e);
        }

        try {
            int status = response.getStatus();

            if (status == PARSING_ERROR) {
                throw new WorkerParseException("Worker could not parse request");
            } else if (status == SERVICE_UNAVAILABLE) {
                throw new QueueFullException("Queue full");
            } else if (status == PREPROCESS_MISSING) {
                throw new PreProcessesMissingException("preprocesses missing");
            } else if (status != 200) {
                throw new UnknownStatusCodeException("unknown status code");
            }

        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    public void statusChanged(IWorkerCore worker) {
        if (worker.getStatus() == WorkerStatus.WORKER_STOPPED) {
            worker.removeStatusChangeListener(this);
            this.workerList.remove(worker);
            if (this.workerList.isEmpty()) {
                try {
                    stop();
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Could not stop the httpClient", e);
                }
            }
        }
    }
}
