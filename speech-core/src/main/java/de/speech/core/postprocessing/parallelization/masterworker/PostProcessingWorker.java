package de.speech.core.postprocessing.parallelization.masterworker;

import de.speech.core.postprocessing.IPostProcess;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Worker of master-worker-pattern, that executes postprocessing.
 * Takes {@link IAudioRequestResult} from queue and executes postprocessing on it.
 * Stores result in the result list. Works until the queue is empty.
 */
public class PostProcessingWorker implements Runnable{

    private List<IPostProcess> postProcesses;

    private BlockingQueue<IAudioRequestResult> queue;
    private List<FinalAudioRequestResult> results;

    /**
     * Initializes the worker with the parameters.
     * @param queue Queue with inputData for postProcessing
     * @param results result list, where the processed data is stored
     * @param postProcesses PostProcesses that need to be executed
     */
    public PostProcessingWorker(BlockingQueue<IAudioRequestResult> queue, List<FinalAudioRequestResult> results, List<IPostProcess> postProcesses) {
        this.postProcesses = postProcesses;
        this.queue = queue;
        this.results = results;
    }

    @Override
    public void run() {
        while (!queue.isEmpty()) {
            IAudioRequestResult audioRequestResult = queue.poll();

            if (audioRequestResult == null) {
                continue;
            }

            Map<String, String> resultMap = new HashMap<>();
            for (IPostProcess postProcess : postProcesses) {
                resultMap.put(postProcess.getName(), postProcess.process(audioRequestResult.getResults()));
            }

            FinalAudioRequestResult finalAudioRequestResult = new FinalAudioRequestResult(audioRequestResult, resultMap);

            results.add(finalAudioRequestResult);
        }
    }

    /**
     * Getter for the postprocesses
     * @return the postprocesses
     */
    public List<IPostProcess> getPostProcesses() {
        return postProcesses;
    }
}
