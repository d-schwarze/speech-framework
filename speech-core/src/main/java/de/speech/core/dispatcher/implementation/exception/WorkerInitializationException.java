package de.speech.core.dispatcher.implementation.exception;

/**
 * Throws if the worker could not initialize.
 */
public class WorkerInitializationException extends Exception {

    public WorkerInitializationException(String message) {
        super(message);
    }
}
