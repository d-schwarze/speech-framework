package de.speech.dev.application;

import de.speech.core.dispatcher.implementation.AbstractDispatcher;
import de.speech.core.task.ITask;

import java.util.List;

public class DeveloperSpeechApplicationMock extends DeveloperSpeechApplication {

    private List<ITask> tasksForStart;

    public DeveloperSpeechApplicationMock(List<ITask> tasksForStart) {
        this.tasksForStart = tasksForStart;
    }

    @Override
    protected AbstractDispatcher createDispatcher() {
        return new DispatcherMock();
    }

    @Override
    protected List<ITask> runTasksOnStart() {
        return tasksForStart;
    }
}
