package de.speech.worker.remote;

import de.fraunhofer.iosb.spinpro.speechtotext.NullObjectSpeechToTextServiceMetadata;
import de.speech.core.dispatcher.implementation.requestresult.FrameworkResult;
import de.speech.core.task.result.SpeechToTextServiceData;
import de.speech.worker.local.DummyISpeechToTextService;
import de.speech.worker.local.WorkerServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Collections;

public class RemoteConnectionTest {

    private NetworkHandler handler;
    private RemoteConnection conn;

    @BeforeEach
    void setUp() throws Exception {
        handler = new NetworkHandler(new WorkerServer(1, new DummyISpeechToTextService(), "name", Paths.get("")));
        conn = new RemoteConnection(6000, handler);
    }

    @AfterEach
    void tearDown() throws Exception {
        conn.shutdown();
    }

    @Test
    public void testSendToCore() throws Exception {
        conn.initClient("127.0.0.1", "/endpoint");
        conn.sendToCore(new FrameworkResult(0, new SpeechToTextServiceData(new NullObjectSpeechToTextServiceMetadata(""), Collections.emptyList())));
    }
}
