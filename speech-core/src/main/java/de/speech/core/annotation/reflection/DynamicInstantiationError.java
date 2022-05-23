package de.speech.core.annotation.reflection;

public class DynamicInstantiationError extends Exception {

    public DynamicInstantiationError(String msg) {
        super(msg);
    }

    public DynamicInstantiationError(Exception ex) {
        super(ex);
    }
}
