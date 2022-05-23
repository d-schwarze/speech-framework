package de.speech.core.application.execution.parts;


import de.speech.core.application.execution.DefaultExecutionWorker;
import de.speech.core.application.execution.ExecutionPart;
import de.speech.core.application.execution.ExecutionWorker;
import de.speech.core.application.execution.element.ExecutionElement;
import de.speech.core.logging.Loggable;

import java.util.logging.Level;

/**
 * Class for a sequential execution part. An execution part can not be sequential by design, because the execution system
 * and its execution parts should run parallel to the speech application and so to the main program counter. sequential
 * means in the context of the execution pipeline, that only one element can be processed by this part and each previous
 * part has to wait, until the element is executed.
 *
 * @param <E> input element type
 * @param <N> output element type/element type of the next part
 */
public abstract class SequentialExecutionPart<E, N> extends ExecutionPart<E, N> implements Loggable {

    /**
     * Implementation of the cache. Only one element to provide an sequential behaving execution part.
     */
    protected ExecutionElement element;

    /**
     * Worker to run the elements parallel but sequential in case of the execution pipeline.
     */
    protected ExecutionWorker<E, N> worker;

    /**
     * Thread for the {@linkplain #worker}
     */
    protected Thread workerThread;

    public SequentialExecutionPart(String identifier, ExecutionPart<N, ?> next) {
        super(identifier, next);

        worker = new DefaultExecutionWorker<>(this, 0);

    }

    /**
     * Starts the {@linkplain #workerThread} and its {@linkplain #worker}. Afterwards {@linkplain #addExecutionElement(ExecutionElement)} Element elements
     * may be added}.
     */
    @Override
    public void onStart() {
        workerThread = new Thread(worker);
        workerThread.start();
    }

    @Override
    public void onStop() {
        workerThread.interrupt();
    }

    /**
     * Adds an element to the cache. If the cache is full, the invoking thread has to wait.
     * Because the cache consists only of one element can be cached an only one element can be executed.
     * @param element new element for the cache
     */
    @Override
    public synchronized void addExecutionElement(ExecutionElement element) {
        while (this.element != null) {
            try {
                wait();
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
            }
        }

        this.element = element;
        this.notifyAll();
    }


    @Override
    public synchronized boolean isCacheFull() {
        return (element != null);
    }

    /**
     * Gets the next element of the queue. If none was found, the {@linkplain #worker} has to wait.
     * @return next element of the queue.
     */
    @Override
    public synchronized ExecutionElement findNextExecutionElement() {
        while (this.element == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                return null;
            }
        }
        ExecutionElement e = this.element;
        this.element = null;

        this.notifyAll();
        return e;
    }
}
