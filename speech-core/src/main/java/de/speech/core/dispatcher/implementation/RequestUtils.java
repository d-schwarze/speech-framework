package de.speech.core.dispatcher.implementation;

import de.speech.core.dispatcher.IWorkerAudioRequest;
import de.speech.core.dispatcher.implementation.requestresult.WorkerAudioRequest;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.implementation.audioRequest.AudioRequestWithInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.InputStreamRequestContent;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A util class to convert {@linkplain IWorkerAudioRequest}s to {@linkplain Request}s and {@linkplain HttpServletRequest}s to {@linkplain IWorkerAudioRequest}s
 */
public final class RequestUtils {

    private static final String PRE_PROCESSES = "preProcesses";
    private static final String REQUEST_ID = "requestId";
    private static final String FRAME_LENGTH = "frameLength";
    private static final String ENCODING = "encoding";
    private static final String SAMPLE_RATE = "sampleRate";
    private static final String SAMPLE_SIZE_IN_BITS = "sampleSizeInBits";
    private static final String CHANNELS = "channels";
    private static final String FRAME_SIZE = "frameSize";
    private static final String FRAME_RATE = "frameRate";
    private static final String BIG_ENDIAN = "bigEndian";
    private static final String WORKER_REQUEST_ID = "id";

    private static final List<String> REQUIRED_PARAMETERS = Arrays.asList(
            PRE_PROCESSES,
            REQUEST_ID,
            FRAME_LENGTH,
            ENCODING,
            SAMPLE_RATE,
            SAMPLE_SIZE_IN_BITS,
            CHANNELS,
            FRAME_SIZE,
            FRAME_RATE,
            BIG_ENDIAN,
            WORKER_REQUEST_ID
    );

    private RequestUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Converts an {@linkplain IWorkerAudioRequest} to a {@linkplain Request} that a {@linkplain HttpClient} can send
     *
     * @param iWorkerAudioRequest the {@linkplain IWorkerAudioRequest} to convert
     * @param client              the {@linkplain HttpClient} used to create the request
     * @param uri                 the uri where the request is send to
     * @return the {@linkplain Request} that is ready to be sent with {@linkplain Request#send()}
     * @throws IOException                   if there was an error getting the {@linkplain AudioInputStream} from the {@linkplain IWorkerAudioRequest}, see {@link IAudioRequest#getAudio()}
     * @throws UnsupportedAudioFileException if there was an error getting the {@linkplain AudioInputStream} from the {@linkplain IWorkerAudioRequest}, see {@link IAudioRequest#getAudio()}
     */
    public static Request iWorkerAudioRequestToRequest(IWorkerAudioRequest iWorkerAudioRequest, HttpClient client, String uri) throws IOException, UnsupportedAudioFileException {
        AudioInputStream stream = iWorkerAudioRequest.getRequest().getAudio();
        AudioFormat format = stream.getFormat();

        return client.POST(uri)
                .param(PRE_PROCESSES, String.join(",", iWorkerAudioRequest.getPreProcesses()))
                .param(REQUEST_ID, String.valueOf(iWorkerAudioRequest.getRequest().getRequestId()))
                .param(WORKER_REQUEST_ID, String.valueOf(iWorkerAudioRequest.getId()))
                .param(FRAME_LENGTH, String.valueOf(stream.getFrameLength()))
                .param(ENCODING, format.getEncoding().toString())
                .param(SAMPLE_RATE, String.valueOf(format.getSampleRate()))
                .param(SAMPLE_SIZE_IN_BITS, String.valueOf(format.getSampleSizeInBits()))
                .param(CHANNELS, String.valueOf(format.getChannels()))
                .param(FRAME_SIZE, String.valueOf(format.getFrameSize()))
                .param(FRAME_RATE, String.valueOf(format.getFrameRate()))
                .param(BIG_ENDIAN, String.valueOf(format.isBigEndian()))
                .body(new InputStreamRequestContent(stream));
    }

    /**
     * Extracts an {@linkplain IWorkerAudioRequest} from an {@linkplain HttpServletRequest}
     *
     * @param request the {@linkplain HttpServletRequest}
     * @return the extracted {@linkplain IWorkerAudioRequest}
     * @throws IllegalArgumentException if the request was not valid
     */
    public static IWorkerAudioRequest requestToIWorkerAudioRequest(HttpServletRequest request) throws IllegalArgumentException {
        if (!areParametersAvailable(request)) {
            throw new IllegalArgumentException("Not all required parameters are present");
        }

        List<String> preProcesses;
        // if the string is empty there are no preprocesses
        if (request.getParameter(PRE_PROCESSES).equals("")) {
            preProcesses = Collections.emptyList();
        } else {
            preProcesses = Arrays.asList(request.getParameter(PRE_PROCESSES).split(","));
        }

        String encoding = request.getParameter(ENCODING);

        long requestId, frameLength, id;
        float sampleRate, frameRate;
        int sampleSizeInBits, channels, frameSize;
        boolean bigEndian;

        try {
            id = Long.parseLong(request.getParameter(WORKER_REQUEST_ID));
            requestId = Long.parseLong(request.getParameter(REQUEST_ID));
            frameLength = Long.parseLong(request.getParameter(FRAME_LENGTH));

            sampleRate = Float.parseFloat(request.getParameter(SAMPLE_RATE));
            frameRate = Float.parseFloat(request.getParameter(FRAME_RATE));

            sampleSizeInBits = Integer.parseInt(request.getParameter(SAMPLE_SIZE_IN_BITS));
            channels = Integer.parseInt(request.getParameter(CHANNELS));
            frameSize = Integer.parseInt(request.getParameter(FRAME_SIZE));

            bigEndian = Boolean.parseBoolean(request.getParameter(BIG_ENDIAN));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("There was an error parsing the parameters");
        }

        AudioFormat format = new AudioFormat(new AudioFormat.Encoding(encoding), sampleRate, sampleSizeInBits, channels, frameSize, frameRate, bigEndian);
        AudioInputStream stream;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            request.getInputStream().transferTo(outputStream);
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            stream = new AudioInputStream(inputStream, format, frameLength);
        } catch (IOException e) {
            throw new IllegalArgumentException("There was an error parsing the parameters");
        }

        IAudioRequest audioRequest = new AudioRequestWithInputStream(requestId, stream);
        return new WorkerAudioRequest(audioRequest, preProcesses, id);
    }

    private static boolean areParametersAvailable(HttpServletRequest request) {
        return REQUIRED_PARAMETERS.stream().allMatch(parameter -> request.getParameter(parameter) != null);
    }

}
