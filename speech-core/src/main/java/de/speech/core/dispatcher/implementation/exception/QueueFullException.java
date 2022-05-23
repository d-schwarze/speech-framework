package de.speech.core.dispatcher.implementation.exception;

/**
 * Thrown if the queue of the worker is full.
 */
public class QueueFullException extends Exception {

    public QueueFullException(String message) {
        super(message);
    }
}
