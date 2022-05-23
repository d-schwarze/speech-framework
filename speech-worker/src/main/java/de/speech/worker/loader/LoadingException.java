package de.speech.worker.loader;

public class LoadingException extends Exception {

    public LoadingException() {
        super("Error while loading files");
    }

    public LoadingException(String message) {
        super(message);
    }
}
