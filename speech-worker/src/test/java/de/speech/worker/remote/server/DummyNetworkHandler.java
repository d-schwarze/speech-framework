package de.speech.worker.remote.server;

import de.speech.core.dispatcher.IWorkerAudioRequest;
import de.speech.core.dispatcher.WorkerInformation;
import de.speech.worker.remote.INetworkHandler;
import de.speech.worker.remote.IRemoteConnection;

public class DummyNetworkHandler implements INetworkHandler {

    private String address;
    private String endpoint;
    private int port;
    private IWorkerAudioRequest request;
    private IRemoteConnection connection;

    @Override
    public void handleInit(String address, String endpoint) {
        this.address = address;
        this.endpoint = endpoint;
        String[] split = address.split(":");
        this.port = Integer.parseInt(split[split.length - 1]);
    }

    @Override
    public void handleAudioRequest(IWorkerAudioRequest request) {
        this.request = request;
    }

    @Override
    public WorkerInformation handleInformationRequest() {
        return new WorkerInformation("dummy worker", "dummy model", 3, 1);
    }

    @Override
    public void setRemoteConnection(IRemoteConnection connection) {
        this.connection = connection;
    }

    public String getAddress() {
        return address;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public int getPort() {
        return port;
    }

    public IWorkerAudioRequest getRequest() {
        return request;
    }

    public IRemoteConnection getConnection() {
        return connection;
    }
}
