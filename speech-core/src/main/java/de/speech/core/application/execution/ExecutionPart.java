package de.speech.core.application.execution;

import de.speech.core.application.execution.element.ExecutionElement;

/**
 * An CustomExecutionPart wraps an execution phase into an usable part of the execution pipeline that is managed within
 * {@linkplain ExecutionSystem}. Elements can be passed through an operations can be run onto the elements. Also
 * elements can be change into different output types.
 *
 * Purpose of this class is to bundle the main functionality of the execution phase into {@linkplain #executeElement}
 * and make it parallelizable so that it can be run onto {@linkplain ExecutionWorker}.
 *
 * The consumer/produces pattern is used to create the communication of two consecutive ExecutionParts. <b>Each
 * CustomExecutionPart is a consumer and a producer</b>. A CustomExecutionPart consumes the elements of its previous part and
 * produces elements for its next part. The previous part does not have to be CustomExecutionPart an can be also for instance
 * the {@linkplain ExecutionSystem}.
 *
 * Examples for execution parts are:
 * <ul>
 *      <li>
 *          {@linkplain de.speech.core.application.execution.parts.implementations.PostProcessingExecutionPart PostProcessing Phase}
 *      </li>
 *      <li>
 *          {@linkplain de.speech.core.application.execution.parts.implementations.DispatcherExecutionPart Dispatcher Phase}
 *      </li>
 * </ul>
 * @param <E> input element type
 * @param <N> output element type
 */
public abstract class ExecutionPart<E, N> {

    /**
     * Consumer part.
     */
    protected ExecutionPart<N, ?> next;

    /**
     * Unique identifier of an CustomExecutionPart.
     */
    private String identifier;

    private boolean running;

    public ExecutionPart(String identifier, ExecutionPart<N, ?> next) {
        this.identifier = identifier;
        this.next = next;
    }

    /**
     * Starts the execution part. Afterwards {@linkplain #addExecutionElement(ExecutionElement)} Element elements may be added}.
     */
    public void start() {
        running = true;

        this.onStart();
    }

    public void onStart() { }

    public void stop() {
        running = false;

        this.onStop();
    }

    public void onStop() { }

    /**
     * Passes the output element to the {@linkplain #next} part.
     * @param element output element that should be consumed by the next part
     */
    public void passExecutionElementToNext(ExecutionElement element) {
        if (next != null) {
            next.addExecutionElement(element);
        }
    }

    /**
     * Add a new element into the cache of the part
     * @param element to be added
     */
    public abstract void addExecutionElement(ExecutionElement element);

    /**
     * Executes the actual underlying phase on an element.
     * @param element element that should be for used execution
     * @return output element for the next part
     * @throws Exception possible exception occurred during the execution
     */
    public abstract N executeElement(E element) throws Exception;

    /**
     * Checks whether the cache of the part is full.
     * @return true if the cache is full
     */
    public abstract boolean isCacheFull();

    /**
     * Finds the next element of the cache that should be executed.
     * @return next element
     */
    public abstract ExecutionElement findNextExecutionElement();

    /**
     * Getter for the identifier
     * @return {@linkplain #identifier}
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Getter for the next CustomExecutionPart
     * @return {@linkplain #next}
     */
    public ExecutionPart<N, ?> getNext() {
        return this.next;
    }

    /**
     * Setter for the next CustomExecutionPart
     * @param nextExecutionPart new next
     */
    protected void setNextExecutionPart(ExecutionPart<N, ?> nextExecutionPart) {
        this.next = nextExecutionPart;
    }

    public boolean isRunning() {
        return running;
    }
}
