package de.speech.core.task.result.implementation;

import java.util.List;

public class RequestResultStatus {

    List<Throwable> errors;
    ResultStatus[] status;

    public RequestResultStatus(ResultStatus[] status, List<Throwable> errors) {
        this.status = status;
        this.errors = errors;
    }

    public List<Throwable> getErrors() {
        return errors;
    }

    public ResultStatus[] getStatus() {
        return status;
    }
}
