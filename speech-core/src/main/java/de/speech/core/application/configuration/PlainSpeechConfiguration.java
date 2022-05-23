package de.speech.core.application.configuration;

public abstract class PlainSpeechConfiguration implements SpeechConfiguration {

    protected int resultTimeout;

    protected int httpTimeout;

    protected int port;

    protected int acceptors;

    protected int selectors;

    protected int queueSize;

    public PlainSpeechConfiguration() {}

    public PlainSpeechConfiguration(int resultTimeout, int httpTimeout, int port, int acceptors, int selectors, int queueSize) {
        this.resultTimeout = resultTimeout;
        this.httpTimeout = httpTimeout;
        this.port = port;
        this.acceptors = acceptors;
        this.selectors = selectors;
        this.queueSize = queueSize;
    }

    @Override
    public int getResultTimeout() {
        return this.resultTimeout;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public int getAcceptors() {
        return this.acceptors;
    }

    @Override
    public int getSelectors() {
        return this.selectors;
    }

    @Override
    public int getQueueSize() {
        return this.queueSize;
    }

    @Override
    public int getHttpTimeout() {
        return this.httpTimeout;
    }
}
