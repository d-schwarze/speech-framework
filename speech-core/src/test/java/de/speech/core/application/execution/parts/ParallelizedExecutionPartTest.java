package de.speech.core.application.execution.parts;

import de.speech.core.application.execution.ExecutionPart;
import de.speech.core.application.execution.element.ExecutionElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParallelizedExecutionPartTest {

    private int counter;

    private ParallelizedExecutionPart<String, String> parallelizedExecutionPart;

    private final static Logger LOGGER = Logger.getLogger(ParallelizedExecutionPartTest.class.getName());

    private Collection<String> results;

    private Collection<String> actual;

    @BeforeEach
    public final void initializeTest() {
        actual = new HashSet<>();
        actual.add("input1");
        actual.add("input2");
        actual.add("input3");
        actual.add("input4");

        results = new HashSet<>();
    }

    @Test
    public final void testFullCache() {

        Object waitObject = new Object();

        parallelizedExecutionPart = new ParallelizedExecutionPart<>("queueTest", 2, 2, null) {
            @Override
            public String executeElement(String element) {
                synchronized (waitObject) {
                    try {
                        waitObject.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };
        parallelizedExecutionPart.start();

        Iterator<String> it = actual.iterator();

        while (it.hasNext()) {
            parallelizedExecutionPart.addExecutionElement(new ExecutionElement(it.next()));
        }

        //2 in execution, 2 in queue
        assertTrue(parallelizedExecutionPart.isCacheFull());

        synchronized (waitObject) {
            waitObject.notifyAll();
        }
    }


    @Test
    public final void testParallelExecution() throws InterruptedException {
        counter = 4;

        Object waitObject = new Object();

        parallelizedExecutionPart = new ParallelizedExecutionPart<String, String>("parallelTestPart", 4, 4, null) {
            @Override
            public String executeElement(String element) {
                LOGGER.log(Level.INFO, "Execution of " + element + " started");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (waitObject) {
                    results.add(element);
                    counter--;
                    waitObject.notifyAll();
                }
                LOGGER.log(Level.INFO, "Execution of " + element + " ended");
                return element;
            }
        };
        parallelizedExecutionPart.start();

        Iterator<String> it = actual.iterator();

        while (it.hasNext()) {
            parallelizedExecutionPart.addExecutionElement(new ExecutionElement(it.next()));
        }



        synchronized (waitObject) {
            while (counter != 0) {
                waitObject.wait(5000);
            }

            assertEquals(0, counter);
        }

        assertEquals(actual, results);

    }

    @Test
    public final void testElementPassing() throws InterruptedException {
        Object waitObject = new Object();

        SequentialExecutionPart<String, String> endPart = new SequentialExecutionPart<String, String>("endTest", null) {
            @Override
            public String executeElement(String element) {
                results.add(element);

                if (results.size() == 4) {
                    synchronized (waitObject) {
                        waitObject.notifyAll();
                    }
                }

                return null;
            }
        };
        endPart.start();


        parallelizedExecutionPart = new ParallelizedExecutionPart<String, String>("parallelTestPart", 4, 4, endPart) {
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
        };
        parallelizedExecutionPart.start();

        Iterator<String> it = actual.iterator();

        while (it.hasNext()) {
            parallelizedExecutionPart.addExecutionElement(new ExecutionElement(it.next()));
        }



        synchronized (waitObject) {
            waitObject.wait(5000);

            assertEquals(4, results.size());
        }

        assertEquals(results, actual);

    }

    @Test
    public final void testThreeLinkedParts() throws InterruptedException {

        Object waitObject = new Object();

        SequentialExecutionPart<String, String> endPart = new SequentialExecutionPart<String, String>("endTest", null) {
            @Override
            public String executeElement(String element) {
                results.add(element);

                if (results.size() == 4) {
                    synchronized (waitObject) {
                        waitObject.notifyAll();
                    }
                }

                return null;
            }
        };
        ExecutionPart<String, String> pep3 = new ParallelizedExecutionPartMock(endPart, 4, "testPart3");
        ExecutionPart<String, String> pep2 = new ParallelizedExecutionPartMock(pep3, 4, "testPart2");
        ExecutionPart<String, String> pep1 = new ParallelizedExecutionPartMock(pep2, 4, "testPart1");
        endPart.start();
        pep1.start();
        pep2.start();
        pep3.start();

        Iterator<String> it = actual.iterator();

        while (it.hasNext()) {
            pep1.addExecutionElement(new ExecutionElement(it.next()));
        }



        synchronized (waitObject) {
            waitObject.wait(5000);

            assertEquals(4, results.size());
        }

        assertEquals(actual, results);

    }

    class ParallelizedExecutionPartMock extends ParallelizedExecutionPart<String, String> {

        public ParallelizedExecutionPartMock(ExecutionPart<String, ?> next, int queueSize, String identifier) {
            super(identifier, queueSize, 4, next);
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
