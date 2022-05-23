package de.speech.core.application.execution.element;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Element that is passed threw the {@linkplain de.speech.core.application.execution.ExecutionSystem}.
 * Contains all data, that should passed with the actual data object like a future for the result.
 */
public class ExecutionElement {

    /**
     * Data that is executed during the phases.
     * May change after a {@linkplain de.speech.core.application.execution.ExecutionPart} finished.
     */
    private Object rawElement;

    /**
     * Future that is called at the the of the execution phase by
     * {@linkplain de.speech.core.application.execution.parts.implementations.CallbackExecutionPart}.
     * Provides the ability to wait for the ending of the execution and get the final result.
     */
    private CompletableFuture<?> completableFuture;

    /**
     * Collects all raised exceptions during the execution phases.
     */
    private Map<String, Exception> occurredExceptions = new HashMap<>();

    public ExecutionElement(Object element) {
        this.rawElement = element;
    }

    public ExecutionElement(Object element, CompletableFuture<?> completableFuture) {
        this.rawElement = element;
        this.completableFuture = completableFuture;
    }

    public Object getRawElement() {
        return this.rawElement;
    }

    public void setElement(Object element) {
        this.rawElement = element;
    }

    /**
     * Getter for the data element. Element is converted to the needed type.
     * @param <E> type to which the element should be converted
     * @return converted element
     */
    public <E> E getElement() throws ClassCastException {
        E element = (E) rawElement;


        return element;
    }

    /**
     * Adds a raised exception.
     * @param partIdentifier part in which the exception was raised
     * @param ex raised exception
     */
    public void addException(String partIdentifier, Exception ex) {
        if (ex != null && partIdentifier != null)
            occurredExceptions.put(partIdentifier, ex);
    }

    /**
     * Determines whether any exceptions were raised or not.
     * @return true if an exception was raised
     */
    public boolean hasExceptions() {
        return !occurredExceptions.isEmpty();
    }

    public Map<String, Exception> getOccurredExceptions() {
        return this.occurredExceptions;
    }

    /*public void setElement(E element) {
        this.element = element;
    }*/

    public CompletableFuture<?> getCompletableFuture() {
        return completableFuture;
    }

    public void setCompletableFuture(CompletableFuture<?> completableFuture) {
        this.completableFuture = completableFuture;
    }
}
