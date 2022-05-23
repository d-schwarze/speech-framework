package de.speech.core.dispatcher;

import de.speech.core.framework.FrameworkManager;
import de.speech.core.framework.IFramework;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.IFrameworkConfiguration;
import de.speech.core.task.ITask;
import de.speech.core.task.implementation.FrameworkConfiguration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TaskMockFactory {

    private final static String TEST_AUDIO_FILE = "/audio.wav";

    public ITask createTask(int frameworks, int requestAmount) {
        List<IFrameworkConfiguration> configs = new ArrayList<>();
        List<IAudioRequest> requests = new ArrayList<>();

        for (int i = 0; i < frameworks; i++) {
            IFramework f = FrameworkManager.getInstance().findFramework("framework" + i, "model0");
            configs.add(new FrameworkConfiguration(f, new LinkedList<>()));
        }

        for (int i = 0; i < requestAmount; i++) {
            requests.add(new AudioRequestMock(TEST_AUDIO_FILE, i));
        }

        return new TaskMock(0, configs, requests);
    }
}
