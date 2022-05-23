package de.speech.worker.remote;

import de.speech.core.dispatcher.IWorkerAudioRequest;
import de.speech.core.dispatcher.implementation.requestresult.WorkerAudioRequest;
import de.speech.core.task.implementation.audioRequest.AudioRequestWithPath;
import de.speech.worker.local.DummyISpeechToTextService;
import de.speech.worker.local.WorkerServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NetworkHandlerTest {

    private NetworkHandler handler;
    private RemoteConnection conn;

    @BeforeEach
    void setUp() throws Exception {
        handler = new NetworkHandler(new WorkerServer(1, new DummyISpeechToTextService(), "name", Paths.get("")));
        conn = new RemoteConnection(5000, handler);
    }

    @AfterEach
    public void teardown() throws Exception {
        conn.shutdown();
    }

    @Test
    public void testHandleInit() throws Exception {
        handler.setRemoteConnection(conn);
        handler.handleInit("127.0.0.1", "/endpoint");
    }

    @Test
    public void testHandleAudioRequest() {
        IWorkerAudioRequest request = new WorkerAudioRequest(new AudioRequestWithPath(0, ""), Collections.emptyList());
        handler.handleAudioRequest(request);
    }

    @Test
    public void testHandleInformationRequest() {
        var info = handler.handleInformationRequest();
        assertEquals("name", info.getFrameworkName());
        assertEquals("dummy model", info.getModel());
        assertEquals(1, info.getMaxQueueSize());
        assertEquals(0, info.getCurrentQueueSize());
    }

}
