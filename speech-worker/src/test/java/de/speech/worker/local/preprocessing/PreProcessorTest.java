package de.speech.worker.local.preprocessing;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PreProcessorTest {

    private static IPreProcessor preProcessor;
    private static AudioInputStream stream;

    @BeforeAll
    public static void setup() {
        preProcessor = new PreProcessor(new DummyPreProcess());
        try {
            stream = AudioSystem.getAudioInputStream(PreProcessorTest.class.getResource("/audio.wav"));
        } catch (UnsupportedAudioFileException | IOException e) {
            System.out.println("error loading audioinputstream");
        }
    }

    @Test
    public void testPreProcessor() {
        assertEquals(stream, preProcessor.process(stream, Collections.singletonList("dummy-preprocess")));
    }

    @Test
    public void testInputNull() {
        assertThrows(NullPointerException.class, () -> preProcessor.process(null, Collections.emptyList()));
    }

    @Test
    public void testPreProcessNotAvailable() {
        assertThrows(IllegalArgumentException.class, () -> preProcessor.process(stream, Collections.singletonList("not-available")));
    }

}
