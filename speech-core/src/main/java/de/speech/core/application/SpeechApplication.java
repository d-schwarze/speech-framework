package de.speech.core.application;

import de.speech.core.application.annotation.SpeechAnnotationSystem;
import de.speech.core.application.configuration.BasicSpeechConfiguration;
import de.speech.core.application.configuration.SpeechConfiguration;
import de.speech.core.application.configuration.WorkerConfiguration;
import de.speech.core.application.configuration.json.ConfigurationFileLoader;
import de.speech.core.application.execution.ExecutionPart;
import de.speech.core.application.execution.ExecutionSystem;
import de.speech.core.application.execution.parts.implementations.DispatcherExecutionPart;
import de.speech.core.application.execution.parts.implementations.PostProcessingExecutionPart;
import de.speech.core.dispatcher.implementation.AbstractDispatcher;
import de.speech.core.dispatcher.implementation.Dispatcher;
import de.speech.core.dispatcher.implementation.DispatcherConfig;
import de.speech.core.dispatcher.implementation.HttpDispatcherConfig;
import de.speech.core.task.ITask;
import de.speech.core.task.result.ITaskResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Main class for starting the speech framework. Application is used for adding the {@linkplain ITask tasks} to the
 * system. Also initialization work is done here.
 * The execution of an {@linkplain ITask} is handled by {@linkplain #executionSystem} and is run parallel to the
 * SpeechApplication, which means that {@linkplain ITask tasks} may be added at any time without blocking other tasks
 * (except if caches of some {@linkplain ExecutionPart parts} are full).
 */
public class SpeechApplication {

    /**
     * Main configuration for the speech framework.
     */
    private SpeechConfiguration configuration;

    /**
     * Main annotation system for the speech framework
     */
    private SpeechAnnotationSystem annotationSystem;

    /**
     * Execution system for executing {@linkplain ITask tasks}
     */
    protected ExecutionSystem<ITask> executionSystem;

    protected AbstractDispatcher dispatcher;

    /**
     * Run the speech framework
     */
    public void start() {
        this.onInitialize();
        this.onStart();
        this.afterStart();
    }

    /**
     * Initialize the speech framework
     */
    protected void onInitialize() {
        initializeAnnotationSystem();
        initializeConfiguration();
        initializeDispatcher();
        initializeExecutionSystem();
    }

    public void stop() throws DispatcherFailedStoppingException {
        this.executionSystem.stopSystem();

        try {
            this.dispatcher.stopAll();
        } catch (Exception e) {
            throw new DispatcherFailedStoppingException(e);
        }

        this.onStop();
    }

    public void onStop() {}


    /**
     * Initializes the {@linkplain #annotationSystem}.
     */
    private void initializeAnnotationSystem() {
        annotationSystem = createAnnotationSystem();
    }

    /**
     * Factory method for the used {@linkplain SpeechAnnotationSystem}.
     * <br>
     * Override this method to provide your custom {@linkplain SpeechAnnotationSystem}.
     * @return instance of {@linkplain SpeechAnnotationSystem}
     */
    protected SpeechAnnotationSystem createAnnotationSystem() {
        return new SpeechAnnotationSystem();
    }

    /**
     * Initializes the {@linkplain #configuration}.
     */
    private void initializeConfiguration() {
        configuration = loadConfiguration();
        if (configuration == null) {
            configuration = new BasicSpeechConfiguration();
        }
    }

    /**
     * Load the main configuration of the speech application.
     * Configuration can be either annotated and thus a class or if nothing was annotated
     * <br>
     * Override this method to provide your custom {@linkplain SpeechConfiguration}.
     * {@linkplain ConfigurationFileLoader#loadDefaultJsonConfiguration()} is used as default.
     */
    protected SpeechConfiguration loadConfiguration() {
        SpeechConfiguration configuration = annotationSystem.getSpeechConfiguration();

        if (configuration == null) {
            try {
                configuration = ConfigurationFileLoader.loadDefaultJsonConfiguration();
            } catch (IOException e) {
                return null;
            }
        }

        return configuration;
    }

    private void initializeDispatcher() {
        dispatcher = createDispatcher();
        if (dispatcher == null) throw new NullPointerException("Only non null dispatchers are valid.");
    }

    /**
     * Factory method for the used {@linkplain AbstractDispatcher}.
     * <br>
     * Override this method to provide your custom {@linkplain AbstractDispatcher}.
     * @return instance of {@linkplain SpeechAnnotationSystem}
     */
    protected AbstractDispatcher createDispatcher() {
        DispatcherConfig dispatcherConfig =
                new HttpDispatcherConfig(
                    configuration.getResultTimeout(),
                    configuration.getHttpTimeout(),
                    configuration.getPort(),
                    configuration.getAcceptors(),
                    configuration.getSelectors(),
                    configuration.getQueueSize()
        );

        AbstractDispatcher dispatcher = new Dispatcher(dispatcherConfig);
        dispatcher.initializeWorker(configuration.getWorkers().toArray(new WorkerConfiguration[0]));

        return dispatcher;
    }

    /**
     * Initializes the execution system.
     */
    private void initializeExecutionSystem() {
        this.executionSystem = createExecutionSystem(null);
    }

    /**
     * Creates the execution system.
     * <br>
     * Override this method to provide your custom {@linkplain ExecutionSystem}.
     * @param tasks tasks that should be put into queue on startup
     */
    protected ExecutionSystem<ITask> createExecutionSystem(List<ITask> tasks) {
        ExecutionSystem<ITask> executionSystem;
        if (tasks != null) {
            executionSystem = new ExecutionSystem<>(tasks, createExecutionParts());
        } else {
            executionSystem = new ExecutionSystem<>(createExecutionParts());
        }

        return executionSystem;
    }

    /**
     * Creates the {@linkplain ExecutionPart parts} for the {@linkplain #executionSystem}.
     * <br>
     * Override this method to provide your custom {@linkplain ExecutionPart parts}.
     */
    protected List<ExecutionPart> createExecutionParts() {

        List<ExecutionPart> parts = new ArrayList<>();
        parts.add(new DispatcherExecutionPart(dispatcher));
        parts.add(new PostProcessingExecutionPart());

        return parts;
    }

    /**
     * Logic for starting the speech framework
     */
    protected void onStart() {
        this.executionSystem.startSystem();
    }

    /**
     * Logic that should be invoked after {@linkplain #onStart()}.
     */
    protected void afterStart() { }

    /**
     * Runs a task on the speech framework
     * @param task task that should be run
     */
    public void runTask(ITask task) {
        executionSystem.executeElement(task);
    }

    /**
     * Runs a task on the speech framework. Returns a future to the final result.
     * <br>
     * In case of an exception during the execution phase, the future will raise an
     * {@linkplain java.util.concurrent.ExecutionException} which contains
     * as {@linkplain ExecutionException#getCause()} an
     * {@linkplain de.speech.core.application.execution.ExecutionErrorException} in case the exception really occurred
     * during execution of the element.
     *
     * @param task task that should be run
     * @return future to the executed result
     */
    public Future<ITaskResult<FinalAudioRequestResult>> runTaskWithFuture(ITask task) {
        return executionSystem.executeElement(task);
    }

    public SpeechConfiguration getConfiguration() {
        return this.configuration;
    }
}
