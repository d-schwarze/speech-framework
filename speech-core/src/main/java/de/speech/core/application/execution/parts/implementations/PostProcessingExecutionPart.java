package de.speech.core.application.execution.parts.implementations;


import de.speech.core.application.execution.ExecutionPart;
import de.speech.core.application.execution.parts.SequentialExecutionPart;
import de.speech.core.postprocessing.PostProcessor;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ITaskResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;


/**
 * CustomExecutionPart that wraps the postprocessing-phase to be accessed in the execution pipeline.
 */
public class PostProcessingExecutionPart extends SequentialExecutionPart<ITaskResult<IAudioRequestResult>, ITaskResult<FinalAudioRequestResult>> {

    private final static String POSTPROCESSING_PART_IDENTIFIER = "postProcessor";

    public PostProcessingExecutionPart() {
        this(POSTPROCESSING_PART_IDENTIFIER, null);

    }

    public PostProcessingExecutionPart(String identifier, ExecutionPart<ITaskResult<FinalAudioRequestResult>, ?> next) {
        super(identifier, next);
    }

    /**
     * Executes the post processing logic.
     *
     * @param element element that should be for the postprocessing-phase
     * @return post processed {@linkplain ITaskResult} by {@linkplain PostProcessor#processAll(ITaskResult)}
     */
    @Override
    public ITaskResult<FinalAudioRequestResult> executeElement(ITaskResult<IAudioRequestResult> element) {

        PostProcessor postProcessor = new PostProcessor(element.getTask().getPostProcessFactories());

        ITaskResult<FinalAudioRequestResult> finalTaskResult = postProcessor.processAll(element);

        return finalTaskResult;
    }
}
