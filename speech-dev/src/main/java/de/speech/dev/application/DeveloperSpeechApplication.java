package de.speech.dev.application;

import de.speech.core.application.SpeechApplication;
import de.speech.core.application.execution.ExecutionPart;
import de.speech.core.application.execution.ExecutionSystem;
import de.speech.core.task.ITask;
import de.speech.core.task.result.implementation.FinalTaskResultWithTac;
import de.speech.dev.application.parts.TargetActualComparisonPart;
import de.speech.dev.targetActualComparison.ITargetActualComparison;
import de.speech.dev.targetActualComparison.implementation.TargetActualComparison;

import java.util.List;
import java.util.concurrent.Future;

/**
 * SpeechApplication derivative that should be used if the speech frameworks purpose is to evaluate different
 * speech-to-text frameworks.
 */
public abstract class DeveloperSpeechApplication extends SpeechApplication {

    private ITargetActualComparison targetActualComparison;

    @Override
    protected void onInitialize() {
        initializeTargetActualComparison();

        super.onInitialize();
    }

    @Override
    protected ExecutionSystem<ITask> createExecutionSystem(List<ITask> t) {
        List<ITask> tasks = runTasksOnStart();

        return super.createExecutionSystem(tasks);
    }

    /**
     * Provide some tasks that should be run on application startup.
     * @return tasks that should be run on startup
     */
    protected abstract List<ITask> runTasksOnStart();

    @Override
    public List<ExecutionPart> createExecutionParts() {
        List<ExecutionPart> parts = super.createExecutionParts();
        parts.add(new TargetActualComparisonPart(this.targetActualComparison));
        return parts;
    }

    private void initializeTargetActualComparison() {
        this.targetActualComparison = createTargetActualComparison();

        if (this.targetActualComparison == null) {
            throw new NullPointerException("Only non null target actual comparison are valid.");
        }
    }

    protected ITargetActualComparison createTargetActualComparison() {
        return new TargetActualComparison();
    }

    /**
     * Runs a task with an future and provides also the target actual comparison.
     * @param task task that should be run
     * @return task result with target actual comparison
     */
    public Future<FinalTaskResultWithTac> runTaskWithFutureAndTac(ITask task) {
        return executionSystem.executeElement(task);
    }
}
