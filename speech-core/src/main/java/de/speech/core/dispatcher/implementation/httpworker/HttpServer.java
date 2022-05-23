package de.speech.core.dispatcher.implementation.httpworker;

import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles incoming results.
 */
public class HttpServer {

    private final int port;
    private final Server server;
    private final HandlerList handlerList;
    private final Map<HttpWorker, HttpWorkerResultHandler> handlerMap = new HashMap<>();
    private int nextIndex;

    /**
     * Creates a new Httpserver.
     */
    public HttpServer(int port, int acceptors, int selectors, int queueSize) {
        this.port = port;
        server = new Server();
        handlerList = new HandlerList();
        server.setHandler(handlerList);
        ServerConnector connector = new ServerConnector(server, acceptors, selectors, new HttpConnectionFactory());
        connector.setPort(port);
        connector.setAcceptQueueSize(queueSize);
        server.addConnector(connector);
        nextIndex = 0;
    }

    /**
     * Stops the server
     *
     * @throws Exception if an exception occurs.
     */
    public void stop() throws Exception {
        server.stop();
    }

    /**
     * Adds a worker to the server and adds a Handler that handle incoming requests.
     *
     * @param worker added worker
     */
    public synchronized void addWorker(HttpWorker worker) throws Exception {
        if (!server.isStarted()) {
            server.start();
        }

        ContextHandler handler = new ContextHandler();
        handler.setAllowNullPathInfo(true);
        String contextPath = "/worker-" + nextIndex;
        nextIndex++;
        worker.setEndpoint(contextPath);
        HttpWorkerResultHandler h = new HttpWorkerResultHandler(worker, contextPath);
        handlerList.addHandler(h);
        handlerMap.put(worker, h);
    }

    public synchronized void removeWorker(HttpWorker worker) throws Exception {
        handlerList.removeHandler(handlerMap.get(worker));

        if (handlerList.getHandlers() == null || handlerList.getHandlers().length == 0) {
            server.stop();
        }
    }

    public int getPort() {
        return port;
    }
}
