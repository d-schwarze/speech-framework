package de.speech.core.task.result;

import de.speech.core.task.ITask;

import java.util.List;

/**
 * Iterable that holds all results from one ITask.
 * Provides iterator over all results.
 * @param <T> Can  be IAudioRequestResult or IFinalAudioRequestResult. Defines Type of the Iterable.
 */
public interface ITaskResult<T extends IAudioRequestResult> {

    /**
     * A getter for the associated task
     * @return the associated task
     */
    ITask getTask();

    List<T> getResults();
}
