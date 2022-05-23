package de.speech.core.task.result.implementation;

import de.speech.core.task.ITask;
import de.speech.core.task.result.ITaskResult;

import java.util.List;

/**
 * This class holds the final data from the Speech-to-Text Frameworks and postprocessing of one Task.
 * It contains a list of all {@link FinalAudioRequestResult}s of one Task.
 */
public class FinalTaskResult implements ITaskResult<FinalAudioRequestResult> {

    private ITask task;
    private List<FinalAudioRequestResult> finalAudioRequestResults;

    public FinalTaskResult(ITask task, List<FinalAudioRequestResult> finalAudioRequestResults) {
        this.finalAudioRequestResults = finalAudioRequestResults;
        this.task = task;
    }

    @Override
    public ITask getTask() {
        return task;
    }

    @Override
    public List<FinalAudioRequestResult> getResults() {
        return finalAudioRequestResults;
    }
}
