package de.speech.test.application;

import de.speech.core.application.configuration.PlainSpeechConfiguration;
import de.speech.core.application.configuration.WorkerConfiguration;

import java.util.List;

public class CustomSpeechConfiguration extends PlainSpeechConfiguration {

    private List<WorkerConfiguration> workers;

    public CustomSpeechConfiguration(List<WorkerConfiguration> workers, int resultTimeout, int httpTimeout, int port, int acceptors, int selectors, int queueSize) {
        super(resultTimeout, httpTimeout, port, acceptors, selectors, queueSize);

        this.workers = workers;
    }

    @Override
    public List<WorkerConfiguration> getWorkers() {
        return workers;
    }
}
