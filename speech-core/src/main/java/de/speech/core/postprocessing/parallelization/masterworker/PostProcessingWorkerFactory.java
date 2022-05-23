package de.speech.core.postprocessing.parallelization.masterworker;

import de.speech.core.postprocessing.IPostProcess;
import de.speech.core.postprocessing.IPostProcessFactory;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Factory that creates new Worker for the postprocessing.
 */
public class PostProcessingWorkerFactory {

    /**
     * Factory method, that creates new Worker.
     * Gets new instances of {@link IPostProcess} from the {@link IPostProcessFactory}s
     * @param queue Queue with inputData for postProcessing
     * @param results result list, where the processed data is stored
     * @param postProcessFactories factories to get new postprocess instances
     * @return a new worker instance
     */
    public static PostProcessingWorker createWorker(BlockingQueue<IAudioRequestResult> queue,
                                                    List<FinalAudioRequestResult> results,
                                                    List<IPostProcessFactory> postProcessFactories) {
        List<IPostProcess> postProcesses = new ArrayList<>();
        for (IPostProcessFactory factory : postProcessFactories) {
            postProcesses.add(factory.createPostProcess());
        }

        return new PostProcessingWorker(queue, results, postProcesses);
    }
}
