package de.speech.worker.local.preprocessing;

import javax.sound.sampled.AudioInputStream;

/**
 * Transforms an {@linkplain AudioInputStream}
 */
public interface IPreProcess {

    /**
     * Returns the name that is used for identifying {@code IPreProcess}es
     *
     * @return the name of the {@code IPreProcess}
     */
    String getName();

    /**
     * The method that transforms the {@linkplain AudioInputStream}
     *
     * @param input the {@linkplain AudioInputStream} to be transformed
     * @return the transformed {@linkplain AudioInputStream}
     */
    AudioInputStream process(AudioInputStream input);

}
