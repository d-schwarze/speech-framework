package de.speech.core.task.implementation;

import de.speech.core.postprocessing.IPostProcessFactory;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.IFrameworkConfiguration;
import de.speech.core.task.ITask;

import java.util.List;

/**
 * This class implements a Task that is used in the System
 */
public class Task implements ITask {
    private List<IAudioRequest> audioRequests;
    private int taskId;
    private List<IFrameworkConfiguration> frameworkConfigurations;
    private List<IPostProcessFactory> postProcessFactories;

    /**
     * A getter for the AudioRequests
     * @return the AudioRequests of the Task
     */
    @Override
    public List<IAudioRequest> getAudioRequests() {
        return audioRequests;
    }

    /**
     * A getter for the Id of the Task
     * @return the id of the Task
     */
    @Override
    public int getTaskID() {
        return taskId;
    }

    /**
     * A getter for the FrameworkConfigurations of the Task
     * @return the FrameworkConfigurations of the Task
     */
    @Override
    public List<IFrameworkConfiguration> getFrameworkConfigurations() {
        return frameworkConfigurations;
    }

    /**
     * A getter for the PostProcesses of the Task
     * @return the PostProcesses of the Task
     */
    @Override
    public List<IPostProcessFactory> getPostProcessFactories() {
        return postProcessFactories;
    }


    /**
     * Builder for a new Task
     * @param taskId the id of the Task
     * @param frameworkConfigurations the FrameworkConfigurations of the Task
     * @param postProcessFactories the PostProcessFactories of the Task
     * @param audioRequests the AudioRequests of the Task
     */
    public Task(int taskId, List<IFrameworkConfiguration> frameworkConfigurations, List<IPostProcessFactory> postProcessFactories, List<IAudioRequest> audioRequests) {
        this.taskId = taskId;
        this.frameworkConfigurations = frameworkConfigurations;
        this.postProcessFactories = postProcessFactories;
        this.audioRequests = audioRequests;
    }
}
