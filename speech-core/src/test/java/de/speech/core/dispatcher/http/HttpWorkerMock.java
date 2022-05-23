package de.speech.core.dispatcher.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.speech.core.dispatcher.IFrameworkResult;
import de.speech.core.dispatcher.IWorkerAudioRequest;
import de.speech.core.dispatcher.SpeechToTextServiceDataMock;
import de.speech.core.dispatcher.WorkerInformation;
import de.speech.core.dispatcher.implementation.JsonInterfaceAdapter;
import de.speech.core.dispatcher.implementation.RequestUtils;
import de.speech.core.dispatcher.implementation.httpworker.HttpNetworkHandler;
import de.speech.core.dispatcher.implementation.requestresult.FrameworkResult;
import de.speech.core.task.result.ISpeechToTextServiceData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.util.StringRequestContent;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.*;

public class HttpWorkerMock {

    private static final int TIMEOUT = 10000;
    private final Server server;
    private final String model;
    private final String name;
    private final int port;
    private Thread t;
    private boolean processing;
    private int queue_size = 10;
    private final BlockingQueue<IWorkerAudioRequest> requests = new LinkedBlockingQueue<>(queue_size);
    private String endpoint;
    private long processing_time;
    private int core_port;

    public HttpWorkerMock(boolean processing, String framework, String model, int port, int processing_time) throws Exception {
        this.model = model;
        this.name = framework;
        this.processing = processing;
        this.port = port;
        this.processing_time = processing_time;
        server = new Server();

        ServerConnector connector = new ServerConnector(server, 1, 1, new HttpConnectionFactory());
        connector.setPort(port);
        connector.setHost("localhost");
        connector.setAcceptQueueSize(128);
        server.addConnector(connector);


        createHandler();
        server.start();

        t = new WorkerThread();
        t.start();
    }

    public void setQueueSize(int size) {
        this.queue_size = size;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
        t.interrupt();
        t = new WorkerThread();
        t.start();
    }

    public void setProcessingTime(long time) {
        this.processing_time = time;
    }

    public void clearRequests() {
        this.requests.clear();
    }

    public void addRequest(IWorkerAudioRequest request) {
        this.requests.add(request);
    }

    public WorkerInformation getInformation() {
        return new WorkerInformation(name, model, queue_size, requests.size());
    }

    public void createHandler() {
        HandlerList handlers = new HandlerList();

        handlers.addHandler(new StatusHandler());

        handlers.addHandler(new RequestHandler());

        handlers.addHandler(new InitRequestHandler());

        server.setHandler(handlers);
    }

    public void stop() throws Exception {
        t.interrupt();
        server.stop();
    }

    class InitRequestHandler extends AbstractHandler {

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse resp) throws IOException {
            if (target.equals(HttpNetworkHandler.INIT_ENDPOINT)) {
                baseRequest.setHandled(true);
                String jsonStr = new Gson().toJson(getInformation());
                endpoint = baseRequest.getParameter("endpoint");
                core_port = Integer.parseInt(baseRequest.getParameter("port"));
                resp.setCharacterEncoding("utf-8");
                resp.addHeader("Access-Control-Allow-Origin", "*");
                resp.addHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
                resp.setContentType("application/json");

                try (PrintWriter writer = resp.getWriter()) {
                    writer.write(jsonStr);
                    resp.setStatus(200);
                }
            }
        }
    }

    class WorkerThread extends Thread {

        private final HttpClient client;

        public WorkerThread() {
            client = new HttpClient();
            try {
                client.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    if (processing) {
                        IWorkerAudioRequest request = requests.take();
                        Thread.sleep(processing_time);
                        IFrameworkResult result = new FrameworkResult(request.getId(), new SpeechToTextServiceDataMock(name, model, "sentence"));

                        GsonBuilder builder = new GsonBuilder();
                        builder.registerTypeAdapter(ISpeechToTextServiceData.class, new JsonInterfaceAdapter<FrameworkResult>());
                        Gson gson = builder.create();

                        String json = gson.toJson(result);
                        client.POST("http://127.0.0.1:" + core_port + endpoint).body(new StringRequestContent(json))
                                .timeout(TIMEOUT, TimeUnit.MILLISECONDS)
                                .send();
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class RequestHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
            if (target.equals(HttpNetworkHandler.REQUEST_ENDPOINT)) {
                baseRequest.setHandled(true);
                requests.add(RequestUtils.requestToIWorkerAudioRequest(request));
                response.setStatus(200);
            }
        }
    }

    class StatusHandler extends AbstractHandler {

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse resp) throws IOException {
            if (target.equals(HttpNetworkHandler.STATUS_ENDPOINT)) {
                baseRequest.setHandled(true);

                String jsonStr = new Gson().toJson(getInformation());

                try (PrintWriter writer = resp.getWriter()) {
                    writer.write(jsonStr);
                    resp.setStatus(200);
                }
            }
        }
    }
}
