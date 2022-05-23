package de.speech.core.task.result.implementation;

import de.speech.core.task.ITask;
import de.speech.core.task.result.ITaskResult;

import java.util.List;

/**
 * This class is a data object that gets returned by TargetActualComparison and containes FinalAudioRequestResults
 * that also have the percentage of Equality saved.
 */
public class FinalTaskResultWithTac implements ITaskResult<FinalAudioRequestResultWithTac> {
    private final ITask task;
    private final List<FinalAudioRequestResultWithTac> results;


    /**
     * Creates a new instance of TaskResultWithTac
     *
     * @param task the Task that the FinalTaskResultWithTac originates from.
     * @param results the List of FinalAudioRequestResultsWithTac
     */
    public FinalTaskResultWithTac(ITask task, List<FinalAudioRequestResultWithTac> results) {
        this.task = task;
        this.results = results;
    }

    /**
     * Getter for the Task
     * @return the task
     */
    @Override
    public ITask getTask() {
        return this.task;
    }

    /**
     * Getter for the List of FinalAudioRequestResultsWithTac
     * @return the List of FinalAudioRequestResults
     */
    @Override
    public List<FinalAudioRequestResultWithTac> getResults() {
        return results;
    }
}
