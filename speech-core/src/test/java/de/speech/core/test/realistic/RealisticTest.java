package de.speech.core.test.realistic;

import de.speech.core.application.SpeechApplication;
import de.speech.core.dispatcher.implementation.AbstractDispatcher;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.ITask;
import de.speech.core.task.result.ITaskResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;
import de.speech.core.test.expected.ExpectedResult;
import de.speech.core.test.expected.ExpectedResultFinder;
import de.speech.core.test.generator.Generator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class RealisticTest {

    private static SpeechApplication application;

    private static List<ExpectedResult> expectedResults;

    private static List<IAudioRequest> audioRequests;

    private static List<ITask> tasks;

    private static Generator generator;

    @BeforeAll
    public static void initializeTestSuit() throws FileNotFoundException {
        application = new SpeechApplicationMock();
        application.start();

        ExpectedResultFinder finder = new ExpectedResultFinder();
        expectedResults = finder.getExpectedResults();

        generator = new Generator(expectedResults, 4, 2, 2, 2, new ArrayList<>());
        tasks = generator.getTasks();
        audioRequests = generator.getRequests();
    }


    @Test
    public final void test() throws InterruptedException, ExecutionException, TimeoutException {

        List<Future<ITaskResult<FinalAudioRequestResult>>> results = new ArrayList<>();

        for (ITask task : tasks) {
            Future<ITaskResult<FinalAudioRequestResult>> result = application.runTaskWithFuture(task);
            results.add(result);
        }

        for (Future<ITaskResult<FinalAudioRequestResult>> f : results) {
            ITaskResult<FinalAudioRequestResult> taskResult = f.get(5000, TimeUnit.MILLISECONDS);

            assertCorrect(taskResult, expectedResults);
        }

    }

    private void assertCorrect(ITaskResult<FinalAudioRequestResult> taskResult, List<ExpectedResult> expectedResults) {

        for (FinalAudioRequestResult r : taskResult.getResults()) {
            boolean found = false;
            for (ExpectedResult er : expectedResults) {
                if (er.getId() == r.getRequest().getRequestId()) {
                    if (r.getResults().get(0).getRecognizedSentence().equals(er.getActual())) {
                        found = true;
                        break;
                    } else {
                        fail("Wrong recognized text");
                    }
                }

            }

            assertTrue(found);
        }

        assertTrue(true);



    }

    static class SpeechApplicationMock extends SpeechApplication {
        @Override
        protected AbstractDispatcher createDispatcher() {
            try {
                return new DispatcherMock();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            throw new NullPointerException();
        }
    }

}
