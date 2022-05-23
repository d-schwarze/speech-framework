package de.speech.core.dispatcher.implementation;

import de.speech.core.dispatcher.implementation.httpworker.HttpOneThreadPerWorkerCoreFactory;

public class HttpDispatcherConfig extends DispatcherConfig {

    /**
     * Creates a new HttpDispatcherConfig
     * @param resultTimeout maximum time for processing requests
     * @param port port of the httpServer
     * @param acceptors acceptors of the httpServer
     * @param selectors selectors of the httpServer
     * @param queueSize size of the queue of the httpServer
     */
    public HttpDispatcherConfig(long resultTimeout, long httpRequestTimeout, int port, int acceptors, int selectors, int queueSize) {
        super(new HttpOneThreadPerWorkerCoreFactory(resultTimeout, httpRequestTimeout, port, acceptors, selectors, queueSize));
    }
}
