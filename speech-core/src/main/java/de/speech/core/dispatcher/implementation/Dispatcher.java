package de.speech.core.dispatcher.implementation;

import de.speech.core.application.configuration.WorkerConfiguration;
import de.speech.core.dispatcher.*;
import de.speech.core.dispatcher.implementation.exception.NoWorkerException;
import de.speech.core.dispatcher.implementation.requestresult.WorkerAudioRequest;
import de.speech.core.dispatcher.linking.ITaskResultBuilder;
import de.speech.core.dispatcher.linking.implementation.TaskResultBuilder;
import de.speech.core.framework.IFramework;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.IFrameworkConfiguration;
import de.speech.core.task.ITask;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ITaskResult;

import java.util.*;
import java.util.concurrent.Future;

/**
 * Implementation of the dispatcher. Divides the {@linkplain IAudioRequest} into {@linkplain IWorkerAudioRequest}s
 * and pass them to {@linkplain IFrameWorkDispatcher}.
 */
public class Dispatcher extends AbstractDispatcher {

    /**
     * Maps the framework to the {@linkplain IFrameWorkDispatcher}
     */
    private final Map<IFramework, FrameWorkDispatcher> dispatcherMap = new HashMap<>();
    private DispatcherConfig config;

    /**
     * Creates a dispatcher with a default configuration.
     */
    public Dispatcher() {
        this.config = new DefaultDispatcherConfig();
    }

    /**
     * Creates a new dispatcher with the specified configuration.
     *
     * @param config configuration
     */
    public Dispatcher(DispatcherConfig config) {
        this.config = config;
    }

    /**
     * A setter for the configuration.
     *
     * @param config config
     */
    public void setConfig(DispatcherConfig config) {
        this.config = config;
    }

    /**
     * Initializes new workers with the {@linkplain IWorkerCoreFactory}. If a {@linkplain IFrameWorkDispatcher} dont exist,
     * it creates a new {@linkplain IFrameWorkDispatcher}.
     *
     * @param configs configs of added workers.
     */
    public synchronized void initializeWorker(WorkerConfiguration... configs) {
        assert (configs != null);
        assert (config.getWorkerCoreFactory() != null);
        IWorkerCoreFactory workerCoreFactory = config.getWorkerCoreFactory();

        List<IWorkerCore> newWorkers = new LinkedList<>();

        for (WorkerConfiguration config : configs) {
            newWorkers.add(workerCoreFactory.createWorkerCore(config));
        }

        linkWorkersToFrameworkDispatcher(newWorkers);
    }

    /**
     * Adds the worker to the associated frameworkDispatcher. If the frameworkDispatcher
     * not exist, a new one is added.
     *
     * @param workers workers to be initialized
     */
    private void linkWorkersToFrameworkDispatcher(List<IWorkerCore> workers) {
        for (IWorkerCore worker : workers) {
            IFramework framework = worker.getFramework();
            if (framework == null) continue;

            FrameWorkDispatcher dispatcher;
            if (dispatcherMap.containsKey(framework)) {
                dispatcher = dispatcherMap.get(framework);
            } else {
                dispatcher = new FrameWorkDispatcher(framework);
                dispatcherMap.put(framework, dispatcher);
            }

            dispatcher.addWorker(worker);
        }
    }

    /**
     * Dispatches one task.
     *
     * @param task task
     * @return Future with the taskResult.
     */
    @Override
    public synchronized Future<ITaskResult<IAudioRequestResult>> dispatchTask(ITask task) {
        assert (task != null);

        ITaskResultBuilder taskBuilder = new TaskResultBuilder(task);

        List<IFrameworkConfiguration> frameworksConfigurations = task.getFrameworkConfigurations();
        List<IFrameWorkDispatcher> frameworkDispatcher = new ArrayList<>();

        // get needed frameworkDispatcher
        for (IFrameworkConfiguration config : frameworksConfigurations) {
            FrameWorkDispatcher dispatcher = dispatcherMap.get(config.getFramework());
            if (dispatcher != null) {
                frameworkDispatcher.add(dispatcher);
            } else {
                taskBuilder.completeExceptionally(new NoWorkerException("no worker with framework"));
                return taskBuilder;
            }
        }

        for (IAudioRequest request : task.getAudioRequests()) {
            taskBuilder.addRequestResult(dispatchRequest(request, frameworkDispatcher, frameworksConfigurations), request);
        }

        return taskBuilder;
    }

    /**
     * Dispatch one request to all {@linkplain IFrameWorkDispatcher} specified.
     *
     * @param request                  the request
     * @param frameworkDispatcher      The frameworkDispatcher to dispatch.
     * @param frameworksConfigurations the configurations for the frameworks.
     * @return Future with results of the frameworks.
     */
    private Future<IFrameworkResult>[] dispatchRequest(IAudioRequest request, List<IFrameWorkDispatcher> frameworkDispatcher, List<IFrameworkConfiguration> frameworksConfigurations) {
        Future<IFrameworkResult>[] futures = new Future[frameworkDispatcher.size()];
        for (int i = 0; i < frameworkDispatcher.size(); i++) {
            List<String> preProcesses = frameworksConfigurations.get(i).getPreprocesses();

            IWorkerAudioRequest workerRequest = new WorkerAudioRequest(request, preProcesses);
            IFrameWorkDispatcher dispatcher = frameworkDispatcher.get(i);

            futures[i] = dispatcher.dispatchRequest(workerRequest);
        }


        return futures;
    }

    @Override
    public void stopWorker(WorkerConfiguration config) throws Exception {
        for (IFrameWorkDispatcher dispatcher : dispatcherMap.values()) {
            for (IWorkerCore worker : dispatcher.getWorkers()) {
                if (worker.getConfiguration().equals(config)) {
                    dispatcher.removeWorker(worker);
                    worker.stop();
                    return;
                }
            }
        }
    }

    @Override
    public void stopAll() throws Exception {
        for (IFrameWorkDispatcher dispatcher : dispatcherMap.values()) {
            for (IWorkerCore worker : dispatcher.getWorkers()) {
                worker.stop();
            }
        }

        for (IFrameWorkDispatcher dispatcher : dispatcherMap.values()) {
            dispatcher.getWorkers().clear();
        }
    }
}
