package de.speech.core.task.result;

import de.fraunhofer.iosb.spinpro.speechtotext.ISpeechToTextServiceMetadata;

import java.util.List;

/**
 * This interface defines an object to store all IAudioFilter in addition to all data provided by ISpeechToTextMetadata
 */
public interface ISpeechToTextServiceData extends ISpeechToTextServiceMetadata {

    /**
     * Getter for the List of IAudioFilter used by the Worker
     * @return the List of IAudioFilter
     */
    List<String> getPreprocesses();
}
