package de.speech.test.application;

import de.speech.core.application.DispatcherFailedStoppingException;
import de.speech.core.application.SpeechApplication;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomSpeechApplicationTest {

    @Test
    public final void testCustomSpeechApplication() throws DispatcherFailedStoppingException {

        SpeechApplication app = new CustomSpeechApplication() {
            @Override
            public CustomSpeechConfiguration setupConfiguration() {
                return new CustomSpeechConfiguration(new ArrayList<>(), 5, 5000, 103, 100, 5, 5);
            }
        };

        app.start();

        assertEquals(app.getConfiguration().getPort(), 103);

        app.stop();

    }

}
