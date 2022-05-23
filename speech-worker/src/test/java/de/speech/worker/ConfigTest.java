package de.speech.worker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigTest {

    @Test
    public void testConstructor() {
        Config config = new Config(1000, 10, "preprocessDir", "serviceJar");
        assertEquals(1000, config.getPort());
        assertEquals(10, config.getMaxQueueSize());
        assertEquals("preprocessDir", config.getPreProcessDir());
        assertEquals("serviceJar", config.getServiceJar());
    }

}
