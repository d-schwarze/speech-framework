package de.speech.core.dispatcher.implementation;

/**
 * Worker status.
 */
public enum WorkerStatus {

    /**
     * The worker has not been initialized.
     */
    NOT_INITIALIZED,

    /**
     * The queue of the worker is full.
     */
    QUEUE_FULL,

    /**
     * The queue of the worker is not full.
     */
    QUEUE_NOT_FULL,

    /**
     * The worker has a connection problem.
     */
    CONNECTION_FAILED,

    /**
     * The worker stopped.
     */
    WORKER_STOPPED
}
