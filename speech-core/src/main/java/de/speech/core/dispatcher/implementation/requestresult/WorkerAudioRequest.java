package de.speech.core.dispatcher.implementation.requestresult;

import de.speech.core.dispatcher.IWorkerAudioRequest;
import de.speech.core.task.IAudioRequest;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class WorkerAudioRequest implements IWorkerAudioRequest {

    private static final AtomicLong NEXT_ID = new AtomicLong(0);
    private final long id;

    private final IAudioRequest request;
    private final List<String> preProcesses;

    public WorkerAudioRequest(IAudioRequest request, List<String> preprocesses) {
        this.request = request;
        this.preProcesses = preprocesses;
        id = NEXT_ID.getAndIncrement();
    }

    public WorkerAudioRequest(IAudioRequest request, List<String> preProcesses, long id) {
        this.request = request;
        this.preProcesses = preProcesses;
        this.id = id;
    }

    @Override
    public IAudioRequest getRequest() {
        return request;
    }

    @Override
    public List<String> getPreProcesses() {
        return preProcesses;
    }

    @Override
    public long getId() {
        return id;
    }
}
