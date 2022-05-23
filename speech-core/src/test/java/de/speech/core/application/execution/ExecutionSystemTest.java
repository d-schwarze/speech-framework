package de.speech.core.application.execution;

import de.speech.core.application.execution.parts.ParallelizedExecutionPart;
import de.speech.core.application.execution.parts.SequentialExecutionPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class ExecutionSystemTest {
    private final static Logger LOGGER = Logger.getLogger(ExecutionSystemTest.class.getName());

    private ExecutionSystem<String> executionSystem;

    private ExecutionPart<String, String> ep1;

    private ExecutionPart<String, String> ep2;

    private ExecutionPart<String, String> endPart;

    private Collection<String> results;

    private Collection<String> actual;

    private Object waitObject;

    private boolean stopped = false;

    @BeforeEach
    public final void initializeTestSuit() {
        ep1 = new ParallelizedExecutionPart<String, String>("testPart1", 4, 2, null) {
            @Override
            public String executeElement(String element) {
                LOGGER.log(Level.INFO, "Execution of " + element + " started at " + this.getIdentifier());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LOGGER.log(Level.INFO, "Execution of " + element + " ended");
                return element;
            }
        };
        ep2 = new ParallelizedExecutionPart<String, String>("testPart2", 4, 2, null) {
            @Override
            public String executeElement(String element) {
                LOGGER.log(Level.INFO, "Execution of " + element + " started at " + this.getIdentifier());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LOGGER.log(Level.INFO, "Execution of " + element + " ended");
                return element;
            }
        };
        executionSystem = new ExecutionSystem(ep1, ep2);

        endPart = new SequentialExecutionPart<String, String>("endTest", null) {
            @Override
            public String executeElement(String element) {
                LOGGER.log(Level.INFO, "Execution of " + element + " started at " + this.getIdentifier());
                results.add(element);

                if (results.size() == actual.size()) {
                    synchronized (waitObject) {
                        waitObject.notifyAll();
                    }
                }
                LOGGER.log(Level.INFO, "Execution of " + element + " ended");

                return element;
            }
        };

        assertEquals(ep1, executionSystem.getStartingExecutionPart());
        assertEquals(ep2, executionSystem.getEndingExecutionPart());
        assertEquals(ep1.getNext(), ep2);
        assertEquals(ep2.getNext(), executionSystem.getCallbackExecutionPart());
        assertNull(executionSystem.getStartupExecutionPart());
    }

    @Test
    public final void testSystemWithStartupElements() throws InterruptedException {

        waitObject = new Object();

        actual = new HashSet<>();
        actual.add("startup1");
        actual.add("startup2");

        results = new HashSet<>();

        this.executionSystem = new ExecutionSystem<>(actual, ep1, endPart);
        this.executionSystem.startSystem();


        synchronized (waitObject) {
            waitObject.wait(5000);

            assertEquals(actual.size(), results.size());
        }

        assertEquals(results, actual);
    }

    @Test
    public final void testAddExecutionPart() {

        this.executionSystem.addExecutionPart(endPart);

        assertEquals(this.executionSystem.getEndingExecutionPart(), endPart);
        assertEquals(endPart.getNext(), this.executionSystem.getCallbackExecutionPart());

    }

    @Test
    public final void testExecuteElement() throws InterruptedException {
        waitObject = new Object();

        actual = new HashSet<>();
        actual.add("startup1");
        actual.add("startup2");

        results = new HashSet<>();

        testAddExecutionPart();

        this.executionSystem.startSystem();

        Iterator<String> it = actual.iterator();

        while (it.hasNext()) {
            this.executionSystem.executeElement(it.next());
            LOGGER.log(Level.INFO, "add element");
        }

        synchronized (waitObject) {
            waitObject.wait(5000);

            assertEquals(actual.size(), results.size());
        }


        assertEquals(results, actual);

    }

    @Test
    public final void testExecuteElementWithFuture() throws InterruptedException, ExecutionException, TimeoutException {

        this.executionSystem.startSystem();

        String actual = "test";

        Future<String> future = this.executionSystem.executeElement(actual);

        String result = future.get(5000, TimeUnit.MILLISECONDS);

        assertEquals(actual, result);
    }

    @Test
    public final void testExecuteElementWithException() {
        this.executionSystem.addExecutionPart(new SequentialExecutionPart<String, String>("errorPart", null) {
            @Override
            public String executeElement(String element) throws Exception {
                System.out.println("wupup");
                throw new Exception("Error occurred during execution");
            }
        });

        this.executionSystem.startSystem();

        String ferrousElement = "Element";

        Future<String> future = this.executionSystem.executeElement(ferrousElement);

        try {
            String result = future.get(5000, TimeUnit.MILLISECONDS);

            fail("Exception should occur");
        } catch (ExecutionException e) {
            assertEquals(e.getCause().getClass().getName(), ExecutionErrorException.class.getName());
        } catch (TimeoutException e) {
            fail("TimeoutException should not occur");
        } catch (InterruptedException e) {
            fail("InterruptedException should not occur");
        }
    }

    @Test
    public final void testExecuteElements() throws InterruptedException {
        waitObject = new Object();

        this.executionSystem = new ExecutionSystem<>(actual, ep1, endPart);
        this.executionSystem.startSystem();

        actual = new HashSet<>();
        actual.add("startup1");
        actual.add("startup2");

        results = new HashSet<>();

        this.executionSystem.executeElements(new ArrayList<>(actual));

        synchronized (waitObject) {
            waitObject.wait(5000);

            assertEquals(actual.size(), results.size());
        }

        assertEquals(results, actual);
    }


    @RepeatedTest(3)
    public final void testStopSystem() throws InterruptedException {

        stopped = false;
        this.executionSystem = new ExecutionSystem<>(new SequentialExecutionPart<String, String>("part", null) {
            @Override
            public String executeElement(String element) throws Exception {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    stopped = true;
                }

                return element;
            }
        });

        this.executionSystem.startSystem();
        this.executionSystem.executeElement("Test");

        Thread.sleep(200);

        this.executionSystem.stopSystem();

        Thread.sleep(1000);

        assertTrue(stopped);

        Future<String> r = this.executionSystem.executeElement("Not executing part");
        try {
            r.get(2000, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            fail();
        } catch (TimeoutException e) {
            assertTrue(true);
            return;
        }

        fail();
    }

    @Test
    public final void testStopStartSystem() throws InterruptedException {

        this.executionSystem = new ExecutionSystem<>(new SequentialExecutionPart<String, String>("part", null) {
            @Override
            public String executeElement(String element) throws Exception {
                Thread.sleep(500);

                return element;
            }
        });

        this.executionSystem.startSystem();
        this.executionSystem.stopSystem();
        Future<String> l = this.executionSystem.executeElement("Test");

        try {
            l.get(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail();
        } catch (ExecutionException e) {
            fail();
        } catch (TimeoutException e) {

        }

        this.executionSystem.startSystem();

        Future<String> f = this.executionSystem.executeElement("Tutut");
        try {
            assertEquals("Tutut", f.get(2000, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            fail();
        } catch (ExecutionException e) {
            fail();
        } catch (TimeoutException e) {
            fail();
        }
    }


}
