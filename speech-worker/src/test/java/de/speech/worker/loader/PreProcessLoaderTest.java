package de.speech.worker.loader;

import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class PreProcessLoaderTest {

    @Test
    public void test() {
        URL url = JarLoaderTest.class.getResource("/jars");
        PreProcessLoader loader;
        try {
            loader = new PreProcessLoader(Paths.get(url.toURI()));
        } catch (LoadingException | URISyntaxException e) {
            fail();
            return;
        }

        var result = loader.getPreProcesses();
        var preprocess = result.get(0);
        assertEquals("dummy-preprocess", preprocess.getName());
    }

}
