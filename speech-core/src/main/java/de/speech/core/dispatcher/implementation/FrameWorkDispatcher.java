package de.speech.core.dispatcher.implementation;

import de.speech.core.dispatcher.*;
import de.speech.core.dispatcher.implementation.exception.NoWorkerException;
import de.speech.core.dispatcher.implementation.requestresult.CompletableFrameworkAudioRequest;
import de.speech.core.framework.IFramework;
import de.speech.core.logging.SpeechLogging;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * This object defines an frameworkDispatcher and a request source. It caches all incoming requests
 * in a blockingqueue.
 */
public class FrameWorkDispatcher implements IFrameWorkDispatcher, IWorkerAudioRequestSource, IStatusChangeListener {

    private static final int DEFAULT_QUEUE_SIZE = 500;
    private final Logger logger = SpeechLogging.getLogger();

    private final List<IWorkerCore> workers = new ArrayList<>();

    private final BlockingQueue<ICompletableFrameworkAudioRequest> queue;
    private final IFramework framework;

    /**
     * Creates a new FrameworkDispatcher with the given capacity of {@linkplain IWorkerAudioRequest}s.
     *
     * @param capacity  size of the queue
     * @param framework framework of the dispatcher
     */
    public FrameWorkDispatcher(int capacity, IFramework framework) {
        queue = new LinkedBlockingQueue<>(capacity);
        this.framework = framework;
    }

    /**
     * Creates a new FrameworkDispatcher with standard queue size.
     *
     * @param framework framework
     */
    public FrameWorkDispatcher(IFramework framework) {
        queue = new LinkedBlockingQueue<>(DEFAULT_QUEUE_SIZE);
        this.framework = framework;
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public List<IWorkerCore> getWorkers() {
        return workers;
    }

    @Override
    public void addWorker(IWorkerCore worker) {
        worker.setRequestSource(this);
        workers.add((worker));
        worker.addStatusChangeListener(this);
    }

    @Override
    public void removeWorker(IWorkerCore worker) {
        workers.remove(worker);
        worker.removeStatusChangeListener(this);
    }

    @Override
    public IFramework getFramework() {
        return framework;
    }

    /**
     * Adds a requests to the blockingqueue. If the queue is full, the thread waits til the
     * queue is free
     *
     * @param request request
     * @return Future with frameworkResult
     */
    @Override
    public Future<IFrameworkResult> dispatchRequest(IWorkerAudioRequest request) {
        CompletableFuture<IFrameworkResult> future = new CompletableFuture<>();
        if (!hasNotTerminatedWorker()) {
            future.completeExceptionally(new NoWorkerException("no valid worker available"));
            return future;
        }

        ICompletableFrameworkAudioRequest completableRequest = new CompletableFrameworkAudioRequest(future, request);
        try {
            queue.put(completableRequest);
        } catch (InterruptedException e) {
            logger.warning("interrupted during dispatching");
        }
        return future;
    }

    @Override
    public ICompletableFrameworkAudioRequest next() throws InterruptedException {
        return queue.take();
    }

    @Override
    public ICompletableFrameworkAudioRequest next(long time, TimeUnit unit) throws InterruptedException {
        return queue.poll(time, unit);
    }

    @Override
    public synchronized void statusChanged(IWorkerCore worker) {
        if (worker.getStatus() == WorkerStatus.WORKER_STOPPED) {
            if (!hasNotTerminatedWorker()) {
                completeLeftRequests();
            }
        }
    }

    private boolean hasNotTerminatedWorker() {
        boolean notTerminatedWorker = false;
        for (IWorkerCore worker : workers) {
            if (worker.getStatus() != WorkerStatus.WORKER_STOPPED) {
                notTerminatedWorker = true;
            }
        }

        return notTerminatedWorker;
    }

    private synchronized void completeLeftRequests() {
        while (!queue.isEmpty()) {
            queue.remove().getCompletableFuture().completeExceptionally(new NoWorkerException("Dispatcher has no valid workers"));
        }
    }
}
