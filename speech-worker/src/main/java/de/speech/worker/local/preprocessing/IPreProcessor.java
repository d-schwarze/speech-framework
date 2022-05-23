package de.speech.worker.local.preprocessing;

import javax.sound.sampled.AudioInputStream;
import java.util.List;

/**
 * Applies a number of {@linkplain IPreProcess}es to the {@linkplain AudioInputStream}
 */
public interface IPreProcessor {

    /**
     * Applies the specified {@linkplain IPreProcess}es to the {@linkplain AudioInputStream}
     *
     * @param input        the {@linkplain AudioInputStream} to process
     * @param preProcesses the list of {@linkplain IPreProcess}es to apply
     * @return the processed {@linkplain AudioInputStream}
     */
    AudioInputStream process(AudioInputStream input, List<String> preProcesses);

    boolean areAllPrePreProcessesAvailable(List<String> preProcesses);
}
