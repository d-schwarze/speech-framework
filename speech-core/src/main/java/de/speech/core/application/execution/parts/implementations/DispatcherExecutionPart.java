package de.speech.core.application.execution.parts.implementations;

import de.speech.core.application.execution.ExecutionPart;
import de.speech.core.application.execution.RuntimeUtil;
import de.speech.core.application.execution.parts.ParallelizedExecutionPart;
import de.speech.core.dispatcher.implementation.AbstractDispatcher;
import de.speech.core.dispatcher.implementation.exception.*;
import de.speech.core.logging.Loggable;
import de.speech.core.task.ITask;
import de.speech.core.task.result.IAudioRequestResult;
import de.speech.core.task.result.ITaskResult;

import java.util.concurrent.ExecutionException;

/**
 * CustomExecutionPart that wraps the dispatcher-phase to be accessed in the execution pipeline.
 */
public class DispatcherExecutionPart extends ParallelizedExecutionPart<ITask, ITaskResult<IAudioRequestResult>> implements Loggable {

    private AbstractDispatcher dispatcher;

    private static final String DISPATCHER_PART_IDENTIFIER = "dispatcherPart";

    private static int DEFAULT_QUEUE_SIZE = 10;

    private static int MIN_NUMBER_OF_EXECUTORS = 8;

    private static int DEFAULT_NUMBER_OF_EXECUTORS = Math.max(RuntimeUtil.getAvailableProcessors(), MIN_NUMBER_OF_EXECUTORS);

    public DispatcherExecutionPart(AbstractDispatcher dispatcher) {
        this(dispatcher, DISPATCHER_PART_IDENTIFIER, DEFAULT_QUEUE_SIZE, DEFAULT_NUMBER_OF_EXECUTORS, null);
    }

    public DispatcherExecutionPart(AbstractDispatcher dispatcher, int numberOfExecutors) {
        this(dispatcher, DISPATCHER_PART_IDENTIFIER, DEFAULT_QUEUE_SIZE, numberOfExecutors, null);
    }

    public DispatcherExecutionPart(AbstractDispatcher dispatcher, String identifier, int queueSize, int numberOfExecutors, ExecutionPart<ITaskResult<IAudioRequestResult>, ?> next) {
        super(identifier, queueSize, numberOfExecutors, next);

        if (dispatcher == null) throw new IllegalArgumentException("Given dispatcher cannot be null.");

        this.dispatcher = dispatcher;
    }

    /**
     * Executes the dispatcher logic.
     *
     * @param element element that should be in the dispatcher
     * @return task result provided by the dispatcher-phase
     */
    @Override
    public ITaskResult<IAudioRequestResult> executeElement(ITask element) throws Exception {
        ITaskResult<IAudioRequestResult> result = null;
        try {
            result = dispatcher.dispatchTask(element).get();
        } catch (ExecutionException e) {
            if (NoWorkerException.class.isInstance(e.getCause())) {
                throw ((NoWorkerException) e.getCause());
            } else if (PreProcessesMissingException.class.isInstance(e.getCause())) {
                throw ((PreProcessesMissingException) e.getCause());
            } else if (QueueFullException.class.isInstance(e.getCause())) {
                throw ((QueueFullException) e.getCause());
            } else if (UnknownStatusCodeException.class.isInstance(e.getCause())) {
                throw ((UnknownStatusCodeException) e.getCause());
            } else if (WorkerInitializationException.class.isInstance(e.getCause())) {
                throw ((WorkerInitializationException) e.getCause());
            } else if (WorkerParseException.class.isInstance(e.getCause())) {
                throw ((WorkerParseException) e.getCause());
            } else {
                throw e;
            }
        }

        return result;
    }
}
