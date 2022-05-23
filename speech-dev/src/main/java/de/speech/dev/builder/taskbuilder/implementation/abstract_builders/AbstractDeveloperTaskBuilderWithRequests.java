package de.speech.dev.builder.taskbuilder.implementation.abstract_builders;

import de.speech.core.task.IAudioRequest;
import de.speech.core.task.implementation.audioRequest.AudioRequestWithInputStream;
import de.speech.core.task.implementation.audioRequest.DeveloperAudioRequest;

import javax.sound.sampled.AudioInputStream;
import java.util.ArrayList;

/**
 * Abstract class that extends AbstractTaskBuilderWithRequests and adds the possibility to add Strings to every AudioRequest that
 * defines what the actual text of this AudioRequest is
 * @param <B>
 */
public abstract class AbstractDeveloperTaskBuilderWithRequests<B extends AbstractDeveloperTaskBuilderWithRequests<B>> extends AbstractTaskBuilderWithRequests<B> {
    protected ArrayList<IAudioRequest> audioRequests = new ArrayList<>();
    private long id = 0;

    /**
     * Adds an AudioInputStream to the instance and sets the actual text for the Request to: ""
     * (uses addAudioInputStreamWithActualText(audioInputStream, ""))
     * @param audioInputStream the AudioInputStream that will be added to the ITaskBuilder.
     * @return this (see Builder-Pattern for more information)
     */
    @Override
    public B addAudioInputStream(AudioInputStream audioInputStream) {
        addAudioInputStreamWithActualText(audioInputStream, "");

        return (B) this;
    }

    /**
     * Adds an AudioInputStream to the instance and sets the actual text for this Request.
     * @param audioInputStream the AudioInputStream that will be added to the instance of TaskBuilder
     * @param actualText the String that defines what the audioInputStream actually contains as text
     * @return this (see Builder-Pattern for more information)
     */
    public B addAudioInputStreamWithActualText(AudioInputStream audioInputStream, String actualText) {
        AudioRequestWithInputStream requestWithInputStream = new AudioRequestWithInputStream(id, audioInputStream);
        this.audioRequests.add(new DeveloperAudioRequest(requestWithInputStream, actualText));

        id++;

        return (B) this;
    }

    /**
     * builds the AudioRequests of the Task
     * @return the AudioRequests of the Task
     */
    @Override
    protected ArrayList<IAudioRequest> buildAudioRequests() {
        return this.audioRequests;
    }
}
