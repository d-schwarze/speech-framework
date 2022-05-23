package de.speech.dev.builder.frameworkConfigurationBuilder;

import de.speech.core.task.IFrameworkConfiguration;

/**
 * This interface allows devs to add PreProcesses to the Framework and safe it as FrameworkConfiguration in ITaskBuilder
 */
public interface IFrameworkConfigurationBuilder {

    /**
     * Adds the preProcesses to the FrameworkConfiguration.
     * @param preprocesses the added preProcesses
     * @return this (see Builder-Pattern for more information)
     */
    IFrameworkConfigurationBuilder addPreProcesses(String... preprocesses);

    IFrameworkConfiguration build();
}
