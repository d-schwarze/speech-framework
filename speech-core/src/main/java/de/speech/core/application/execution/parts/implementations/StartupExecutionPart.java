package de.speech.core.application.execution.parts.implementations;

import de.speech.core.application.execution.ExecutionPart;
import de.speech.core.application.execution.element.ExecutionElement;

import java.util.ArrayList;
import java.util.Collection;

/**
 * CustomExecutionPart that should be run on startup of an {@linkplain de.speech.core.application.execution.ExecutionSystem}.
 * This part just adds a given list of elements to the next part.
 *
 * For more information on the behaviour of an StartupExecutionPart see
 * {@linkplain de.speech.core.application.execution.ExecutionSystem}.
 *
 * @param <E> element type
 */
public class StartupExecutionPart<E> extends ExecutionPart<E, E> {

    private Collection<E> startupElements;

    private final static String STARTUP_PART_IDENTIFIER = "startupPart";

    public StartupExecutionPart(Collection<E> startupElements) {
        this(startupElements, STARTUP_PART_IDENTIFIER, null);
    }

    public StartupExecutionPart(Collection<E> startupElements, String identifier, ExecutionPart<E, ?> next) {
        super(identifier, next);

        if (startupElements != null) {
            this.startupElements = startupElements;
        } else {
            this.startupElements = new ArrayList<>();
        }
    }

    @Override
    public void start() {
        for (E element : startupElements) {
            this.passExecutionElementToNext(new ExecutionElement(element, null));
        }
    }

    @Override
    public void addExecutionElement(ExecutionElement element) {
        this.passExecutionElementToNext(element);
    }

    /**
     * Returns the output element without any modification of the element or any other logic.
     * @param element element that should be for used execution
     * @return element that was given as parameter
     */
    @Override
    public E executeElement(E element) {
        return element;
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
