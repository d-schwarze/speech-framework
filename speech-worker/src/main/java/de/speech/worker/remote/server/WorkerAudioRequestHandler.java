package de.speech.worker.remote.server;

import de.speech.core.dispatcher.IWorkerAudioRequest;
import de.speech.core.dispatcher.implementation.RequestUtils;
import de.speech.worker.remote.INetworkHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * The handler for accepting new {@linkplain IWorkerAudioRequest}s on the / endpoint
 */
public class WorkerAudioRequestHandler extends CustomHandler {

    /**
     * Creates a new {@linkplain WorkerAudioRequestHandler} with the specified {@linkplain INetworkHandler}
     *
     * @param networkHandler the {@linkplain INetworkHandler} to use
     */
    public WorkerAudioRequestHandler(INetworkHandler networkHandler) {
        super("/request", networkHandler);
    }

    @Override
    public void handleRequest(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        baseRequest.setHandled(true);
        IWorkerAudioRequest workerAudioRequest;
        try {
            workerAudioRequest = RequestUtils.requestToIWorkerAudioRequest(request);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain");
            PrintWriter writer = response.getWriter();
            writer.println(e.getMessage());
            writer.flush();
            return;
        }

        try {
            networkHandler.handleAudioRequest(workerAudioRequest);
        } catch (IllegalStateException e) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.println("the queue has reached it's maximum size");
            writer.flush();
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain");
            PrintWriter writer = response.getWriter();
            writer.println("not all preprocesses are available");
            writer.flush();
        }
    }
}
