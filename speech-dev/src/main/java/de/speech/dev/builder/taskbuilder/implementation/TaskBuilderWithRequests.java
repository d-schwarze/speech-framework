package de.speech.dev.builder.taskbuilder.implementation;

import de.speech.core.task.implementation.Task;
import de.speech.dev.builder.taskbuilder.implementation.abstract_builders.AbstractTaskBuilderWithRequests;


/**
 * This class is used to build tasks that contain AudioRequestsWitPath objects
 */
public class TaskBuilderWithRequests extends AbstractTaskBuilderWithRequests<TaskBuilderWithRequests> {

    public TaskBuilderWithRequests() {
        super();
    }

    /**
     * Builds a ne Task
     * @return the new instance of Task
     */
    @Override
    public Task buildTask() {
        return new Task(taskId, frameworkConfigurations, postProcessesFactories, buildAudioRequests());
    }
}
