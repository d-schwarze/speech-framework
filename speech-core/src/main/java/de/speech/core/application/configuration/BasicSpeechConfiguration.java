package de.speech.core.application.configuration;

import java.util.ArrayList;
import java.util.List;

public class BasicSpeechConfiguration extends PlainSpeechConfiguration {

    public final static int DEFAULT_RESULT_TIMEOUT = 10000;

    public final static int DEFAULT_HTTP_TIMEOUT = 60000;

    public final static int DEFAULT_PORT = 8080;

    public final static int DEFAULT_ACCEPTORS = 10;

    public final static int DEFAULT_SELECTORS = 10;

    public final static int DEFAULT_QUEUE_SIZE = 100;

    private List<WorkerConfiguration> workers = new ArrayList<>();

    public BasicSpeechConfiguration() {
        super(DEFAULT_RESULT_TIMEOUT, DEFAULT_HTTP_TIMEOUT, DEFAULT_PORT, DEFAULT_ACCEPTORS, DEFAULT_SELECTORS, DEFAULT_QUEUE_SIZE);
    }

    @Override
    public List<WorkerConfiguration> getWorkers() {
        return this.workers;
    }
}
