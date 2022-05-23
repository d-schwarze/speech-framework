package de.speech.core.postprocessing.parallelization.masterworker;

import de.speech.core.logging.Loggable;
import de.speech.core.postprocessing.IPostProcessFactory;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

/**
 * Master of master-worker pattern, that executes the postprocessing.
 * Using the {@link IPostProcessFactory}s, every worker has their own postprocess instances.
 * Manages queue of {@link IAudioRequestResult}s from which the workers can get the input to execute the postprocessing.
 */
public class PostProcessingMaster implements Loggable {

    private Thread[] workerThreads;

    private BlockingQueue<IAudioRequestResult> queue;
    private List<FinalAudioRequestResult> results;

    private int taskCount;


    /**
     * Initializes the empty queue and resultList. Initializes the worker threads.
     * @param workerCount number of workers
     * @param postProcessFactories Factories to get instances of postprocesses
     * @throws IllegalArgumentException if number of workers is less than 1
     */
    public PostProcessingMaster(int workerCount, List<IPostProcessFactory> postProcessFactories) {
        taskCount = 0;
        queue = new LinkedBlockingQueue<>();
        results = Collections.synchronizedList(new ArrayList<>());

        if (workerCount < 1) {
            throw new IllegalArgumentException("Number of workers has to be at least 1.");
        }
        workerThreads = new Thread[workerCount];

        for (int i = 0; i < workerCount; i++) {
            PostProcessingWorker worker = PostProcessingWorkerFactory.createWorker(this.queue, this.results, postProcessFactories);
            workerThreads[i] = new Thread(worker);
        }

    }

    /**
     * Gets List of the final results.
     * @return the final results
     */
    public List<FinalAudioRequestResult> getResults() {
        return results;
    }

    /**
     * Submits new {@link IAudioRequestResult} to queue, that need postprocessing done
     * If null, nothing happens.
     * @param audioRequestResult the IAudioRequestResult
     */
    public void submit (IAudioRequestResult audioRequestResult) {
        if (audioRequestResult != null) {
            queue.add(audioRequestResult);
            taskCount++;
        }
    }

    /**
     * Starts all worker threads, so they start executing.
     */
    public void execute() {
        for (Thread t : workerThreads) {
            t.start();
        }
    }

    /**
     * Waits for the threads to finish their executing.
     * Logs a warning if a thread was interrupted.
     */
    public void waitOnFinish() {
        for (Thread t : workerThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Interrupted while PostProcessing. There may be some Results missing.", e);
            }
        }
    }

    /**
     * Returns if the postprocessing execution is completed.
     * @return if the postprocessing execution is completed
     */
    public boolean isComplete() {
        int resultCount = results.size();
        return (resultCount == taskCount);
    }
}
