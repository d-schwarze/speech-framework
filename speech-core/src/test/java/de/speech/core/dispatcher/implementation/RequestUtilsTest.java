package de.speech.core.dispatcher.implementation;

import de.speech.core.dispatcher.IWorkerAudioRequest;
import de.speech.core.dispatcher.implementation.requestresult.WorkerAudioRequest;
import de.speech.core.task.implementation.audioRequest.AudioRequestWithInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.jupiter.api.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class RequestUtilsTest {

    private HttpClient client;
    private static Server server;

    private static HttpServletRequest receivedRequest;
    private static Runnable runn;

    @BeforeAll
    public static void setupAll() throws Exception {
        server = new Server(3000);
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, org.eclipse.jetty.server.Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
                baseRequest.setHandled(true);
                receivedRequest = request;
                runn.run();
            }
        });
        server.start();
    }

    @AfterAll
    public static void destroyAll() throws Exception {
        server.stop();
    }

    @BeforeEach
    public void setup() {
        client = new HttpClient();
        try {
            client.start();
        } catch (Exception e) {
            fail("error while starting client");
        }
    }

    @AfterEach
    public void destroy() {
        try {
            client.stop();
        } catch (Exception e) {
            fail("error while stopping client");
        }
    }

    @Test
    public void test() {
        AudioInputStream stream;
        try {
            stream = AudioSystem.getAudioInputStream(RequestUtilsTest.class.getResource("/audio.wav"));
        } catch (UnsupportedAudioFileException | IOException e) {
            fail("failed to load audio file");
            return;
        }
        IWorkerAudioRequest audioRequest = new WorkerAudioRequest(new AudioRequestWithInputStream(1, stream), Collections.singletonList("dummy"));
        Request request;
        try {
            request = RequestUtils.iWorkerAudioRequestToRequest(audioRequest, client, "http://127.0.0.1:3000");
        } catch (IOException | UnsupportedAudioFileException e) {
            fail("error while converting to request");
            return;
        }

        try {
            request.send();
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            fail("error while sending the request");
        }

        runn = () -> assertEquals(audioRequest, RequestUtils.requestToIWorkerAudioRequest(receivedRequest));
    }

}
