package de.speech.core.dispatcher;

import de.speech.core.dispatcher.implementation.FrameWorkDispatcher;
import de.speech.core.dispatcher.implementation.exception.NoWorkerException;
import de.speech.core.dispatcher.implementation.requestresult.WorkerAudioRequest;
import de.speech.core.framework.FrameworkManager;
import de.speech.core.framework.IFramework;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TestFrameworkDispatcher {

    @Test
    public void testGetNext() {
        IFramework framework = FrameworkManager.getInstance().findFramework("framework_0", "");
        FrameWorkDispatcher frameWorkDispatcher = new FrameWorkDispatcher(10, framework);
        frameWorkDispatcher.addWorker(new WorkerMock());

        IWorkerAudioRequest request = new WorkerAudioRequest(null, null);
        frameWorkDispatcher.dispatchRequest(request);

        try {
            ICompletableFrameworkAudioRequest completableFrameworkAudioRequest = frameWorkDispatcher.next();
            Assertions.assertNull(completableFrameworkAudioRequest.getWorkerAudioRequest().getRequest());
        } catch (InterruptedException e) {
            Assertions.fail("interrupted", e);
        }
    }

    @Test
    public void testNoWorkerAvailable() {
        IFramework framework = FrameworkManager.getInstance().findFramework("framework_0", "");
        FrameWorkDispatcher frameWorkDispatcher = new FrameWorkDispatcher(framework);

        IWorkerAudioRequest request = new WorkerAudioRequest(null, null);
        Future<IFrameworkResult> result = frameWorkDispatcher.dispatchRequest(request);

        boolean exception = false;

        try {
            result.get();
        } catch (InterruptedException e) {
            Assertions.fail("interrupted", e);
        } catch (ExecutionException e) {
            exception = true;
            Assertions.assertTrue(e.getCause() instanceof NoWorkerException);
        }

        Assertions.assertTrue(exception);
    }

    @Test
    public void removeWorker() {
        IFramework framework = FrameworkManager.getInstance().findFramework("framework_0", "");
        FrameWorkDispatcher frameWorkDispatcher = new FrameWorkDispatcher(framework);

        IWorkerCore worker = new WorkerMock();
        frameWorkDispatcher.addWorker(worker);

        IWorkerAudioRequest request = new WorkerAudioRequest(null, null);
        Future<IFrameworkResult> result = frameWorkDispatcher.dispatchRequest(request);
        try {
            frameWorkDispatcher.next().getCompletableFuture().complete(null);
            result.get();
        } catch (InterruptedException e) {
            Assertions.fail("interrupted", e);
        } catch (ExecutionException e) {
            Assertions.fail("executionException", e);
        }

        frameWorkDispatcher.removeWorker(worker);
        request = new WorkerAudioRequest(null, null);
        result = frameWorkDispatcher.dispatchRequest(request);

        try {
            result.get();
        } catch (InterruptedException e) {
            Assertions.fail("interrupted", e);
        } catch (ExecutionException e) {
            Assertions.assertTrue(e.getCause() instanceof NoWorkerException);
            return;
        }
        Assertions.fail("no executionException");
    }
}
