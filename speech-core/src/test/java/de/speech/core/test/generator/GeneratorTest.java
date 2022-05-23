package de.speech.core.test.generator;

import de.speech.core.postprocessing.IPostProcessFactory;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.IFrameworkConfiguration;
import de.speech.core.task.ITask;
import de.speech.core.test.expected.ExpectedResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeneratorTest {

    private Generator generator;

    @Test
    public final void testGeneratePostProcessFactories() {
        List<IPostProcessFactory> postProcessFactoryList = Generator.generatePostProcessFactories(5);

        assertEquals(5, postProcessFactoryList.size());
    }

    @Test
    public final void testGenerateAudioRequest() {
        IAudioRequest audioRequest = Generator.generateAudioRequest(new ExpectedResult(1, "path", "acutal text"));

        assertEquals(1, audioRequest.getRequestId());
    }

    @Test
    public final void testGenerateFrameworkConfigurations() {
        List<IFrameworkConfiguration> frameworkConfigurations = Generator.generateFrameworkConfigurations(2, 2, new ArrayList<>());

        for (int i = 0; i < 2; i++) {
            IFrameworkConfiguration fc = frameworkConfigurations.get(i);
            assertEquals(String.format("Framework%d", 0), fc.getFramework().getIdentifier());
            assertEquals(String.format("Model%d", i), fc.getFramework().getModel());
        }

        for (int i = 2; i < 4; i++) {
            IFrameworkConfiguration fc = frameworkConfigurations.get(i);
            assertEquals(String.format("Framework%d", 1), fc.getFramework().getIdentifier());
            assertEquals(String.format("Model%d", i - 2), fc.getFramework().getModel());
        }
    }

    @Test
    public final void testGenerateTasks() {
        List<IAudioRequest> requests = Generator.generateAudioRequests(
                Arrays.asList(
                        new ExpectedResult(1, "test", "test"),
                        new ExpectedResult(2, "test", "test"),
                        new ExpectedResult(3, "test", "test"),
                        new ExpectedResult(4, "test", "test"),
                        new ExpectedResult(5, "test", "test")));

        List<IPostProcessFactory> factories = Generator.generatePostProcessFactories(3);
        List<IFrameworkConfiguration> frameworkConfigurations = Generator.generateFrameworkConfigurations(2, 2, new ArrayList<>());

        List<ITask> tasks = Generator.generateTasks(2, requests, frameworkConfigurations, factories);

        assertEquals(2, tasks.size());
        assertEquals(2, tasks.get(0).getAudioRequests().size());
        assertEquals(3, tasks.get(1).getAudioRequests().size());
    }

}
