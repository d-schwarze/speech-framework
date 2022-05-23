package de.speech.core.postprocessing.parallelization.masterworker;

import de.speech.core.postprocessing.IPostProcessFactory;
import de.speech.core.postprocessing.parallelization.ParallelizationStrategy;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;

import java.util.List;

/**
 * Algorithm for the parallelization of the postprocessing.
 * Uses Master-Worker pattern to execute the postprocessing.
 */
public class ParallelizationWithMasterWorker implements ParallelizationStrategy {

    //Minimum number of workers to use.
    private static final int MIN_WORKERS = 4;

    private int numberOfWorkers;

    /**
     * Constructor, that defines the number of workers to be used
     */
    public ParallelizationWithMasterWorker() {
        numberOfWorkers = 2 * Runtime.getRuntime().availableProcessors();

        if (numberOfWorkers < MIN_WORKERS) numberOfWorkers = MIN_WORKERS;
    }


    @Override
    public List<FinalAudioRequestResult> executePostProcessing(List<IAudioRequestResult> audioRequestResults, List<IPostProcessFactory> postProcessFactories) {

        PostProcessingMaster master = new PostProcessingMaster(numberOfWorkers, postProcessFactories);

        for (IAudioRequestResult audioRequestResult : audioRequestResults) {
            master.submit(audioRequestResult);
        }

        master.execute();
        master.waitOnFinish();

        return master.getResults();
    }
}
