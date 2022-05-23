package de.speech.core.task.result.implementation;

import de.speech.core.task.result.AudioRequestResultDecorator;
import de.speech.core.task.result.IAudioRequestResult;

import java.util.Map;

/**
 * This class extends an AudioRequestResult with the results of postprocessing.
 * It is part of a Decorator and decorates the Interface {@link IAudioRequestResult}
 */
public class FinalAudioRequestResult extends AudioRequestResultDecorator {

    private Map<String, String> postProcessingResults;

    /**
     * Constructor for FinalAudioRequestResult
     * @param audioRequestResultToBeDecorated {@link IAudioRequestResult} that gets extended
     * @param postProcessingResults results from postprocessing
     */
    public FinalAudioRequestResult(IAudioRequestResult audioRequestResultToBeDecorated,
                                   Map<String, String> postProcessingResults) {
        super(audioRequestResultToBeDecorated);
        this.postProcessingResults = postProcessingResults;
    }

    /**
     * Gets the result of all postprocessing algorithms
     * @return Map from the name of postprocess to their result String
     */
    public Map<String, String> getPostProcessingResults() {
        return postProcessingResults;
    }

}
