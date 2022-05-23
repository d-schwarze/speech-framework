package de.speech.core.postprocessing;

import de.speech.core.annotation.adaptions.postprocessing.PostProcessingAnnotationSystem;
import de.speech.core.postprocessing.parallelization.ParallelizationStrategy;
import de.speech.core.postprocessing.parallelization.masterworker.ParallelizationWithMasterWorker;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ITaskResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;
import de.speech.core.task.result.implementation.FinalTaskResult;

import java.util.List;

/**
 * Manages the execution of the postprocessing
 * Provides the possibilty to run multiple postprocesses on one Task.
 */
public class PostProcessor {

    private List<IPostProcessFactory> postProcessFactories;

    private ParallelizationStrategy parallelizationStrategy;

    /**
     * Constructor for the postProcessor
     * If the List of {@link IPostProcessFactory} is empty, it gets annotated postprocess factories from the annotation system.
     * @param postProcessFactories postprocess factories to work with
     */
    public PostProcessor (List<IPostProcessFactory> postProcessFactories) {

        if (postProcessFactories != null && postProcessFactories.size() > 0) {
            this.postProcessFactories = postProcessFactories;
        } else {
            PostProcessingAnnotationSystem annotationSystem = new PostProcessingAnnotationSystem();
            this.postProcessFactories = annotationSystem.getAnnotatedPostProcessFactories();
        }

        parallelizationStrategy = new ParallelizationWithMasterWorker();
    }

    /**
     * Computes the postprocessing of one TaskResult.
     * Returns the TaskResult extended by the calculated Strings in postprocessing.
     * @param taskResult TaskResult containing the data of the Speech-to-Text Frameworks
     * @return TaskResult with list of {@link FinalAudioRequestResult}s
     */
    public ITaskResult<FinalAudioRequestResult> processAll(ITaskResult<IAudioRequestResult> taskResult) {
        List<FinalAudioRequestResult> finalAudioRequestResults =
                parallelizationStrategy.executePostProcessing(taskResult.getResults(), postProcessFactories);

        return new FinalTaskResult(taskResult.getTask(), finalAudioRequestResults);
    }


    /**
     * Getter for the postProcessFactories
     * @return the factories
     */
    public List<IPostProcessFactory> getPostProcessFactories() {
        return postProcessFactories;
    }
}
