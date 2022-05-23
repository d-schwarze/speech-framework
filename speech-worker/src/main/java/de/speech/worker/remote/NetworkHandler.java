package de.speech.worker.remote;

import de.speech.core.dispatcher.IWorkerAudioRequest;
import de.speech.core.dispatcher.WorkerInformation;
import de.speech.worker.local.IWorkerServer;

/**
 * The main implementation of the {@linkplain INetworkHandler} interface
 */
public class NetworkHandler implements INetworkHandler {

    private final IWorkerServer workerServer;
    private IRemoteConnection connection;

    /**
     * Creates a new {@linkplain NetworkHandler}
     *
     * @param workerServer the {@linkplain IWorkerServer} to be used
     */
    public NetworkHandler(IWorkerServer workerServer) {
        this.workerServer = workerServer;
    }

    @Override
    public void handleInit(String address, String endpoint) throws Exception {
        connection.initClient(address, endpoint);
    }

    @Override
    public void handleAudioRequest(IWorkerAudioRequest request) throws IllegalStateException {
        workerServer.submitWork(request);
    }

    @Override
    public WorkerInformation handleInformationRequest() {
        return new WorkerInformation(
                workerServer.getName(),
                workerServer.getModel(),
                workerServer.getMaxQueueSize(),
                workerServer.getQueueSize());
    }

    @Override
    public void setRemoteConnection(IRemoteConnection connection) {
        this.connection = connection;
    }
}
