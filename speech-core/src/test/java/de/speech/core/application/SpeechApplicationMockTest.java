package de.speech.core.application;

import de.speech.core.application.execution.ExecutionPart;
import de.speech.core.application.execution.parts.ParallelizedExecutionPart;
import de.speech.core.application.execution.parts.ParallelizedExecutionPartTest;
import de.speech.core.application.execution.parts.SequentialExecutionPart;
import de.speech.core.postprocessing.IPostProcessFactory;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.IFrameworkConfiguration;
import de.speech.core.task.ITask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpeechApplicationMockTest {

    private SpeechApplicationMock application;

    private final static Logger LOGGER = Logger.getLogger(ParallelizedExecutionPartTest.class.getName());

    private Object waitObject;

    private Collection<String> actual;

    private Collection<String> results;


    @BeforeEach
    public final void initializeTest() {
        application = new SpeechApplicationMock();

        waitObject = new Object();
        actual = new HashSet<>();
        actual.add("test1");
        actual.add("test2");

        results = new HashSet<>();
    }

    @Test
    public void testSpeechApplication() throws InterruptedException {
        application.start();

        Iterator<String> it = actual.iterator();
        while (it.hasNext()) {
            application.runTask(new TaskMock(it.next()));
        }

        synchronized (waitObject) {
            waitObject.wait(5000);

            assertEquals(actual.size(), results.size());
        }

        assertEquals(results, actual);
    }

    @Test
    public void testTaskWithFuture() throws InterruptedException, ExecutionException, TimeoutException {
        application.start();

        Future<String> f = application.runTaskWithFutureToString(new TaskMock("futureTask"));

        String result = f.get(5000, TimeUnit.MILLISECONDS);

        assertEquals("futureTask", result);
    }

    class SpeechApplicationMock extends SpeechApplication {

        @Override
        protected List<ExecutionPart> createExecutionParts() {
            List<ExecutionPart> parts = new ArrayList<>();
            parts.add(new ParallelizedExecutionPart<ITask, String>("testPart1", 4, 2, null) {
                @Override
                public String executeElement(ITask element) {
                    LOGGER.log(Level.INFO, "Execution of " + element.toString() + " started at " + this.getIdentifier());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LOGGER.log(Level.INFO, "Execution of " + element.toString() + " ended at " + this.getIdentifier());
                    return element.toString();
                }
            });

            parts.add(new SequentialExecutionPart<String, String>("endPart", null) {
                @Override
                public String executeElement(String element) {
                    results.add(element);

                    if (results.size() == actual.size()) {
                        synchronized (waitObject) {
                            waitObject.notifyAll();
                        }
                    }

                    return element;
                }
            });

            return parts;
        }

        public Future<String> runTaskWithFutureToString(ITask task) {
            return executionSystem.executeElement(task);
        }

    }

    class TaskMock implements ITask {

        private String element;

        public TaskMock(String element) {
            this.element = element;
        }

        @Override
        public String toString() {
            return this.element;
        }

        @Override
        public List<IAudioRequest> getAudioRequests() {
            return null;
        }

        @Override
        public int getTaskID() {
            return 0;
        }

        @Override
        public List<IFrameworkConfiguration> getFrameworkConfigurations() {
            return null;
        }

        @Override
        public List<IPostProcessFactory> getPostProcessFactories() {
            return null;
        }

    }
}
