package de.speech.dev.builder.frameworkConfigurationBuilder;

/**
 * This interface is used to enable lambda expressions for an easier possibility to add preProcesses to a FrameworkConfigurationBuilder
 */
public interface IConfigureFrameworkConfiguration {

    /**
     * Method used to enable lambda-expressions for FrameworkConfigurationBuilder
     * @param builder the FrameworkConfigurationBuilder
     */
    void configure(IFrameworkConfigurationBuilder builder);
}
