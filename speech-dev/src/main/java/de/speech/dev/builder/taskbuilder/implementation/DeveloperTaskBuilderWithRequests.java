package de.speech.dev.builder.taskbuilder.implementation;

import de.speech.core.task.implementation.Task;
import de.speech.dev.builder.taskbuilder.implementation.abstract_builders.AbstractDeveloperTaskBuilderWithRequests;


/**
 * This class is used to build tasks that contain DeveloperAudioRequestsWitRequest objects
 */
public class DeveloperTaskBuilderWithRequests extends AbstractDeveloperTaskBuilderWithRequests<DeveloperTaskBuilderWithRequests> {


    /**
     * creates a new task
     * @return the new instance of Task
     */
    @Override
    public Task buildTask() {
        return new Task(taskId, frameworkConfigurations, postProcessesFactories, buildAudioRequests());
    }
}
