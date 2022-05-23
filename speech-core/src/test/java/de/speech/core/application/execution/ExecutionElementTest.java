package de.speech.core.application.execution;

import de.speech.core.application.execution.element.ExecutionElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExecutionElementTest {

    @Test
    public final void testGetElement_InvalidRequiredType_ConvertingException() {
        ExecutionElement executionElement = new ExecutionElement("Data");


        assertThrows(ClassCastException.class, () -> {
            Integer data = executionElement.getElement();
        });
    }
}
