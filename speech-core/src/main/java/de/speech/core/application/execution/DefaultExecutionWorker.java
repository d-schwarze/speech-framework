package de.speech.core.application.execution;

import de.speech.core.application.execution.element.ExecutionElement;
import de.speech.core.logging.Loggable;

import java.util.logging.Level;

/**
 * Default schema implementation for an {@linkplain ExecutionWorker} that consumes the cached elements of
 * its parent execution part, executes them and passes them to the next part if there is one.
 *
 * Used by {@linkplain de.speech.core.application.execution.parts.ParallelizedExecutionPart},
 * {@linkplain de.speech.core.application.execution.parts.SequentialExecutionPart}.
 * @param <E>
 * @param <N>
 */
public class DefaultExecutionWorker<E, N> extends ExecutionWorker<E, N> implements Loggable {

    public DefaultExecutionWorker(ExecutionPart<E, N> parent, int identifier) {
        super(parent, identifier);
    }

    @Override
    public void runWorker() {
        while (this.getParent().isRunning()) {
            ExecutionElement executionElement = this.getParent().findNextExecutionElement();

            if (executionElement == null) continue;

            if (executionElement.getRawElement() != null) {
                try {
                    N executedElement = this.getParent().executeElement(executionElement.getElement());
                    executionElement.setElement(executedElement);
                } catch (Exception ex) {
                    executionElement.addException(this.getParent().getIdentifier(), ex);
                    executionElement.setElement(null);
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                }
            }

            this.getParent().passExecutionElementToNext(executionElement);
        }
    }
}
