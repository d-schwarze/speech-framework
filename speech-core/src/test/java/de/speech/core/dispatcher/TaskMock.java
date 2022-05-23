package de.speech.core.dispatcher;

import de.speech.core.postprocessing.IPostProcessFactory;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.IFrameworkConfiguration;
import de.speech.core.task.ITask;

import java.util.List;

public class TaskMock implements ITask {

    private final int id;
    private final List<IFrameworkConfiguration> configs;
    private final List<IAudioRequest> requests;

    public TaskMock(int id, List<IFrameworkConfiguration> configs, List<IAudioRequest> requests) {
        this.id = id;
        this.configs = configs;
        this.requests = requests;
    }

    @Override
    public List<IAudioRequest> getAudioRequests() {
        return requests;
    }

    @Override
    public int getTaskID() {
        return id;
    }

    @Override
    public List<IFrameworkConfiguration> getFrameworkConfigurations() {
        return configs;
    }

    @Override
    public List<IPostProcessFactory> getPostProcessFactories() {
        return null;
    }

}
