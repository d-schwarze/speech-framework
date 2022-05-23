package de.speech.core.dispatcher.implementation.exception;

/**
 * Thrown if worker don't have the preprocesses.
 */
public class PreProcessesMissingException extends Exception {

    public PreProcessesMissingException(String message) {
        super(message);
    }
}
