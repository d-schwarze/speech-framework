package de.speech.core.application.configuration;

import de.speech.core.application.annotation.SpeechAnnotationSystem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpeechAnnotationSystemTest {

    private static SpeechAnnotationSystem speechAnnotationSystem;

    @BeforeAll
    public static void initializeTestSuit() {
        speechAnnotationSystem = new SpeechAnnotationSystem();
    }

    @Test
    public void testGetSpeechConfiguration() {

        SpeechConfiguration configuration = speechAnnotationSystem.getSpeechConfiguration();

        assertEquals(configuration.getWorkers().get(0).getLocation(), "localhost:8082");

    }

}
