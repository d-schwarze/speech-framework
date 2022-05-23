package de.speech.worker.remote.server;

import com.google.gson.Gson;
import de.speech.core.dispatcher.WorkerInformation;
import de.speech.worker.remote.INetworkHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;

import java.io.IOException;

/**
 * The handler for the /info endpoint that returns information about the worker like name, model, maximum queue size and current queue size
 */
public class InformationHandler extends CustomHandler {

    /**
     * Creates a new {@linkplain InformationHandler} with the specified {@linkplain INetworkHandler}
     *
     * @param handler the {@linkplain INetworkHandler} to use
     */
    public InformationHandler(INetworkHandler handler) {
        super("/info", handler);
    }

    @Override
    public void handleRequest(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        baseRequest.setHandled(true);
        WorkerInformation info = networkHandler.handleInformationRequest();
        Gson gson = new Gson();
        String json = gson.toJson(info);
        response.getWriter().println(json);
    }
}
