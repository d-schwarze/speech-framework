package de.speech.core.task.result.implementation;

import de.speech.core.task.result.IAudioRequestResult;

import java.util.Map;

/**
 * This class has all functionalities of FinalAudioRequestResult with the addition of a Map from the processed
 * postprocess String to the percentage of Equality with the actualText.
 */
public class FinalAudioRequestResultWithTac extends FinalAudioRequestResult {
    private final Map<String, Float> equalityMap;

    /**
     * Getter for the EqualityMap
     * @return the EqualityMap
     */
    public Map<String, Float> getEqualityOfPostProcesses() {
        return equalityMap;
    }

    /**
     * Constructor for FinalAudioRequestResultWithTac
     *
     * @param audioRequestResultToBeDecorated {@link IAudioRequestResult} that gets extended
     * @param equalityMap A Map that routes each postprocess to the corresponding equality.
     *                    The equality is a float from 0 to 100 that describes how alike the actualText and processedText is
     */
    public FinalAudioRequestResultWithTac(FinalAudioRequestResult audioRequestResultToBeDecorated, Map<String, Float> equalityMap) {
        super(audioRequestResultToBeDecorated, audioRequestResultToBeDecorated.getPostProcessingResults());
        this.equalityMap = equalityMap;
    }

}
