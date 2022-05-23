package de.speech.core.dispatcher.implementation.exception;

/**
 * Thrown if no workers are available for a specific framework.
 */
public class NoWorkerException extends Exception {

    public NoWorkerException(String msg) {
        super(msg);
    }
}
