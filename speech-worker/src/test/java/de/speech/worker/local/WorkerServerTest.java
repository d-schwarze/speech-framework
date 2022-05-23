package de.speech.worker.local;

import de.speech.core.dispatcher.implementation.requestresult.WorkerAudioRequest;
import de.speech.core.task.implementation.audioRequest.AudioRequestWithInputStream;
import de.speech.worker.BlockingSpeechToTextService;
import de.speech.worker.loader.LoadingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class WorkerServerTest {

    private static WorkerServer worker;

    @BeforeEach
    public void setup() {
        try {
            worker = new WorkerServer(1, new BlockingSpeechToTextService(), "BlockingService", Paths.get("preprocesses"));
        } catch (LoadingException e) {
            fail();
        }
    }

    @Test
    public void testGetName() {
        assertEquals("BlockingService", worker.getName());
    }

    @Test
    public void testGetModel() {
        assertEquals("blocking", worker.getModel());
    }

    //TODO fix test
    @Test
    public void testQueueFull() throws IOException, UnsupportedAudioFileException, InterruptedException {
        AudioInputStream stream = AudioSystem.getAudioInputStream(WorkerServerTest.class.getResource("/audio.wav"));
        worker.submitWork(new WorkerAudioRequest(new AudioRequestWithInputStream(0, stream), Collections.emptyList()));
        Thread.sleep(1000); // wait for the queueworker to take the first request out of the queue
        worker.submitWork(new WorkerAudioRequest(new AudioRequestWithInputStream(0, stream), Collections.emptyList()));
        assertThrows(IllegalStateException.class, () -> worker.submitWork(new WorkerAudioRequest(new AudioRequestWithInputStream(0, stream), Collections.emptyList())));
    }

    @Test
    public void testGetMaxQueueSize() {
        assertEquals(1, worker.getMaxQueueSize());
    }

    @Test
    public void testGetQueueSize() throws IOException, UnsupportedAudioFileException, InterruptedException {
        AudioInputStream stream = AudioSystem.getAudioInputStream(WorkerServerTest.class.getResource("/audio.wav"));
        worker.submitWork(new WorkerAudioRequest(new AudioRequestWithInputStream(0, stream), Collections.emptyList()));
        Thread.sleep(1000);
        worker.submitWork(new WorkerAudioRequest(new AudioRequestWithInputStream(0, stream), Collections.emptyList()));
        assertEquals(1, worker.getQueueSize());
    }
}
