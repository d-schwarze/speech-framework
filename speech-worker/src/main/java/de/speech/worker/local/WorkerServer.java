package de.speech.worker.local;

import de.fraunhofer.iosb.spinpro.speechtotext.ISpeechToTextService;
import de.speech.core.dispatcher.IWorkerAudioRequest;
import de.speech.worker.loader.LoadingException;
import de.speech.worker.local.preprocessing.IPreProcessor;
import de.speech.worker.local.preprocessing.PreProcessor;

import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * The main {@linkplain IWorkerServer} implementation
 */
public class WorkerServer implements IWorkerServer {

    private IResultHandler resultHandler;
    private final String name;
    private final BlockingQueue<IWorkerAudioRequest> queue;
    private final ISpeechToTextService speechToTextService;
    private final QueueWorker worker;
    private final int maxQueueSize;
    private final IPreProcessor preProcessor;

    /**
     * Creates a new {@linkplain WorkerServer} with the specified maxQueueSize and {@linkplain ISpeechToTextService}
     *
     * @param maxQueueSize        the maximum queue size to use
     * @param speechToTextService the {@linkplain ISpeechToTextService} to use
     */
    public WorkerServer(int maxQueueSize, ISpeechToTextService speechToTextService, String name, Path preprocessesPath) throws LoadingException {
        this.speechToTextService = speechToTextService;
        resultHandler = new NullObjectResultHandler();
        this.name = name;
        this.maxQueueSize = maxQueueSize;
        queue = new ArrayBlockingQueue<>(maxQueueSize);
        preProcessor = new PreProcessor(preprocessesPath);
        worker = new QueueWorker(queue, speechToTextService, resultHandler, preProcessor);
        worker.start();
    }

    @Override
    public void shutdown() throws InterruptedException {
        worker.interrupt();
        worker.join();
    }

    @Override
    public void setResultHandler(IResultHandler handler) {
        this.resultHandler = handler;
        worker.setHandler(this.resultHandler);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getModel() {
        return speechToTextService.getModelIdentifier();
    }

    @Override
    public void submitWork(IWorkerAudioRequest request) throws IllegalStateException {
        if (!preProcessor.areAllPrePreProcessesAvailable(request.getPreProcesses())) {
            throw new IllegalArgumentException("not all preprocesses are available");
        }
        queue.add(request);
    }

    @Override
    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    @Override
    public int getQueueSize() {
        return queue.size();
    }
}
