package de.speech.core.application.execution.parts;

import de.speech.core.application.execution.DefaultExecutionWorker;
import de.speech.core.application.execution.ExecutionPart;
import de.speech.core.application.execution.element.ExecutionElement;
import de.speech.core.logging.Loggable;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

/**
 * Implementation for an parallelized execution part. A parallelized execution part has a pool of threads that
 * run {@linkplain DefaultExecutionWorker default workers}. It also has a thread-safe queue for the cache with
 * a fixed size. If it is tried to add more elements that are allowed by the queue size, the calling thread is
 * blocked.
 * @param <E> input element type
 * @param <N> output element type/element type of the next part
 */
public abstract class ParallelizedExecutionPart<E, N> extends ExecutionPart<E, N> implements Loggable {

    /**
     * Thread pool
     */
    private ExecutorService availableExecutors;

    /**
     * Blocking queue as cache implementation
     */
    protected BlockingQueue<ExecutionElement> elements;

    /**
     * Maximum of elements tha may be hold in cache.
     */
    private int queueSize;

    /**
     * Amount threads/workers
     */
    private int numberOfExecutors;

    public ParallelizedExecutionPart(String identifier, int queueSize, int numberOfExecutors, ExecutionPart<N, ?> next) {
        super(identifier, next);

        this.queueSize = queueSize;
        this.elements = new LinkedBlockingQueue<>(this.queueSize);
        this.numberOfExecutors = numberOfExecutors;
    }

    /**
     * Start the execution part by starting all possible {@linkplain DefaultExecutionWorker workers}.
     */
    @Override
    public void onStart() {
        if (availableExecutors == null) {
            availableExecutors = Executors.newFixedThreadPool(this.numberOfExecutors);
        }

        for (int i = 0; i < this.numberOfExecutors; i++) {
            availableExecutors.submit(new DefaultExecutionWorker<>(this, i));
        }
    }

    @Override
    public void onStop() {
        this.availableExecutors.shutdownNow();
    }

    /**
     * Adds an element to the cache. If the cache is full, the invoking thread has to wait.
     *
     * @param element new element for the cache
     */
    @Override
    public void addExecutionElement(ExecutionElement element) {
        try {
            this.elements.put(element);
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    @Override
    public synchronized boolean isCacheFull() {
        return (elements.size() >= queueSize);
    }

    /**
     * Gets the next element of the queue. If none was found, the invoking thread has to wait.
     * @return next element of the queue.
     */
    @Override
    public ExecutionElement findNextExecutionElement() {
        ExecutionElement element;
        try {
            element = this.elements.take();
        } catch (InterruptedException e) {
            return null;
        }

        return element;
    }
}
