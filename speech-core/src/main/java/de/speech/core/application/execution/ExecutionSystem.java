package de.speech.core.application.execution;

import de.speech.core.application.execution.element.ExecutionElement;
import de.speech.core.application.execution.parts.implementations.CallbackExecutionPart;
import de.speech.core.application.execution.parts.implementations.StartupExecutionPart;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * The ExecutionSystem contains all {@linkplain ExecutionPart execution parts} that are part of the speech process. With
 * the system, processing elements can be run parallel to the speech application.
 * For instance: After Element A is added to the system,
 * the application is ready to add another Element B to the system.
 *
 * The default speech process contains:
 * <ol>
 *     <li>
 *         DispatchingProcess
 *     </li>
 *     <li>
 *         {@linkplain de.speech.core.application.execution.parts.implementations.PostProcessingExecutionPart PostProcessingProcess}
 *     </li>
 *     And additionally in case you may be interested in testing speech-to-text frameworks via the module
 *     speech-dev:
 *     <li>
 *         Developer Specific Processes like the TargetActualComparison
 *     </li>
 * </ol>
 *
 * The {@linkplain ExecutionPart execution parts} are stored as an linked list to represent a pipeline of execution. Which means that
 * an element start with {@linkplain #startingExecutionPart} and goes on with the next part until {@linkplain #endingExecutionPart}
 * has finally executed the element. An element may change during the pipeline.
 *
 * @param <E>
 */
public class ExecutionSystem<E> {

    /**
     * Starting part for the execution pipeline.
     */
    private ExecutionPart<E, ?> startingExecutionPart;

    /**
     * Ending part for the execution pipeline.
     */
    private ExecutionPart endingExecutionPart;

    /**
     * Part that should be run once on the startup of the ExecutionSystem. Its purpose is, to put on startup
     * a set of elements into the execution pipeline.
     * This CustomExecutionPart is not part of the execution pipeline and thus is run neither on {@linkplain #executeElement}
     * nor on {@linkplain #executeElements}
     */
    private ExecutionPart<E, E> startupExecutionPart;

    private ExecutionPart callbackExecutionPart;

    public ExecutionSystem(ExecutionPart... parts) {
        this(Arrays.asList(parts));
    }

    public ExecutionSystem(List<ExecutionPart> parts) {
        if (parts == null || parts.isEmpty()) throw new IllegalArgumentException("System needs at least on part.");

        initializeExecutionParts(parts);
    }

    /**
     * Constructor for creating the execution pipeline and also putting some elements into it immediately after startup.
     *
     * @param startupElements elements that should be put into the pipeline after this system has started
     * @param parts parts of the execution pipeline
     */
    public ExecutionSystem(Collection<E> startupElements, ExecutionPart... parts) {
        this(startupElements, Arrays.asList(parts));
    }

    /**
     * Constructor for creating the execution pipeline and also putting some elements into it immediately after startup.
     *
     * @param startupElements elements that should be put into the pipeline after this system has started
     * @param parts parts of the execution pipeline
     */
    public ExecutionSystem(Collection<E> startupElements, List<ExecutionPart> parts) {
        if (parts == null || parts.isEmpty()) throw new IllegalArgumentException("System needs at least on part.");

        startupExecutionPart = new StartupExecutionPart<>(startupElements);
        startupExecutionPart.setNextExecutionPart(parts.get(0));
        initializeExecutionParts(parts);
    }

    /**
     * Initializes the execution pipeline. Pipeline is build like a linked list.
     *
     * @param parts parts of the execution pipeline
     */
    private void initializeExecutionParts(List<ExecutionPart> parts) {
        startingExecutionPart = endingExecutionPart = parts.get(0);

        for (int i = 1; i < parts.size(); i++) {
            addExecutionPart(parts.get(i));
        }

        callbackExecutionPart = new CallbackExecutionPart();
        endingExecutionPart.setNextExecutionPart(callbackExecutionPart);
    }

    /**
     * Starts the system and its execution pipeline.
     * Elements that should be put directly into the execution pipeline are added.
     * Afterwards elements can be added.
     */
    public synchronized void startSystem() {
        if (startupExecutionPart != null)
            startupExecutionPart.start();

        ExecutionPart part = startingExecutionPart;
        do {
            part.start();
            part = part.getNext();
        } while(part != null);
    }

    public void stopSystem() {
        if (startupExecutionPart != null) {
            startupExecutionPart.stop();
        }

        ExecutionPart part = startingExecutionPart;

        do {
            part.stop();
            part = part.getNext();
        } while (part != null);
    }


    /**
     * Adds a new {@linkplain ExecutionPart execution parts} <b>at the end</b> of the execution pipeline.
     * @param executionPart new part
     */
    public void addExecutionPart(ExecutionPart executionPart) {
        endingExecutionPart.setNextExecutionPart(executionPart);

        endingExecutionPart = executionPart;
        executionPart.setNextExecutionPart(callbackExecutionPart);
    }

    /**
     * Puts an element into the execution pipeline and consequently executes it.
     * Note that it depends on the {@linkplain #startingExecutionPart} if the system returns immediately or if it
     * has to wait.
     * For instance if {@linkplain #startingExecutionPart} is a derivative of
     * {@linkplain de.speech.core.application.execution.parts.ParallelizedExecutionPart} and its queue is not full
     * the method will return immediately. In case the queue is full, the method will only return if a new place
     * is free.
     *
     * @param element element that should be executed
     * @return a future to wait for the result of this particular {@code element}
     */
    public <F> Future<F> executeElement(E element) {
        CompletableFuture future = new CompletableFuture<>();
        ExecutionElement executionElement = new ExecutionElement(element, future);
        startingExecutionPart.addExecutionElement(executionElement);

        return future;
    }


    /**
     * Puts a list of elements into the execution pipeline and consequently executes it.
     * Note that it depends on the {@linkplain #startingExecutionPart} if the system returns immediately or if it
     * has to wait.
     *  
     * @param elements elements that should be put into the execution pipeline
     * @see #executeElement
     */
    public void executeElements(List<E> elements) {
        for (E element : elements) {
            executeElement(element);
        }
    }

    public ExecutionPart<E, ?> getStartingExecutionPart() {
        return this.startingExecutionPart;
    }

    public ExecutionPart getEndingExecutionPart() {
        return this.endingExecutionPart;
    }

    public ExecutionPart<E, E> getStartupExecutionPart() {
        return this.startupExecutionPart;
    }

    public ExecutionPart getCallbackExecutionPart() {
        return this.callbackExecutionPart;
    }
}
