package de.speech.worker.remote.server;

import de.speech.worker.remote.INetworkHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.io.IOException;

/**
 * A simple handler that is used for all endpoints
 */
public abstract class CustomHandler extends AbstractHandler {

    private final String endpoint;
    protected INetworkHandler networkHandler;

    /**
     * Creates a new {@linkplain CustomHandler}
     *
     * @param endpoint       the endpoint this handler is for
     * @param networkHandler the {@linkplain INetworkHandler} to use
     */
    public CustomHandler(String endpoint, INetworkHandler networkHandler) {
        this.endpoint = endpoint;
        this.networkHandler = networkHandler;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        baseRequest.setHandled(true);
        handleRequest(target, baseRequest, request, response);
    }

    /**
     * The method a handler for a specific endpoint has to implement. For more information about the exceptions: {@link org.eclipse.jetty.server.Handler}
     *
     * @param target      the target {@linkplain String} from jetty, i.e. the endpoint string like /init
     * @param baseRequest the {@linkplain Request} from jetty
     * @param request     the {@linkplain HttpServletRequest} from jetty
     * @param response    the {@linkplain HttpServletResponse} from jetty
     * @throws IOException      if unable to handle the request or response processing
     * @throws ServletException if unable to handle the request or response due to underlying servlet issue
     */
    public abstract void handleRequest(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;

    /**
     * Returns the endpoint is handler is for
     *
     * @return the endpoint of the handler
     */
    public String getEndpoint() {
        return endpoint;
    }
}
