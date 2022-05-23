package de.speech.core.task.result.implementation;

import de.speech.core.task.ITask;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ITaskResult;

import java.util.List;

public class TaskResult implements ITaskResult<IAudioRequestResult> {

    private List<IAudioRequestResult> results;
    private ITask task;

    public TaskResult(ITask task , List<IAudioRequestResult> results) {
        this.task = task;
        this.results = results;
    }

    @Override
    public ITask getTask() {
        return task;
    }

    @Override
    public List<IAudioRequestResult> getResults() {
        return results;
    }
}
