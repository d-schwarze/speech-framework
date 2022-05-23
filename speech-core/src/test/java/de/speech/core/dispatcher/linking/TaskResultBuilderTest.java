package de.speech.core.dispatcher.linking;

import de.fraunhofer.iosb.spinpro.speechtotext.NullObjectSpeechToTextServiceMetadata;
import de.speech.core.dispatcher.IFrameworkResult;
import de.speech.core.dispatcher.TaskMockFactory;
import de.speech.core.dispatcher.implementation.requestresult.FrameworkResult;
import de.speech.core.dispatcher.linking.implementation.TaskResultBuilder;
import de.speech.core.framework.IFramework;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.ITask;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ITaskResult;
import de.speech.core.task.result.SpeechToTextServiceData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class TaskResultBuilderTest {

    private static final int FRAMEWORKS = 50;
    private static final int REQUEST_AMOUNT = 100;

    private ITask task;
    private ITaskResultBuilder resultBuilder;

    @BeforeEach
    public void setupTask() {
        task = new TaskMockFactory().createTask(FRAMEWORKS, REQUEST_AMOUNT);
        resultBuilder = new TaskResultBuilder(task);
    }

    @Test
    public void testTimeoutWithoutAddedFutures() {
        Assertions.assertThrows(TimeoutException.class, () -> resultBuilder.get(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testTimeoutWithAddedFutures() {
        for (IAudioRequest request : task.getAudioRequests()) {
            CompletableFuture[] results = new CompletableFuture[task.getFrameworkConfigurations().size()];
            for (int i = 0; i < task.getFrameworkConfigurations().size(); i++) {
                results[i] = new CompletableFuture<>();
            }

            resultBuilder.addRequestResult(results, request);
        }

        Assertions.assertThrows(TimeoutException.class, () -> resultBuilder.get(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testCancel() {
        for (IAudioRequest request : task.getAudioRequests()) {
            Future<IFrameworkResult>[] results = new Future[task.getFrameworkConfigurations().size()];
            for (int i = 0; i < task.getFrameworkConfigurations().size(); i++) {
                results[i] = new CompletableFuture<>();
            }

            resultBuilder.addRequestResult(results, request);
        }

        resultBuilder.cancel(true);
        assertTrue(resultBuilder.isCancelled());
        Assertions.assertThrows(CancellationException.class, () -> resultBuilder.get(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testNoCancelBecauseFinished() {
        for (IAudioRequest request : task.getAudioRequests()) {
            CompletableFuture<IFrameworkResult>[] results = new CompletableFuture[task.getFrameworkConfigurations().size()];
            for (int i = 0; i < task.getFrameworkConfigurations().size(); i++) {
                results[i] = new CompletableFuture<>();
                results[i].complete(new FrameworkResult(0, null));
            }

            resultBuilder.addRequestResult(results, request);
        }

        Assertions.assertFalse(resultBuilder.cancel(true));
        try {
            resultBuilder.get(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("interrupted", e);
        } catch (ExecutionException e) {
            fail("exception during execution", e);
        } catch (TimeoutException e) {
            fail("timeout", e);
        }
    }

    @Test
    public void testIsDoneTrue() {
        for (IAudioRequest request : task.getAudioRequests()) {
            CompletableFuture<IFrameworkResult>[] results = new CompletableFuture[task.getFrameworkConfigurations().size()];
            for (int i = 0; i < task.getFrameworkConfigurations().size(); i++) {
                IFramework framework = task.getFrameworkConfigurations().get(i).getFramework();
                results[i] = new CompletableFuture<>();
                results[i].complete(null);
            }

            resultBuilder.addRequestResult(results, request);
        }

        assertTrue(resultBuilder.isDone());
    }

    @Test
    public void testIsDoneFalseBecauseNotCompleted() {
        for (IAudioRequest request : task.getAudioRequests()) {
            Future<IFrameworkResult>[] results = new Future[task.getFrameworkConfigurations().size()];
            for (int i = 0; i < task.getFrameworkConfigurations().size(); i++) {
                results[i] = new CompletableFuture<>();
            }

            resultBuilder.addRequestResult(results, request);
        }

        assertFalse(resultBuilder.isDone());
    }

    @Test
    public void completeExceptionallyTest() {
        Assertions.assertTrue(resultBuilder.completeExceptionally(new RuntimeException("")));

        boolean exception = false;

        try {
            resultBuilder.get();
        } catch (ExecutionException e) {
            exception = true;
            Assertions.assertTrue(e.getCause() instanceof RuntimeException);
        } catch (InterruptedException e) {
            fail("interrupted", e);
        }

        Assertions.assertTrue(exception);
    }

    @Test
    public void cantCompleteExceptionallyBecauseDoneTest() {
        for (IAudioRequest request : task.getAudioRequests()) {
            CompletableFuture<IFrameworkResult>[] results = new CompletableFuture[task.getFrameworkConfigurations().size()];
            for (int i = 0; i < task.getFrameworkConfigurations().size(); i++) {
                IFramework framework = task.getFrameworkConfigurations().get(i).getFramework();
                results[i] = new CompletableFuture<>();
                results[i].complete(null);
            }

            resultBuilder.addRequestResult(results, request);
        }

        Assertions.assertFalse(resultBuilder.completeExceptionally(new RuntimeException("")));
    }

    @Test
    public void testGetWithoutTimeout() {
        for (IAudioRequest request : task.getAudioRequests()) {
            CompletableFuture<IFrameworkResult>[] results = new CompletableFuture[task.getFrameworkConfigurations().size()];
            for (int i = 0; i < task.getFrameworkConfigurations().size(); i++) {
                IFramework framework = task.getFrameworkConfigurations().get(i).getFramework();
                results[i] = new CompletableFuture<>();
                results[i].complete(new FrameworkResult(0, new SpeechToTextServiceData(new NullObjectSpeechToTextServiceMetadata("s"), null)));
            }

            resultBuilder.addRequestResult(results, request);
        }

        try {
            ITaskResult<IAudioRequestResult> result = resultBuilder.get();

            for (IAudioRequestResult r : result.getResults()) {
                Assertions.assertTrue(r.getStatus().getErrors().isEmpty());
            }
        } catch (InterruptedException e) {
            fail("interrupted", e);
        } catch (ExecutionException e) {
            fail("error during exceution", e);
        }
    }
}