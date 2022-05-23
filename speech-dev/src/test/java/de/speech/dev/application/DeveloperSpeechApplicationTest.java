package de.speech.dev.application;

import de.speech.core.task.ITask;
import de.speech.core.task.result.implementation.FinalAudioRequestResultWithTac;
import de.speech.core.task.result.implementation.FinalTaskResultWithTac;
import de.speech.dev.builder.taskbuilder.implementation.DeveloperTaskBuilderWithPath;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeveloperSpeechApplicationTest {



    @Test
    public final void testDeveloperSpeechApplication() throws ExecutionException, InterruptedException, TimeoutException {
        DeveloperTaskBuilderWithPath builder = new DeveloperTaskBuilderWithPath(DeveloperSpeechApplication.class.getResource("/AudioFiles").getPath(), DeveloperSpeechApplication.class.getResource("/ActualTexts/requestTexts.txt").getPath());
        builder.addFrameworkConfiguration("Framework0", "Model0");
        ITask task = builder.buildTask();

        DeveloperSpeechApplicationMock mock = new DeveloperSpeechApplicationMock(new ArrayList<>());
        mock.start();
        Future<FinalTaskResultWithTac> f = mock.runTaskWithFutureAndTac(task);

        FinalTaskResultWithTac resultWithTac =  f.get(5000, TimeUnit.MILLISECONDS);
        System.out.println(resultWithTac.getResults().get(0).getResults().size());

        assertEquals(task.getAudioRequests().size(), resultWithTac.getResults().size());
        for (FinalAudioRequestResultWithTac result : resultWithTac.getResults()) {
            assertEquals(String.format("%d_%d_%s_%s",
                                       task.getTaskID(),
                                       result.getRequest().getRequestId(),
                                       task.getFrameworkConfigurations().get(0).getFramework().getIdentifier(),
                                       task.getFrameworkConfigurations().get(0).getFramework().getModel()),
                        result.getResults().get(0).getRecognizedSentence());
        }
    }

}
