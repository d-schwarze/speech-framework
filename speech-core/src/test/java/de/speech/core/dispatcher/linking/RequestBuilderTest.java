package de.speech.core.dispatcher.linking;

import de.speech.core.dispatcher.IFrameworkResult;
import de.speech.core.dispatcher.implementation.exception.NoWorkerException;
import de.speech.core.dispatcher.implementation.requestresult.FrameworkResult;
import de.speech.core.dispatcher.linking.implementation.RequestResultBuilder;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.implementation.audioRequest.AudioRequestWithInputStream;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.implementation.RequestResultStatus;
import de.speech.core.task.result.implementation.ResultStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.fail;

public class RequestBuilderTest {

    @Test
    public void testTimeoutOnCreate() {
        Future<IFrameworkResult>[] futures = new CompletableFuture[10];
        for (int i = 0; i < 10; i++)
            futures[i] = new CompletableFuture<>();
        IAudioRequest request = new AudioRequestWithInputStream(1, null);
        RequestResultBuilder builder = new RequestResultBuilder(request, futures);

        Assertions.assertThrows(TimeoutException.class, () -> builder.createRequestResult(100, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testCreate() {
        CompletableFuture<IFrameworkResult>[] futures = new CompletableFuture[10];
        IAudioRequest request = new AudioRequestWithInputStream(1, null);
        RequestResultBuilder builder = new RequestResultBuilder(request, futures);

        List<IFrameworkResult> results = new LinkedList<>();

        for (int i = 0; i < 10; i++) {
            futures[i] = new CompletableFuture<>();
        }

        for (int i = 0; i < 10; i++) {
            results.add(new FrameworkResult(i, null));
        }

        for (int i = 0; i < 10; i++) {
            futures[i].complete(results.get(i));
        }

        IAudioRequestResult result = null;
        try {
            result = builder.createRequestResult(100, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            fail("timeout", e);
        } catch (InterruptedException e) {
            fail("interrupted", e);
        }
        for (int i = 0; i < 10; i++) {
            Assertions.assertEquals(result.getResults().get(i), results.get(i).getData());
        }
    }

    @Test
    public void createWithoutTimeoutTest() {
        CompletableFuture<IFrameworkResult>[] futures = new CompletableFuture[10];
        IAudioRequest request = new AudioRequestWithInputStream(1, null);
        RequestResultBuilder builder = new RequestResultBuilder(request, futures);

        List<IFrameworkResult> results = new LinkedList<>();

        for (int i = 0; i < 10; i++) {
            futures[i] = new CompletableFuture<>();
        }

        for (int i = 0; i < 10; i++) {
            results.add(new FrameworkResult(i, null));
        }

        for (int i = 0; i < 10; i++) {
            futures[i].complete(results.get(i));
        }

        IAudioRequestResult result = null;
        try {
            result = builder.createRequestResult();
        } catch (InterruptedException e) {
            fail("interrupted", e);
        }
        for (int i = 0; i < 10; i++) {
            Assertions.assertEquals(result.getResults().get(i), results.get(i).getData());
        }
    }

    @Test
    public void testStatusAfterCreate() {
        CompletableFuture<IFrameworkResult>[] futures = new CompletableFuture[10];
        IAudioRequest request = new AudioRequestWithInputStream(1, null);
        RequestResultBuilder builder = new RequestResultBuilder(request, futures);

        for (int i = 0; i < 10; i++) {
            futures[i] = new CompletableFuture<>();
        }

        List<IFrameworkResult> results = new LinkedList<>();

        for (int i = 0; i < 10; i++) {
            results.add(new FrameworkResult(i, null));
        }

        for (int i = 0; i < 10; i++) {
            futures[i].complete(results.get(i));
        }

        IAudioRequestResult result = null;
        try {
            result = builder.createRequestResult(100, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            fail("timeout", e);
        } catch (InterruptedException e) {
            fail("interrupted", e);
        }
        RequestResultStatus status = result.getStatus();

        for (ResultStatus s : status.getStatus()) {
            Assertions.assertSame(s, ResultStatus.PROCESSED);
        }
    }

    @Test
    public void testErrorStats() {
        CompletableFuture<IFrameworkResult>[] futures = new CompletableFuture[10];
        IAudioRequest request = new AudioRequestWithInputStream(1, null);
        RequestResultBuilder builder = new RequestResultBuilder(request, futures);

        for (int i = 0; i < 10; i++) {
            futures[i] = new CompletableFuture<>();
        }

        List<IFrameworkResult> results = new LinkedList<>();

        for (int i = 0; i < 10; i++) {
            results.add(new FrameworkResult(i, null));
        }

        for (int i = 0; i < 9; i++) {
            futures[i].complete(results.get(i));
        }

        futures[9].completeExceptionally(new NoWorkerException("now Worker"));

        IAudioRequestResult result = null;
        try {
            result = builder.createRequestResult();
        } catch (InterruptedException e) {
            fail("interrupted", e);
        }
        RequestResultStatus status = result.getStatus();

        for (int i = 0; i < 9; i++) {
            Assertions.assertSame(status.getStatus()[i], ResultStatus.PROCESSED);
        }

        Assertions.assertEquals(1, status.getErrors().size());
        Assertions.assertTrue(status.getErrors().get(0) instanceof NoWorkerException);
    }

    @Test
    public void isDoneFalseTest() {
        CompletableFuture<IFrameworkResult>[] futures = new CompletableFuture[10];
        IAudioRequest request = new AudioRequestWithInputStream(1, null);
        RequestResultBuilder builder = new RequestResultBuilder(request, futures);

        for (int i = 0; i < 10; i++) {
            futures[i] = new CompletableFuture<>();
        }

        Assertions.assertFalse(builder.isDone());
    }

    @Test
    public void isDoneTrueTest() {
        CompletableFuture<IFrameworkResult>[] futures = new CompletableFuture[10];
        IAudioRequest request = new AudioRequestWithInputStream(1, null);
        RequestResultBuilder builder = new RequestResultBuilder(request, futures);

        for (int i = 0; i < 10; i++) {
            futures[i] = new CompletableFuture<>();
        }

        List<IFrameworkResult> results = new LinkedList<>();

        for (int i = 0; i < 10; i++) {
            results.add(new FrameworkResult(i, null));
        }

        for (int i = 0; i < 10; i++) {
            futures[i].complete(results.get(i));
        }

        Assertions.assertTrue(builder.isDone());
    }

    @Test
    public void createWithException() {
        CompletableFuture<IFrameworkResult>[] futures = new CompletableFuture[10];
        IAudioRequest request = new AudioRequestWithInputStream(1, null);
        RequestResultBuilder builder = new RequestResultBuilder(request, futures);

        for (int i = 0; i < 10; i++) {
            futures[i] = new CompletableFuture<>();
        }

        List<IFrameworkResult> results = new LinkedList<>();

        for (int i = 0; i < 10; i++) {
            results.add(new FrameworkResult(i, null));
        }

        for (int i = 0; i < 9; i++) {
            futures[i].complete(results.get(i));
        }
        futures[9].completeExceptionally(new RuntimeException(""));

        IAudioRequestResult result = null;
        try {
            result = builder.createRequestResult(100, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            fail("timeout", e);
        } catch (InterruptedException e) {
            fail("interrupted", e);
        }
        Assertions.assertEquals(result.getStatus().getErrors().size(), 1);
    }
}
