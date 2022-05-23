package de.speech.core.postprocessing.parallelization;

import de.speech.core.postprocessing.IPostProcessFactory;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;

import java.util.List;

/**
 * Strategy for the parallelization of postprocessing.
 */
public interface ParallelizationStrategy {

    /**
     * Executes PostProcessing of multiple AudioRequestResults
     * @param audioRequestResults AudioRequestResults containing inputData for the postprocessing
     * @param factories factories for each postprocess
     * @return  List of FinalAudioRequestResults containing AudioRequestResults
     *          with additional Data from postprocessing
     */
    List<FinalAudioRequestResult> executePostProcessing(List<IAudioRequestResult> audioRequestResults,
                                                               List<IPostProcessFactory> factories);

}
