package de.speech.core.postprocessing;

import de.speech.core.task.result.ISpeechToTextServiceData;

import java.util.List;

/**
 * This is an interface for postprocessing algorithms.
 * Implementations of this Interface execute an algorithm to calculate a result String
 * out of the available data from the Speech-to-Text Frameworks.
 */
public interface IPostProcess {

    /**
     * Method to run the algorithm.
     * Gets available data from Speech-to-text Frameworks and runs the algorithm with that data.
     * @param inputData available data from Speech-to-Text Frameworks
     *                  including metadata and recognized texts of one Audio Request
     * @return  The final result as String
     */
    String process(List<ISpeechToTextServiceData> inputData);


    /**
     * Gets the name of the PostProcess. If not specified the name of the class is returned.
     * @return name of the postprocess
     */
    default String getName() {
        return this.getClass().getName();
    }

}
