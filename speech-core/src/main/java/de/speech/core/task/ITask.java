package de.speech.core.task;

import de.speech.core.postprocessing.IPostProcessFactory;

import java.util.List;

/**
 *  This interface defines an object that holds the configuration of a task and the information to iterate over the requests.
 */
public interface ITask {

    /**
     * A getter for the AudioRequests of the instance
     * @return The Requests of the Task
     */
    List<IAudioRequest> getAudioRequests();

    /**
     * A getter for the id of the instance.
     * @return The id of the task.
     */
    int getTaskID();

    /**
     * A getter for the List of IFrameworkConfigurations of the instance.
     * @return The IFrameworkConfigurations of the instance.
     */
    List<IFrameworkConfiguration> getFrameworkConfigurations();

    /**
     * A getter for the IPostProcesses of the instance.
     * @return The IPostProcesses of the instance.
     */
    List<IPostProcessFactory> getPostProcessFactories();
}