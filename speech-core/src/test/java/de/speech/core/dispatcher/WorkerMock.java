package de.speech.core.dispatcher;

import de.speech.core.application.configuration.WorkerConfiguration;
import de.speech.core.dispatcher.implementation.WorkerStatus;
import de.speech.core.framework.FrameworkManager;
import de.speech.core.framework.IFramework;

public class WorkerMock implements IWorkerCore {

    @Override
    public void initialize() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void setRequestSource(IWorkerAudioRequestSource source) {

    }

    @Override
    public void sendRequest() {

    }

    @Override
    public int getQueuePlacesFree() {
        return 0;
    }

    @Override
    public int getQueueItemsAmount() {
        return 0;
    }

    @Override
    public IFramework getFramework() {
        return FrameworkManager.getInstance().findFramework("framework_0", "");
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public WorkerStatus getStatus() {
        return WorkerStatus.QUEUE_NOT_FULL;
    }

    @Override
    public WorkerConfiguration getConfiguration() {
        return null;
    }

    @Override
    public void addStatusChangeListener(IStatusChangeListener listener) {

    }

    @Override
    public void removeStatusChangeListener(IStatusChangeListener listener) {

    }
}
