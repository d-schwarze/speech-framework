package de.speech.worker.remote.server;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InitHandlerTest {

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
    public void testValidRequest() throws InterruptedException, ExecutionException, TimeoutException {
        ContentResponse response = client
                .POST("http://127.0.0.1:3000/init")
                .param("endpoint", "/test")
                .param("port", String.valueOf(4000))
                .send();
        assertEquals(200, response.getStatus());
        assertEquals("/test", networkHandler.getEndpoint());
        assertEquals(4000, networkHandler.getPort());
    }

    @Test
    public void testInvalidRequest() throws InterruptedException, ExecutionException, TimeoutException {
        ContentResponse response = client
                .POST("http://127.0.0.1:3000/init")
                .send();
        assertEquals(400, response.getStatus());
    }

}
