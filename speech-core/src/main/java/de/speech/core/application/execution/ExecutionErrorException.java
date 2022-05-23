package de.speech.core.application.execution;

import java.util.Map;

public class ExecutionErrorException extends Exception {

    private Map<String, Exception> occurredExceptions;

    public ExecutionErrorException(Map<String, Exception> occurredExceptions) {
        this.occurredExceptions = occurredExceptions;
    }

    public Map<String, Exception> getOccurredExceptions() {
        return occurredExceptions;
    }
}
