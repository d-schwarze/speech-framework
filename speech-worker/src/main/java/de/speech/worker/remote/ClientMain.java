package de.speech.worker.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.speech.core.dispatcher.IFrameworkResult;
import de.speech.core.dispatcher.implementation.JsonInterfaceAdapter;
import de.speech.core.dispatcher.implementation.requestresult.FrameworkResult;
import de.speech.core.task.result.ISpeechToTextServiceData;
import de.speech.worker.WorkerLogger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringRequestContent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The client used for sending {@linkplain IFrameworkResult}s back to the core
 */
public class ClientMain {

    private final HttpClient client;
    private final String uri;
    private final Timer timer;
    private final BlockingQueue<IFrameworkResult> results;
    private final Thread queueWorker;

    /**
     * Creates a new {@linkplain ClientMain} with the {@code address} of the core and the {@code endpoint} to use
     *
     * @param address  the address of the core
     * @param endpoint the endpoint that the core assigned this worker to
     * @throws Exception if there was an error starting the client, see {@link HttpClient#start()}
     */
    public ClientMain(String address, String endpoint) throws Exception {
        if (!endpoint.startsWith("/")) {
            endpoint = "/" + endpoint;
        }

        if (!address.startsWith("http://")) {
            address = "http://" + address;
        }

        this.uri = address + endpoint;
        client = new HttpClient();
        client.start();
        timer = new Timer();
        results = new LinkedBlockingQueue<>();
        queueWorker = new Thread(new QueueWorker());
        queueWorker.start();
    }

    /**
     * Shuts down the client
     *
     * @throws Exception if there was an error shutting down the client, see {@link HttpClient#stop()}
     */
    public void shutdown() throws Exception {
        client.stop();
        queueWorker.interrupt();
        queueWorker.join();
    }

    /**
     * Sends an {@link IFrameworkResult} to the core
     *
     * @param result the {@linkplain IFrameworkResult} to send
     */
    public void sendResultToCore(IFrameworkResult result) {
        results.add(result);
    }

    private class QueueWorker implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                IFrameworkResult result = null;
                try {
                    result = results.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(ISpeechToTextServiceData.class, new JsonInterfaceAdapter<FrameworkResult>())
                        .create();
                String json = gson.toJson(result);
                Request request = client.POST(uri)
                        .body(new StringRequestContent("application/json", json));
                AtomicReference<ContentResponse> response = new AtomicReference<>();

                AtomicInteger count = new AtomicInteger(0);

                IFrameworkResult finalResult = result;
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            response.set(request.send());
                            // got response, cancel timer
                            cancel();
                        } catch (InterruptedException | TimeoutException | ExecutionException e) {
                            count.incrementAndGet();
                            if (count.get() == 5) {
                                // no response after trying 5 times, cancel timer, add to queue again
                                cancel();
                                WorkerLogger.error("Error sending result to core", e);
                                results.add(finalResult);
                            }
                        }
                    }
                };

                timer.schedule(task, 0, 10000);

                if (response.get() != null && response.get().getStatus() != 200) {
                    WorkerLogger.error("Received status " + response.get().getStatus() + " " + response.get().getReason() + " from core");
                }
            }
        }
    }
}
