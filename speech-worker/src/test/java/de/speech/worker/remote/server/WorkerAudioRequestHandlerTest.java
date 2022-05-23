package de.speech.worker.remote.server;

import de.speech.core.dispatcher.IWorkerAudioRequest;
import de.speech.core.dispatcher.implementation.RequestUtils;
import de.speech.core.dispatcher.implementation.requestresult.WorkerAudioRequest;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.implementation.audioRequest.AudioRequestWithInputStream;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorkerAudioRequestHandlerTest {

    private static HttpClient client;
    private static ServerMain serverMain;
    private static DummyNetworkHandler networkHandler;

    @BeforeEach
    public void setup() throws Exception {
        networkHandler = new DummyNetworkHandler();
        serverMain = new ServerMain(networkHandler, 3000);
        client = new HttpClient();
        client.start();
    }

    @AfterEach
    public void destroy() throws Exception {
        client.stop();
        serverMain.shutdown();
    }
    
    @Test
    public void testValidRequest() throws IOException, UnsupportedAudioFileException, InterruptedException, ExecutionException, TimeoutException {
        AudioInputStream stream = AudioSystem.getAudioInputStream(WorkerAudioRequestHandlerTest.class.getResource("/audio.wav"));
        IAudioRequest audioRequest = new AudioRequestWithInputStream(1, stream);
        IWorkerAudioRequest request = new WorkerAudioRequest(audioRequest, Collections.emptyList());
        Request jettyRequest = RequestUtils.iWorkerAudioRequestToRequest(request, client, "http://127.0.0.1:3000/request");
        ContentResponse response = jettyRequest.send();
        assertEquals(200, response.getStatus());

        IWorkerAudioRequest received = networkHandler.getRequest();
        assertEquals(request.getId(), received.getId());
        assertEquals(request.getRequest().getRequestId(), received.getRequest().getRequestId());
        assertEquals(request.getPreProcesses(), received.getPreProcesses());

        AudioInputStream shouldStream = request.getRequest().getAudio();
        AudioInputStream isStream = received.getRequest().getAudio();
        assertEquals(shouldStream.getFrameLength(), isStream.getFrameLength());
        assertTrue(shouldStream.getFormat().matches(isStream.getFormat()));
    }

}
