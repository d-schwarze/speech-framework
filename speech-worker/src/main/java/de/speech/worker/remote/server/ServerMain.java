package de.speech.worker.remote.server;

import de.speech.worker.remote.INetworkHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import java.util.Arrays;
import java.util.List;

/**
 * The main server class that creates the webserver
 */
public class ServerMain {

    private Server server;
    private final int port;
    private final List<CustomHandler> endpoints;

    /**
     * Creates a new {@linkplain ServerMain} with the specified {@linkplain INetworkHandler} and {@code port}
     *
     * @param networkHandler the {@linkplain INetworkHandler} to use for handling requests
     * @param port           the port to use for the webserver
     * @throws Exception if there was an error creating the server, see {@link Server#start()}
     */
    public ServerMain(INetworkHandler networkHandler, int port) throws Exception {
        this.port = port;
        endpoints = Arrays.asList(
                new InitHandler(networkHandler),
                new InformationHandler(networkHandler),
                new WorkerAudioRequestHandler(networkHandler)
        );
        setupServer();
    }

    private void setupServer() throws Exception {
        server = new Server(port);

        ContextHandlerCollection contextCollection = new ContextHandlerCollection();
        server.setHandler(contextCollection);

        endpoints.forEach(handler -> {
            ContextHandler context = new ContextHandler();
            context.setContextPath(handler.getEndpoint());
            context.setAllowNullPathInfo(true);
            context.setHandler(handler);

            contextCollection.addHandler(context);
        });

        server.start();
    }

    /**
     * Shuts down the server
     *
     * @throws Exception if there was an error shutting down the server, see {@link Server#stop()}
     */
    public void shutdown() throws Exception {
        server.stop();
    }
}
