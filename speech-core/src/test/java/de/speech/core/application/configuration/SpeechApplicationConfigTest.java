package de.speech.core.application.configuration;

import de.speech.core.application.SpeechApplication;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpeechApplicationConfigTest {

    @Test
    public final void testInitializeConfiguration_BasisSpeechConfiguration() {
        SpeechApplication application = new SpeechApplicationMock();
        application.start();

        assertEquals(BasicSpeechConfiguration.class, application.getConfiguration().getClass());
    }

    class SpeechApplicationMock extends SpeechApplication {
        @Override
        protected SpeechConfiguration loadConfiguration() {
            return null;
        }
    }
}
