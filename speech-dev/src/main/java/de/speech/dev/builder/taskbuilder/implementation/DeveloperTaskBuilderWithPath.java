package de.speech.dev.builder.taskbuilder.implementation;

import de.speech.core.task.implementation.Task;
import de.speech.dev.builder.taskbuilder.implementation.abstract_builders.AbstractDeveloperTaskBuilderWithPath;


/**
 * This class is used to build tasks that contain DeveloperAudioRequestsWitPath objects
 */
public class DeveloperTaskBuilderWithPath extends AbstractDeveloperTaskBuilderWithPath<DeveloperTaskBuilderWithPath> {

    /**
     * creates a new instance of TaskBuilder and dynamically sets the taskId for the buildTask method.
     * @param audioPath the String that defines the Path to the audioFiles
     * @param textPath the String that defines the Path to the textFile
     */
    public DeveloperTaskBuilderWithPath(String audioPath, String textPath){
        super(audioPath, textPath);
    }

    /**
     * creates a new Task.
     * @return the new instance of Task.
     */
    @Override
    public Task buildTask() {
        return new Task(taskId, frameworkConfigurations, postProcessesFactories, buildAudioRequests());
    }
}
