package de.speech.core.dispatcher.implementation.httpworker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.speech.core.dispatcher.IFrameworkResult;
import de.speech.core.dispatcher.implementation.JsonInterfaceAdapter;
import de.speech.core.dispatcher.implementation.requestresult.FrameworkResult;
import de.speech.core.task.result.ISpeechToTextServiceData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;


/**
 * Handles incoming http requests with results.
 */
public class HttpWorkerResultHandler extends AbstractHandler {

    private final HttpWorker worker;
    private final String endpoint;

    public HttpWorkerResultHandler(HttpWorker worker, String endpoint) {
        this.endpoint = endpoint;
        this.worker = worker;
    }

    /**
     * A getter for the worker.
     *
     * @return worker
     */
    public HttpWorker getWorker() {
        return worker;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        if (target.equals(endpoint)) {
            try {
                String json = new String(baseRequest.getInputStream().readAllBytes());

                GsonBuilder builder = new GsonBuilder();
                builder.registerTypeAdapter(ISpeechToTextServiceData.class, new JsonInterfaceAdapter<FrameworkResult>());
                Gson gson = builder.create();

                IFrameworkResult result = gson.fromJson(json, FrameworkResult.class);

                worker.handleNextResult(result);
                baseRequest.setHandled(true);
                response.setStatus(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
