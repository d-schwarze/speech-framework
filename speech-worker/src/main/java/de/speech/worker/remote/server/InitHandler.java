package de.speech.worker.remote.server;

import com.google.gson.Gson;
import de.speech.core.dispatcher.WorkerInformation;
import de.speech.worker.WorkerLogger;
import de.speech.worker.remote.INetworkHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * The handler for the /init endpoint that sets the endpoint of the core the worker should send results to
 */
public class InitHandler extends CustomHandler {

    /**
     * Creates a new {@linkplain InitHandler} with the specified {@linkplain INetworkHandler}
     *
     * @param networkHandler the {@linkplain INetworkHandler} to use
     */
    public InitHandler(INetworkHandler networkHandler) {
        super("/init", networkHandler);
    }

    @Override
    public void handleRequest(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        baseRequest.setHandled(true);
        String endpoint = baseRequest.getParameter("endpoint");
        if (endpoint == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter writer = response.getWriter();
            writer.println("missing endpoint parameter");
            writer.flush();
            return;
        }

        int port;
        try {
            port = Integer.parseInt(baseRequest.getParameter("port"));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter writer = response.getWriter();
            writer.println("missing or malformed port parameter");
            writer.flush();
            return;
        }

        String address = request.getRemoteAddr() + ":" + port;

        try {
            networkHandler.handleInit(address, endpoint);
        } catch (Exception e) {
            WorkerLogger.error("Error while init", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter writer = response.getWriter();
            writer.println("There was an internal error initializing the client");
            writer.flush();
            return;
        }

        WorkerInformation info = networkHandler.handleInformationRequest();
        String json = new Gson().toJson(info);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.println(json);
        writer.flush();
    }
}
