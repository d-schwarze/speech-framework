package de.speech.core.postprocessing.mocks;

import de.speech.core.task.ITask;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ITaskResult;

import java.util.ArrayList;
import java.util.List;

public class MockTaskResult implements ITaskResult<IAudioRequestResult> {
    private final List<IAudioRequestResult> audioRequestResults = new ArrayList<>();

    @Override
    public ITask getTask() {
        return null;
    }

    @Override
    public List<IAudioRequestResult> getResults() {
        return audioRequestResults;
    }
}
