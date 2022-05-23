package de.speech.worker.local;

import de.fraunhofer.iosb.spinpro.exceptions.ServiceInitializingException;
import de.fraunhofer.iosb.spinpro.exceptions.UnsupportedAudioFormat;
import de.fraunhofer.iosb.spinpro.speechtotext.ISpeechToTextService;
import de.fraunhofer.iosb.spinpro.speechtotext.ISpeechToTextServiceMetadata;
import de.speech.core.dispatcher.IFrameworkResult;
import de.speech.core.dispatcher.IWorkerAudioRequest;
import de.speech.core.dispatcher.implementation.requestresult.FrameworkResult;
import de.speech.core.task.result.ISpeechToTextServiceData;
import de.speech.core.task.result.SpeechToTextServiceData;
import de.speech.worker.local.preprocessing.IPreProcessor;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Processes the {@linkplain IWorkerAudioRequest}s in the queue. Used by the {@linkplain WorkerServer}
 */
public class QueueWorker extends Thread {

    private final BlockingQueue<IWorkerAudioRequest> queue;
    private final ISpeechToTextService speechToTextService;
    private IResultHandler handler;
    private final IPreProcessor preProcessor;

    /**
     * Creates a new {@linkplain QueueWorker} with the specified queue, {@linkplain ISpeechToTextService} and {@linkplain IResultHandler}
     *
     * @param queue               the queue that contains the {@linkplain IWorkerAudioRequest}s that the {@linkplain ISpeechToTextService} should process
     * @param speechToTextService the {@linkplain ISpeechToTextService} that should be used for recognizing the text in the {@linkplain IWorkerAudioRequest}
     * @param handler             the {@linkplain IResultHandler} that is called every time a result from the {@linkplain ISpeechToTextService} is ready
     */
    public QueueWorker(BlockingQueue<IWorkerAudioRequest> queue, ISpeechToTextService speechToTextService, IResultHandler handler, IPreProcessor preProcessor) {
        this.queue = queue;
        this.speechToTextService = speechToTextService;
        this.handler = handler;
        this.preProcessor = preProcessor;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                process();
            }
        } catch (InterruptedException | UnsupportedAudioFormat | ServiceInitializingException | IOException | UnsupportedAudioFileException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void process() throws InterruptedException, UnsupportedAudioFormat, ServiceInitializingException, IOException, UnsupportedAudioFileException {
        IWorkerAudioRequest request = queue.take();
        AudioInputStream inputStream = request.getRequest().getAudio();
        AudioInputStream processedStream = preProcessor.process(inputStream, request.getPreProcesses());
        ISpeechToTextServiceMetadata speechToTextResult = speechToTextService.speechToTextWithMetadata(processedStream);
        ISpeechToTextServiceData data = new SpeechToTextServiceData(speechToTextResult, request.getPreProcesses());
        IFrameworkResult result = new FrameworkResult(request.getId(), data);

        handler.handleResult(result);
    }

    /**
     * Sets the used {@linkplain IResultHandler}
     *
     * @param handler the new {@linkplain IResultHandler} to use
     */
    public void setHandler(IResultHandler handler) {
        this.handler = handler;
    }
}
