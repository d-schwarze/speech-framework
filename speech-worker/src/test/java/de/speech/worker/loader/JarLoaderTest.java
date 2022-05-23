package de.speech.worker.loader;

import de.fraunhofer.iosb.spinpro.speechtotext.ISpeechToTextService;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class JarLoaderTest {

    @Test
    public void testLoadFromDir() {
        URL url = JarLoaderTest.class.getResource("/jars");
        JarLoader<ISpeechToTextService> loader;
        try {
            loader = new JarLoader<>(Paths.get(url.toURI()), ISpeechToTextService.class);
        } catch (LoadingException | URISyntaxException e) {
            fail();
            return;
        }

        List<Class<ISpeechToTextService>> classes = loader.getClasses();
        assertEquals("DummyISpeechToTextService", classes.get(0).getSimpleName());
    }
}
