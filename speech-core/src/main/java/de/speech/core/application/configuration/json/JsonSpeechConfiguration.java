package de.speech.core.application.configuration.json;

import de.speech.core.application.configuration.PlainSpeechConfiguration;
import de.speech.core.application.configuration.WorkerConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads the default configuration file.
 * If you just want to adapt the given default config, you may extends this class and overwrite
 * {@linkplain #getWorkers()} to get the default config file and modify it. Note, that you need
 * add {@linkplain de.speech.core.application.annotation.ApplicationConfiguration}.
 */
public class JsonSpeechConfiguration extends PlainSpeechConfiguration {

    private List<JsonWorkerConfiguration> workers = new ArrayList<>();

    public JsonSpeechConfiguration() { }

    @Override
    public List<WorkerConfiguration> getWorkers() {
        return (List<WorkerConfiguration>) (List<?>) workers;
    }
}
