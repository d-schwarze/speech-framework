package de.speech.dev.builder.taskbuilder.implementation.abstract_builders;

import de.speech.core.logging.Loggable;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.implementation.audioRequest.AudioRequestWithPath;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * Extends AbstractTaskBuilder to enable building Tasks which AudioRequests have a Path to the AudioInputStream
 * @param <B>
 */
public abstract class AbstractTaskBuilderWithPath<B extends AbstractTaskBuilderWithPath<B>> extends AbstractTaskBuilder<B> implements Loggable {
    protected String path;



    public AbstractTaskBuilderWithPath(String path) {
        super();
        this.path = path;
    }


    /**
     * Iterates through the file (dir) that is set by setAudioRequestPath() compiles every AudioInputStream in the file
     * to a AudioRequest and adds them to the instance
     * @return the created AudioRequests of the Task
     */
    protected List<IAudioRequest> buildAudioRequests() {
        long requestId = 0;
        File dir = new File(path);
        List<IAudioRequest> requests = new ArrayList<>();
        if (dir.exists()) {
            if (dir.isDirectory()) {
                addRequests(dir.listFiles(), requests, requestId);
            } else {
                String absolutePathOfFile = dir.getAbsolutePath();
                requests.add(new AudioRequestWithPath(requestId, absolutePathOfFile));
            }
        } else {
            LOGGER.log(Level.WARNING, "Der angegebene Pfad existiert nicht.");
        }



        return requests;
    }

    /**
     * adds a Path to a file or folder that contains all AudioInputStreams of the Task.
     *
     * @param path the Path to the file or folder that contains the AudioInputStreams.
     * @return this (see Builder-Pattern for more information)
     */
    public B setAudioRequestPath(String path) {
        this.path = path;

        return (B) this;
    }

    private void addRequests(File[] files, List<IAudioRequest> requests, long nextRequestId) {
        Arrays.sort(files);
        for (File file: files) {
            if (file.isDirectory()) {
                addRequests(file.listFiles(), requests, nextRequestId);
            } else {
                String absolutePathOfFile = file.getAbsolutePath();
                requests.add(new AudioRequestWithPath(nextRequestId, absolutePathOfFile));
                nextRequestId++;
            }
        }
    }
}
