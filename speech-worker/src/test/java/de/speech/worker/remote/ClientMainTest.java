package de.speech.worker.remote;

import com.google.gson.Gson;
import de.speech.core.dispatcher.IFrameworkResult;
import de.speech.core.dispatcher.implementation.requestresult.FrameworkResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientMainTest {

    private static IFrameworkResult result;
    private static Runnable runn;

    private ClientMain clientMain;

    @BeforeAll
    public static void setupAll() {
        Server server = new Server(3000);
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
                baseRequest.setHandled(true);
                result = new Gson().fromJson(request.getReader(), FrameworkResult.class);
                runn.run();
            }
        });
    }

    @BeforeEach
    public void setup() {
        try {
            clientMain = new ClientMain("localhost:3000", "/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSendResultToCore() {
        IFrameworkResult result = new FrameworkResult(0, new DummyISpeechToTextServiceData());
        clientMain.sendResultToCore(result);
        runn = () -> assertEquals(result, ClientMainTest.result);
    }
}
