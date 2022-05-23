package de.speech.worker.local;

import de.speech.core.dispatcher.IFrameworkResult;
import de.speech.worker.remote.IRemoteConnection;

/**
 * The main {@linkplain IResultHandler} implementation. Just sends the results back to core
 */
public class ResultHandler implements IResultHandler {

    private final IRemoteConnection connection;

    /**
     * Creates a new {@linkplain ResultHandler} with the specified {@linkplain IRemoteConnection} to the core
     *
     * @param connection the {@linkplain IRemoteConnection} to the core
     */
    public ResultHandler(IRemoteConnection connection) {
        this.connection = connection;
    }

    @Override
    public void handleResult(IFrameworkResult result) {
        connection.sendToCore(result);
    }
}
