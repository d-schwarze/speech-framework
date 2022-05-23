package de.speech.test.generator;

import de.speech.core.framework.FrameworkManager;
import de.speech.core.framework.IFramework;
import de.speech.core.postprocessing.IPostProcessFactory;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.IFrameworkConfiguration;
import de.speech.core.task.ITask;
import de.speech.core.task.implementation.FrameworkConfiguration;
import de.speech.core.task.implementation.Task;
import de.speech.core.task.implementation.audioRequest.AudioRequestWithPath;
import de.speech.test.expected.ExpectedResult;

import java.util.ArrayList;
import java.util.List;

public class Generator {

    List<IPostProcessFactory> factories;

    List<IAudioRequest> requests;

    List<ITask> tasks;

    List<IFrameworkConfiguration> frameworkConfigurations;

    public Generator(List<ExpectedResult> expectedResults, int numberOfPostProcessFactories, int numberOfTasks, int numberOfFrameworks, int numberOfModelsPerFramework, List<String> preProcesses) {
        this.factories = generatePostProcessFactories(numberOfPostProcessFactories);
        this.requests = generateAudioRequests(expectedResults);
        this.frameworkConfigurations = generateFrameworkConfigurations(numberOfFrameworks, numberOfModelsPerFramework, preProcesses);
        this.tasks = generateTasks(numberOfTasks, this.requests, this.frameworkConfigurations, new ArrayList<>());
    }

    public List<IPostProcessFactory> getFactories() {
        return factories;
    }

    public List<IAudioRequest> getRequests() {
        return requests;
    }

    public List<ITask> getTasks() {
        return tasks;
    }

    public List<IFrameworkConfiguration> getFrameworkConfigurations() {
        return frameworkConfigurations;
    }

    public static List<IPostProcessFactory> generatePostProcessFactories(int quantity) {
        List<IPostProcessFactory> factories = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            factories.add(() -> new PostProcessMock());
        }

        return factories;
    }

    public static List<IAudioRequest> generateAudioRequests(List<ExpectedResult> expectedResults) {
        List<IAudioRequest> requests = new ArrayList<>();
        for (ExpectedResult er : expectedResults) {
            requests.add(generateAudioRequest(er));
        }

        return requests;
    }

    public static IAudioRequest generateAudioRequest(ExpectedResult er) {
        return new AudioRequestWithPath(er.getId(), er.getPath());
    }

    public static List<IFrameworkConfiguration> generateFrameworkConfigurations(int numberOfFrameworks, int numberOfModelsPerFramework, List<String> preProcesses) {
        List<IFrameworkConfiguration> frameworkConfigurations = new ArrayList<>();

        for (int i = 0; i < numberOfFrameworks; i++) {
            for (int l = 0; l < numberOfModelsPerFramework; l++) {
                IFramework framework = FrameworkManager.getInstance().findFramework(String.format("framework%d", i), String.format("model%d", l));
                frameworkConfigurations.add(new FrameworkConfiguration(framework, preProcesses));
            }
        }

        return frameworkConfigurations;
    }

    public static List<ITask> generateTasks(int quantity, List<IAudioRequest> requests, List<IFrameworkConfiguration> frameworkConfigurations, List<IPostProcessFactory> postProcessFactories) {
        assert(requests.size() >= quantity);

        int size = requests.size() / quantity;

        List<ITask> tasks = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            List<IAudioRequest> taskRequests = new ArrayList<>();
            ITask task = new Task(i, frameworkConfigurations, postProcessFactories, taskRequests);
            for (int l = i * size; l < requests.size() && l < (i + 1) * size; l++) {
                taskRequests.add(requests.get(l));
            }

            if (i == quantity - 1) {
                for (int l = (i + 1) * size; l < requests.size(); l++) {
                    taskRequests.add(requests.get(l));
                }
            }

            tasks.add(task);
        }

        return tasks;
    }
}
