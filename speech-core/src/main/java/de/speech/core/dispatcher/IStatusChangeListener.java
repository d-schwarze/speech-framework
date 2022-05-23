package de.speech.core.dispatcher;

/**
 * Notifies this listener if the status of a worker has changed.
 */
public interface IStatusChangeListener {

    /**
     * Listen for status changes.
     * @param worker worker with change
     */
    void statusChanged(IWorkerCore worker);
}
