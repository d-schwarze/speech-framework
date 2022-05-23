package de.speech.core.application.configuration;

import de.speech.core.application.annotation.ApplicationConfiguration;
import de.speech.core.application.configuration.json.JsonWorkerConfiguration;

import java.util.ArrayList;
import java.util.List;

@ApplicationConfiguration
public class CustomAnnotatedSpeechConfiguration implements SpeechConfiguration {

    private List<WorkerConfiguration> workers = new ArrayList<>();

    public CustomAnnotatedSpeechConfiguration() {
        workers.add(new JsonWorkerConfiguration("localhost:8082"));
    }

    @Override
    public List<WorkerConfiguration> getWorkers() {
       return workers;
    }

    @Override
    public int getResultTimeout() {
        return 5001;
    }

    @Override
    public int getHttpTimeout() {
        return 5002;
    }

    @Override
    public int getPort() {
        return 8002;
    }

    @Override
    public int getAcceptors() {
        return 10;
    }

    @Override
    public int getSelectors() {
        return 10;
    }

    @Override
    public int getQueueSize() {
        return 10;
    }
}
