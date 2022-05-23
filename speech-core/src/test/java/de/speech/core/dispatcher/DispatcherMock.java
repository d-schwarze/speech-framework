package de.speech.core.dispatcher;

import de.speech.core.application.configuration.WorkerConfiguration;
import de.speech.core.dispatcher.implementation.AbstractDispatcher;
import de.speech.core.framework.IFramework;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.IFrameworkConfiguration;
import de.speech.core.task.ITask;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ISpeechToTextServiceData;
import de.speech.core.task.result.ITaskResult;
import de.speech.core.task.result.implementation.AudioRequestResult;
import de.speech.core.task.result.implementation.TaskResult;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class DispatcherMock extends AbstractDispatcher {

    @Override
    public Future<ITaskResult<IAudioRequestResult>> dispatchTask(ITask task) {
        List<IAudioRequestResult> requestResultList = new LinkedList<>();

        for (IAudioRequest request : task.getAudioRequests()) {
            List<ISpeechToTextServiceData> results = new LinkedList<>();
            for (IFrameworkConfiguration config : task.getFrameworkConfigurations()) {
                IFramework framework = config.getFramework();
                String sentence = task.getTaskID() + "_" + request.getRequestId() + "_" + framework.getIdentifier() + "_" + framework.getModel();

                results.add(new SpeechToTextServiceDataMock(framework.getIdentifier(), framework.getModel(), sentence));
            }

            requestResultList.add(new AudioRequestResult(results, request, null));
        }

        ITaskResult<IAudioRequestResult> result = new TaskResult(task, requestResultList);
        CompletableFuture<ITaskResult<IAudioRequestResult>> future = new CompletableFuture<>();
        future.complete(result);
        return future;
    }

    @Override
    public void initializeWorker(WorkerConfiguration... workers) {

    }

    @Override
    public void stopWorker(WorkerConfiguration config) throws Exception {

    }

    @Override
    public void stopAll() throws Exception {

    }
}
