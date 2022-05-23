package de.speech.core.application.execution.parts;

import de.speech.core.application.execution.ExecutionPart;
import de.speech.core.application.execution.element.ExecutionElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class SequentialExecutionPartTest {

    private final static Logger LOGGER = Logger.getLogger(SequentialExecutionPartTest.class.getName());


    private SequentialExecutionPart<String, String> sequentialExecutionPart;

    @BeforeEach
    public final void initializeTest() {
        sequentialExecutionPart = new SequentialExecutionPartMock(null, "testSequentialPart");
        sequentialExecutionPart.start();
    }

    @Test
    public final void testCacheFull() {

        sequentialExecutionPart.addExecutionElement(new ExecutionElement("input1", null));
        sequentialExecutionPart.addExecutionElement(new ExecutionElement("input2", null));

        assertTrue(sequentialExecutionPart.isCacheFull());
    }

    @Test
    public final void testElementPassing() throws InterruptedException {

        Object waitObject = new Object();

        List<String> actual = new ArrayList<>();
        actual.add("input1");
        actual.add("input2");

        List<String> results = new ArrayList<>();

        SequentialExecutionPart endPart = new SequentialExecutionPart<String, String>("endTest", null) {
            @Override
            public String executeElement(String element) {

                results.add(element);

                if (results.size() == 2) {
                    synchronized (waitObject) {
                        waitObject.notifyAll();
                    }
                }

                return null;
            }
        };
        endPart.start();

        sequentialExecutionPart = new SequentialExecutionPartMock(endPart, "testSequentialPart");
        sequentialExecutionPart.start();

        sequentialExecutionPart.addExecutionElement(new ExecutionElement(actual.get(0), null));
        sequentialExecutionPart.addExecutionElement(new ExecutionElement(actual.get(1), null));


        synchronized (waitObject) {
            waitObject.wait(5000);

            assertEquals(2, results.size());
        }

        assertEquals(actual, results);
    }

    class SequentialExecutionPartMock extends SequentialExecutionPart<String, String> {


        public SequentialExecutionPartMock(ExecutionPart<String, ?> next, String identifier) {
            super(identifier, next);
        }

        @Override
        public String executeElement(String element) {
            LOGGER.log(Level.INFO, "Execution of " + element + " started");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LOGGER.log(Level.INFO, "Execution of " + element + " ended");
            return element;
        }

    }

}
