package de.speech.core.application.configuration;

import de.speech.core.application.configuration.json.ConfigurationFileLoader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ConfigurationLoaderTest {

    @Test
    public void testLoadDefaultJsonConfiguration() throws IOException {

        SpeechConfiguration config = ConfigurationFileLoader.loadDefaultJsonConfiguration();

        assertEquals(config.getWorkers().size(), 2);
        assertEquals(config.getWorkers().get(0).getLocation(), "http://localhost:8080");
        assertEquals(config.getWorkers().get(1).getLocation(), "http://localhost:8081");
        assertEquals(5000, config.getResultTimeout());
        assertEquals(8001, config.getPort());
        assertEquals(15, config.getAcceptors());
        assertEquals(15, config.getSelectors());
        assertEquals(30, config.getQueueSize());
    }

    @Test
    public void testLoadJsonConfiguration() throws IOException {
        SpeechConfiguration config = ConfigurationFileLoader.loadJsonConfiguration(Path.of("src/test/resources/testConfiguration.json"));

        assertEquals(config.getWorkers().size(), 1);
        assertEquals(config.getWorkers().get(0).getLocation(), "localhost:1234");
        assertEquals(1, config.getResultTimeout());
        assertEquals(2, config.getPort());
        assertEquals(3, config.getAcceptors());
        assertEquals(4, config.getSelectors());
        assertEquals(5, config.getQueueSize());
    }
}
