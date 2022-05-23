package de.speech.worker.remote;

import de.speech.core.dispatcher.IWorkerAudioRequest;
import de.speech.core.dispatcher.WorkerInformation;
import org.eclipse.jetty.client.HttpClient;

/**
 * Handles a network events
 */
public interface INetworkHandler {

    /**
     * The method called when the core wants to initialize the connection
     *
     * @param address  the address of the core
     * @param endpoint the endpoint the worker should send results to
     * @throws Exception if there was an error initializing the client, see {@link HttpClient#start()}
     */
    void handleInit(String address, String endpoint) throws Exception;

    /**
     * The method called when an {@linkplain IWorkerAudioRequest} is received from the core
     *
     * @param request the received {@linkplain IWorkerAudioRequest}
     */
    void handleAudioRequest(IWorkerAudioRequest request);

    /**
     * The method called when the core wants to get information about the worker
     *
     * @return a {@linkplain WorkerInformation} with information about the worker
     */
    WorkerInformation handleInformationRequest();

    /**
     * Sets the {@linkplain IRemoteConnection} to use
     *
     * @param connection the {@linkplain IRemoteConnection} to use
     */
    void setRemoteConnection(IRemoteConnection connection);
}
