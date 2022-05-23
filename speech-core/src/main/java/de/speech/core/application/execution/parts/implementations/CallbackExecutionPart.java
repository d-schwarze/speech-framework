package de.speech.core.application.execution.parts.implementations;

import de.speech.core.application.execution.ExecutionErrorException;
import de.speech.core.application.execution.ExecutionPart;
import de.speech.core.application.execution.element.ExecutionElement;
import de.speech.core.logging.Loggable;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class CallbackExecutionPart extends ExecutionPart<ExecutionElement, ExecutionElement> implements Loggable {

    private final static String CALLBACK_PART_IDENTIFIER = "callbackPart";

    public CallbackExecutionPart() {
        this(CALLBACK_PART_IDENTIFIER, null);
    }

    public CallbackExecutionPart(String identifier, ExecutionPart<ExecutionElement, ?> next) {
        super(identifier, next);
    }

    @Override
    public void start() { }

    @Override
    public void addExecutionElement(ExecutionElement element) {
        executeElement(element);
    }

    @Override
    public ExecutionElement executeElement(ExecutionElement element) {
        if (element.getCompletableFuture() != null) {
            CompletableFuture<?> completableFuture = element.getCompletableFuture();
            if (!element.hasExceptions()) {
                try {
                    completableFuture.complete(element.getElement());
                } catch (ClassCastException e) {
                    element.addException(this.getIdentifier(), e);
                    element.setElement(null);
                    LOGGER.log(Level.WARNING, e.getMessage(), e);
                }
            }

            if (element.hasExceptions()) {
                completableFuture.completeExceptionally(new ExecutionErrorException(element.getOccurredExceptions()));
            }
        }

        return null;
    }

    @Override
    public boolean isCacheFull() {
        return false;
    }

    @Override
    public ExecutionElement findNextExecutionElement() {
        return null;
    }
}
