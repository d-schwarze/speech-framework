package de.speech.core.application.configuration.json;

import de.speech.core.application.configuration.WorkerConfiguration;

public class JsonWorkerConfiguration implements WorkerConfiguration {

    private String location;

    public JsonWorkerConfiguration() { }

    public JsonWorkerConfiguration(String location) {
        this.location = location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String getLocation() {
        return location;
    }
}
