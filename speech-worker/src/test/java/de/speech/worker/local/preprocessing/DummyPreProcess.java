package de.speech.worker.local.preprocessing;

import javax.sound.sampled.AudioInputStream;

public class DummyPreProcess implements IPreProcess {

    @Override
    public String getName() {
        return "dummy-preprocess";
    }

    @Override
    public AudioInputStream process(AudioInputStream input) {
        return input;
    }
}
