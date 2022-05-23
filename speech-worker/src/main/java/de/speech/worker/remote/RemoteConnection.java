package de.speech.worker.remote;

import de.speech.core.dispatcher.IFrameworkResult;
import de.speech.worker.remote.server.ServerMain;
import org.eclipse.jetty.server.Server;

/**
 * The main implementation of the {@linkplain IRemoteConnection} interface
 */
public class RemoteConnection implements IRemoteConnection {

    private ClientMain client;
    private final ServerMain server;
    private INetworkHandler networkHandler;

    /**
     * Creates a new {@linkplain RemoteConnection}
     *
     * @param port           the port the server runs on
     * @param networkHandler the {@linkplain INetworkHandler} to use for handling requests
     * @throws Exception if there was an error creating the server, see {@link Server#start()}
     */
    public RemoteConnection(int port, INetworkHandler networkHandler) throws Exception {
        this.networkHandler = networkHandler;
        networkHandler.setRemoteConnection(this);
        server = new ServerMain(networkHandler, port);
    }

    @Override
    public void initClient(String address, String endpoint) throws Exception {
        client = new ClientMain(address, endpoint);
    }

    @Override
    public void sendToCore(IFrameworkResult result) {
        client.sendResultToCore(result);
    }

    @Override
    public void setHandler(INetworkHandler handler) {
        this.networkHandler = handler;
        this.networkHandler.setRemoteConnection(this);
    }

    @Override
    public void shutdown() throws Exception {
        if (client != null) {
            client.shutdown();
        }
        server.shutdown();
    }
}
