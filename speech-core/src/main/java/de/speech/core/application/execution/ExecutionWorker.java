package de.speech.core.application.execution;

/**
 * Class for executing phase logic parallel. A ExecutionWorker is part of an {@linkplain ExecutionPart}. Depending
 * of the {@linkplain ExecutionPart execution part implementation}, many works can be added to an execution part
 * to handle multiple elements parallel. One key advantage is, to run the execution phase parallel to the real
 * speech application. This leads speech application that can react to new incoming elements and has not to wait
 * for elements to be executed.
 *
 * @param <E> input element type
 * @param <N> output element type
 */
public abstract class ExecutionWorker<E, N> implements Runnable {

    /**
     * Parent execution part which holds on to the executable elements and global logic for executing them.
     */
    private ExecutionPart<E, N> parent;

    /**
     * Unique identifier inside the parent execution part
     */
    private int identifier;

    public ExecutionWorker(ExecutionPart<E, N> parent, int identifier) {
        this.parent = parent;
        this.identifier = identifier;
    }

    @Override
    public void run() {
        runWorker();
    }

    /**
     * Logic that should be run by the worker.
     *
     * @see DefaultExecutionWorker
     */
    public abstract void runWorker();

    /**
     * Getter for the parent execution part
     * @return {@linkplain #parent}
     */
    public ExecutionPart<E, N> getParent() {
        return parent;
    }

    /**
     * Getter for the identifier
     * @return {@linkplain #identifier}
     */
    public int getIdentifier() {
        return this.identifier;
    }
}
