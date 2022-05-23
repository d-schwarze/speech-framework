package de.speech.test.preprocesses;

import de.speech.worker.local.preprocessing.IPreProcess;

import javax.sound.sampled.AudioInputStream;

public class DefaultPreProcess implements IPreProcess {
    @Override
    public String getName() {
        return "default";
    }

    @Override
    public AudioInputStream process(AudioInputStream input) {
        return input;
    }
}
