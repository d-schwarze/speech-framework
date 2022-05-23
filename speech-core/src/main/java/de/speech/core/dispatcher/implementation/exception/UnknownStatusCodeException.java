package de.speech.core.dispatcher.implementation.exception;

/**
 * Thrown if a httpRequest has an unknown status code.
 */
public class UnknownStatusCodeException extends Exception {

    public UnknownStatusCodeException(String message) {
        super(message);
    }
}
