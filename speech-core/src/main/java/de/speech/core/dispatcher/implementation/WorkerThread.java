package de.speech.core.dispatcher.implementation;

import de.speech.core.dispatcher.IStatusChangeListener;
import de.speech.core.dispatcher.IWorkerCore;
import de.speech.core.logging.SpeechLogging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread responsible for one worker.
 */
public class WorkerThread extends Thread implements IStatusChangeListener {

    private static final long WAITING_STATUS_REQUEST = 2000;
    private final AbstractWorker worker;
    private final Logger logger = SpeechLogging.getLogger();

    /**
     * Creates a new WorkerThread.
     *
     * @param worker worker
     */
    public WorkerThread(AbstractWorker worker) {
        this.worker = worker;
        worker.addStatusChangeListener(this);
    }

    /**
     * Initialize worker and then send continuously requests. Runs til interrupted.
     */
    @Override
    public void run() {
        assert (worker.getStatus() == WorkerStatus.NOT_INITIALIZED);
        worker.initialize();

        try {
            worker.waitForRequestSource();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        while (!Thread.interrupted()) {
            if (worker.getStatus() == WorkerStatus.CONNECTION_FAILED) {
                try {
                    worker.stop();
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "worker could not stopped", e);
                }
            } else if (worker.isReady()) {
                worker.sendRequest();
                worker.checkTimeouts();
            } else {
                try {
                    if (worker.getNotTimedOutItemAmount() == 0) {
                        Thread.sleep(WAITING_STATUS_REQUEST);
                        worker.statusRequest();
                    }
                    worker.waitForNextResultOrTimeout();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public void statusChanged(IWorkerCore worker) {
        if (worker.getStatus() == WorkerStatus.WORKER_STOPPED) {
            this.interrupt();
        }
    }
}
