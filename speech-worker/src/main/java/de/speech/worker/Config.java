package de.speech.worker;

/**
 * The config of the worker, which is loaded on startup
 */
public class Config {

    private int port = 3000;
    private int maxQueueSize = 30;
    private String preProcessDir = "preprocesses";
    private String serviceJar = "service.jar";

    /**
     * Creates a config with the specified values
     *
     * @param port          the port the worker should use
     * @param maxQueueSize  the maximum size the queue may have
     * @param preProcessDir the directory where preprocesses should be loaded from
     * @param serviceJar    the jar file where the {@linkplain de.fraunhofer.iosb.spinpro.speechtotext.ISpeechToTextService} implementation is in
     */
    public Config(int port, int maxQueueSize, String preProcessDir, String serviceJar) {
        this.port = port;
        this.maxQueueSize = maxQueueSize;
        this.preProcessDir = preProcessDir;
        this.serviceJar = serviceJar;
    }

    /**
     * Creates a default config with following values:
     * <ul>
     *     <li>port: 3000</li>
     *     <li>maxQueueSize: 30</li>
     *     <li>preProcessDir: preprocesses</li>
     *     <li>serviceJar: service.jar</li>
     * </ul>
     */
    public Config() {

    }

    /**
     * Returns the port the worker should use to start the server
     *
     * @return the port to use for the server
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the maximum queue size that should be used for the {@linkplain de.speech.worker.local.IWorkerServer}
     *
     * @return the maximum queue size
     */
    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    public String getPreProcessDir() {
        return preProcessDir;
    }

    public String getServiceJar() {
        return serviceJar;
    }
}
