package de.speech.worker.remote;

import de.speech.core.dispatcher.IFrameworkResult;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.server.Server;

/**
 * The interface used to interact with the underlying network code
 */
public interface IRemoteConnection {

    /**
     * Sends an object to the core module
     *
     * @param result The {@linkplain IFrameworkResult} to send
     */
    void sendToCore(IFrameworkResult result);

    /**
     * Sets the handler used for receiving objects
     *
     * @param handler The handler to use
     */
    void setHandler(INetworkHandler handler);

    /**
     * Used to initialize the client after the core has send an init request
     *
     * @param address  the address of the core
     * @param endpoint the endpoint the worker should send results to
     * @throws Exception if there was an error initializing the client, see {@link HttpClient#start()}
     */
    void initClient(String address, String endpoint) throws Exception;

    /**
     * Shuts down the connection
     *
     * @throws Exception if there was an error shutting down the server or the client, see {@link Server#stop()} and {@link HttpClient#start()}
     */
    void shutdown() throws Exception;
}
