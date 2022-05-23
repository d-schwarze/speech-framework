package de.speech.dev.targetActualComparison;

import de.speech.core.task.result.ITaskResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;
import de.speech.core.task.result.implementation.FinalTaskResultWithTac;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * This interface capsules the targetActualComparison as object.
 * @author Sven Ambrosius
 */
public interface ITargetActualComparison {

    /**
     * used to compare a FinalTaskResult to the actualText Strings of DeveloperAudioRequests
     * @param fileName the name of the newly created File. Should end with .txt
     *                 A goof name could be : "TargetActualComparisonResult.txt"
     * @return the new File containing the result of the comparison.
     */
    File compare( ITaskResult<FinalAudioRequestResult> taskResult, String fileName);

    FinalTaskResultWithTac compare(ITaskResult<FinalAudioRequestResult> taskResult);

    default float calculateEquality(String actualText, String calculatedText) {
        int actualTextLength = actualText.length();
        int calculatedTextLength = calculatedText.length();
        float max;
        float diff;

        if (actualTextLength >= calculatedTextLength) {
            diff = StringUtils.difference(calculatedText, actualText).length();
            max = actualTextLength;
        } else {
            diff = StringUtils.difference(actualText, calculatedText).length();
            max = calculatedTextLength;
        }

        return ((max - diff) / max) * 100;
    }
}
