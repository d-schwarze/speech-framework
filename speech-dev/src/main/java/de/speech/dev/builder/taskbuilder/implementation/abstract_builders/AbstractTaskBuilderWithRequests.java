package de.speech.dev.builder.taskbuilder.implementation.abstract_builders;

import de.speech.core.task.IAudioRequest;
import de.speech.core.task.implementation.audioRequest.AudioRequestWithInputStream;

import javax.sound.sampled.AudioInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This abstract class extends AbstractTaskBuilder to enable building Tasks that contains AudioRequest that have an AudioInputStreamObject
 * @param <B>
 */
public abstract class AbstractTaskBuilderWithRequests<B extends AbstractTaskBuilderWithRequests<B>> extends AbstractTaskBuilder<B> {
    protected List<AudioInputStream> audioInputStreams = new ArrayList<>();
    private final long requestIdCounter = 0;


    public AbstractTaskBuilderWithRequests() {
        super();
    }

    /**
     * adds an AudioInputStream to the instance of TaskBuilderWithRequests.
     * @param audioInputStream the AudioInputStream that will be added to the ITaskBuilder.
     * @return this (see Builder-Pattern for more information)
     */
    public B addAudioInputStream(AudioInputStream audioInputStream) {
        this.audioInputStreams.add(audioInputStream);

        return (B) this;
    }

    /**
     * Builds the AudioRequestsWithInputStream for the Task
     * @return the created AudioRequests of the Task
     */
    protected List<IAudioRequest> buildAudioRequests() {
        long id = 0;
        List<IAudioRequest> audioRequests = new ArrayList<>();

        for (AudioInputStream audioInputStream:
                audioInputStreams) {
            audioRequests.add(new AudioRequestWithInputStream(id, audioInputStream));
            id++;
        }

        return audioRequests;
    }
}