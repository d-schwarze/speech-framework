package de.speech.core.dispatcher.implementation.exception;

/**
 * Thrown if the worker could not parse the request.
 */
public class WorkerParseException extends Exception {

    public WorkerParseException(String message) {
        super(message);
    }
}
