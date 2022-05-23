package de.speech.worker;

import org.junit.jupiter.api.Test;

public class WorkerManagerTest {

    @Test
    public void test() {
        WorkerManager manager = new WorkerManager(new Config(3000, 30, "preprocesses", "src/test/resources/jars/DummySpeechToTextService.jar"));
        manager.shutdown();
    }
}

