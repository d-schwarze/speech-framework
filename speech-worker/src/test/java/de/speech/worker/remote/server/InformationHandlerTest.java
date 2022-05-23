package de.speech.worker.remote.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InformationHandlerTest {

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
                .POST("http://localhost:3000/info")
                .send();
        assertEquals(200, response.getStatus());
        JsonObject jsonObj = new Gson().fromJson(response.getContentAsString(), JsonObject.class);
        assertEquals("dummy worker", jsonObj.get("name").getAsString());
        assertEquals("dummy model", jsonObj.get("model").getAsString());
        assertEquals(3, jsonObj.get("maxQueueSize").getAsInt());
        assertEquals(1, jsonObj.get("currentQueueSize").getAsInt());
    }

}
