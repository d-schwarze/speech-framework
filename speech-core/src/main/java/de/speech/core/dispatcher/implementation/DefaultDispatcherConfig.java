package de.speech.core.dispatcher.implementation;

import de.speech.core.dispatcher.implementation.httpworker.HttpOneThreadPerWorkerCoreFactory;

/**
 * The default configuration of the dispatcher. It contains a HttpWorkerFactory.
 */
public class DefaultDispatcherConfig extends DispatcherConfig {

    private static final int RESULT_TIMEOUT = 15000;
    private static final int QUEUE_SIZE = 1024;
    private static final int ACCEPTORS = 1;
    private static final int SELECTORS = 1;
    private static final int PORT = 2999;
    private static final long DEFAULT_HTTP_REQUEST_TIMEOUT = 50000;

    public DefaultDispatcherConfig() {
        super(new HttpOneThreadPerWorkerCoreFactory(RESULT_TIMEOUT, DEFAULT_HTTP_REQUEST_TIMEOUT, PORT, ACCEPTORS, SELECTORS, QUEUE_SIZE));
    }
}
