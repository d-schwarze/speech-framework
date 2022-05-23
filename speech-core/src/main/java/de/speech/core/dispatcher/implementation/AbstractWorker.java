package de.speech.core.dispatcher.implementation;

import de.speech.core.application.configuration.WorkerConfiguration;
import de.speech.core.dispatcher.*;
import de.speech.core.dispatcher.errorhandler.IWorkerResultTimeoutErrorHandler;
import de.speech.core.dispatcher.errorhandler.implementation.DefaultWorkerResultTimeoutExceptionHandler;
import de.speech.core.framework.FrameworkManager;
import de.speech.core.framework.IFramework;
import de.speech.core.logging.SpeechLogging;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractWorker implements IWorkerCore {

    protected final Logger logger = SpeechLogging.getLogger();
    /**
     * contains all times, when the request was send.
     */
    protected final List<Long> requestTimes = Collections.synchronizedList(new LinkedList<>());
    /**
     * contains all requests.
     */
    protected final List<ICompletableFrameworkAudioRequest> requests = Collections.synchronizedList(new LinkedList<>());
    protected final WorkerConfiguration config;
    private final List<IStatusChangeListener> changeStatusListeners = Collections.synchronizedList(new LinkedList<>());
    /**
     * The timeout between the request and the result.
     */
    protected long resultTimeout;
    protected WorkerStatus status = WorkerStatus.NOT_INITIALIZED;
    protected int maxQueueSize = -1;
    protected int currentQueueSize = 0;
    protected IFramework framework;
    protected IWorkerAudioRequestSource source;
    protected IWorkerResultTimeoutErrorHandler resultTimeoutErrorHandler = new DefaultWorkerResultTimeoutExceptionHandler();

    /**
     * Creates a new worker
     *
     * @param config        configuration of the worker
     * @param resultTimeout maximum time between request and result
     */
    public AbstractWorker(WorkerConfiguration config, long resultTimeout) {
        this.config = config;
        this.resultTimeout = resultTimeout;
    }

    @Override
    public synchronized void setRequestSource(IWorkerAudioRequestSource source) {
        this.source = source;
        notifyAll();
    }

    @Override
    public int getQueuePlacesFree() {
        return maxQueueSize - currentQueueSize;
    }

    @Override
    public int getQueueItemsAmount() {
        return currentQueueSize;
    }

    /**
     * A getter for the number of requests, that are not overdue.
     * @return number of requests what are not overdue.
     */
    public int getNotTimedOutItemAmount() {
        return requests.size();
    }

    /**
     * Wait til the worker has send its framework or the worker stopped.
     *
     * @return framework of the worker
     */
    @Override
    public synchronized IFramework getFramework() {
        while (framework == null && status != WorkerStatus.WORKER_STOPPED) {
            try {
                wait();
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "thread interrupted", e);
            }
        }
        return framework;
    }

    /**
     * A getter for the status of the worker.
     *
     * @return current status of the worker.
     */
    public WorkerStatus getStatus() {
        return status;
    }

    @Override
    public WorkerConfiguration getConfiguration() {
        return config;
    }

    /**
     * A setter for the resultTimeoutHandler.
     *
     * @param handler handler
     */
    public void setResultTimeoutErrorHandler(IWorkerResultTimeoutErrorHandler handler) {
        this.resultTimeoutErrorHandler = handler;
    }

    /**
     * A setter for the resultTimeout.
     *
     * @param timeout maximum time
     */
    public void setResultTimeout(long timeout) {
        this.resultTimeout = timeout;
    }

    @Override
    public boolean isReady() {
        return source != null && (status == WorkerStatus.QUEUE_NOT_FULL);
    }

    /**
     * send a status request. Updates the status of the worker.
     */
    public abstract void statusRequest();

    @Override
    public synchronized void stop() throws Exception {
        if (status != WorkerStatus.WORKER_STOPPED) {
            status = WorkerStatus.WORKER_STOPPED;
            notifyAll();
            notifyStatusChangeListeners();
            completeLeftResults();
        }
    }

    /**
     * Completes all left results with an exception.
     */
    private void completeLeftResults() {
        for (ICompletableFrameworkAudioRequest request : requests) {
            request.getCompletableFuture().completeExceptionally(new Exception("worker stopped"));
        }

        requests.clear();
        requestTimes.clear();
    }

    /**
     * Changes the status of the worker.
     *
     * @param information information
     */
    protected void changeStatus(WorkerInformation information) {
        assert (information != null);
        if (framework == null) {
            framework = FrameworkManager.getInstance().findFramework(information.getFrameworkName(),
                    information.getModel());
        }

        this.maxQueueSize = information.getMaxQueueSize();

        if (maxQueueSize < 1) {
            logger.log(Level.WARNING, "worker at " + config.getLocation() + " has 0 queueSize");
        }

        changeStatus();
    }

    /**
     * Changes the status of the worker.
     */
    protected synchronized void changeStatus() {
        if (status != WorkerStatus.WORKER_STOPPED
                && getQueuePlacesFree() == 0) {
            if (status != WorkerStatus.QUEUE_FULL) notifyStatusChangeListeners();
            status = WorkerStatus.QUEUE_FULL;
        } else {
            if (status != WorkerStatus.QUEUE_NOT_FULL) notifyStatusChangeListeners();
            status = WorkerStatus.QUEUE_NOT_FULL;
        }

        notifyAll();
    }

    /**
     * Waits for the next result or the next timeout of the result. If the queue is empty it returns.
     */
    public synchronized void waitForNextResultOrTimeout() throws InterruptedException {
        while (!isReady()) {
            if (requests.size() == 0) {
                return;
            }
            long timePassed = System.currentTimeMillis() - requestTimes.get(0);

            long timeout = resultTimeout - timePassed;
            if (timeout > 0) {
                wait(timeout);
            }

            checkTimeouts();
        }

    }

    /**
     * Checks if the first result is overdue.
     */
    public synchronized void checkTimeouts() {
        if (requestTimes.size() < 1) return;
        long time = System.currentTimeMillis() - requestTimes.get(0);
        if (time >= resultTimeout) {
            resultTimeoutErrorHandler.handleTimeoutError(requests.get(0), this);
            requests.remove(0);
            requestTimes.remove(0);
        }
    }

    /**
     * Handles the next result. If the result is not in the queue it doing nothing(e.g. timed out).
     *
     * @param result result.
     */
    public synchronized void handleNextResult(IFrameworkResult result) {
        currentQueueSize--;
        //checks all requests in the queue if they are the right one
        for (int i = 0; i < requests.size(); i++) {
            ICompletableFrameworkAudioRequest future = requests.get(i);

            if (future.getWorkerAudioRequest().getId() == result.getRequestId()) {
                future.getCompletableFuture().complete(result);
                requests.remove(i);
                requestTimes.remove(i);
                changeStatus();
                return;
            }
        }
        notifyAll();
    }

    /**
     * Return the time til the next requestResult times out. Returns the max value of long,
     * if the worker has 0 requests in progress.
     *
     * @return time
     */
    public synchronized long nextWaitingTime() {
        if (requestTimes.size() > 0) {
            long time = resultTimeout - (System.currentTimeMillis() - requestTimes.get(0));
            return time >= 0 ? time : 0;
        } else {
            return Long.MAX_VALUE;
        }
    }

    /**
     * Waits til the worker has a request_source.
     */
    public synchronized void waitForRequestSource() throws InterruptedException {
        while (source == null && status != WorkerStatus.WORKER_STOPPED) {
            wait();
        }
    }

    /**
     * Notifies all statusChangeListeners.
     */
    protected void notifyStatusChangeListeners() {
        for (int i = changeStatusListeners.size() - 1; i >= 0; i--) {
            changeStatusListeners.get(i).statusChanged(this);
        }
    }

    @Override
    public void addStatusChangeListener(IStatusChangeListener listener) {
        this.changeStatusListeners.add(listener);
    }

    @Override
    public void removeStatusChangeListener(IStatusChangeListener listener) {
        this.changeStatusListeners.remove(listener);
    }
}
