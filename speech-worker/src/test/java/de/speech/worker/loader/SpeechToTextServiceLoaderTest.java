package de.speech.worker.loader;

import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SpeechToTextServiceLoaderTest {

    @Test
    public void test() {
        URL url = JarLoaderTest.class.getResource("/jars");
        SpeechToTextServiceLoader loader;
        try {
            loader = new SpeechToTextServiceLoader(Paths.get(url.toURI()));
        } catch (LoadingException | URISyntaxException e) {
            fail();
            return;
        }

        var result = loader.getResult();
        assertEquals("dummy-speech", result.getName());
        assertEquals("Steffen", result.getAuthor());
        assertEquals("1.0", result.getVersion());
        assertEquals("PSE", result.getOrganisation());
    }

}
