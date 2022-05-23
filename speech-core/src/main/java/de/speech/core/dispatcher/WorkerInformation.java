package de.speech.core.dispatcher;

/**
 * Object used for information transfer between worker and core.
 */
public class WorkerInformation {

    private final String name;
    private final String model;
    private final int maxQueueSize;
    private final int currentQueueSize;

    public WorkerInformation(String name, String model, int maxQueueSize, int currentQueueSize) {
        this.name = name;
        this.model = model;
        this.maxQueueSize = maxQueueSize;
        this.currentQueueSize = currentQueueSize;
    }

    /**
     * A getter for the name.
     *
     * @return name of the framework
     */
    public String getFrameworkName() {
        return name;
    }

    /**
     * A getter fot the model.
     *
     * @return model loaded on the worker
     */
    public String getModel() {
        return model;
    }

    /**
     * A getter for the maximum queue size.
     *
     * @return the maximum queue size
     */
    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    /**
     * A getter for the current queue size.
     *
     * @return the current queue size
     */
    public int getCurrentQueueSize() {
        return currentQueueSize;
    }
}
