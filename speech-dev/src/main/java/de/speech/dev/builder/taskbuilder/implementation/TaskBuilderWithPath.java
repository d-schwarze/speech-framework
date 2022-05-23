package de.speech.dev.builder.taskbuilder.implementation;

import de.speech.core.task.implementation.Task;
import de.speech.dev.builder.taskbuilder.implementation.abstract_builders.AbstractTaskBuilderWithPath;

/**
 * This class is used to build tasks that contain AudioRequestsWitPath objects
 */
public class TaskBuilderWithPath extends AbstractTaskBuilderWithPath<TaskBuilderWithPath> {

    /**
     *
     * @param path The String that defines the path to the audioFiles
     */
    public TaskBuilderWithPath(String path) {
        super(path);
    }


    /**
     * Builds a new Task
     * @return the new instance of Task
     */
    @Override
    public Task buildTask() {
        return new Task(taskId, frameworkConfigurations, postProcessesFactories, buildAudioRequests());
    }
}
