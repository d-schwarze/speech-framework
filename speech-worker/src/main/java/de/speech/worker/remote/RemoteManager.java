package de.speech.worker.remote;

import de.speech.worker.local.IWorkerServer;

/**
 * The main entry point for the remote package
 */
public class RemoteManager {

    private IRemoteConnection connection;

    /**
     * Creates a new {@linkplain RemoteManager}
     *
     * @param workerServer the {@linkplain IWorkerServer} to use
     * @param port         the port of the server
     */
    public RemoteManager(IWorkerServer workerServer, int port) {
        INetworkHandler networkHandler = new NetworkHandler(workerServer);
        try {
            connection = new RemoteConnection(port, networkHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shuts down the {@linkplain RemoteManager}
     *
     * @throws Exception if there was an error shutting down the connection, see {@link IRemoteConnection#shutdown()}
     */
    public void shutdown() throws Exception {
        connection.shutdown();
    }

    /**
     * Returns the used {@linkplain IRemoteConnection}
     *
     * @return the used {@linkplain IRemoteConnection}
     */
    public IRemoteConnection getConnection() {
        return connection;
    }
}
