package de.speech.test.application;

import de.speech.core.application.SpeechApplication;
import de.speech.core.application.configuration.SpeechConfiguration;

public abstract class CustomSpeechApplication extends SpeechApplication {

    @Override
    protected SpeechConfiguration loadConfiguration() {
        return setupConfiguration();
    }

    public abstract CustomSpeechConfiguration setupConfiguration();
}
