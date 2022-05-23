package de.speech.dev.builder.taskbuilder;
import de.speech.core.postprocessing.IPostProcessFactory;
import de.speech.core.task.implementation.Task;
import de.speech.dev.builder.frameworkConfigurationBuilder.IConfigureFrameworkConfiguration;


/**
 * this interface allows developers to dynamically create an immutable object that implements the ITask interface.
 * @author Sven Ambrosius
 * @version 1.0
 */
public interface ITaskBuilder {



    /**
     * adds a PostProcess to the instance of ITaskBuilder.
     * @param postProcessFactory the PostProcessFactory that will be executed.
     * @return this (see Builder-Pattern for more information)
     */
    ITaskBuilder addPostProcessFactory(IPostProcessFactory postProcessFactory);


    /**
     * Creates a new FrameworkConfiguration that will be executed on the ITask
     * @param identifier the identifier of the framework
     * @param model the model of the framework
     * @param configureBuilder the object to use lambda expressions to add preProcesses to teh FrameworkConfiguration
     * @return the IFrameWorkConfigurationBuilder that allows the dev to add PreProcesses that should get executed on the Framework
     */
    ITaskBuilder addFrameworkConfiguration(String identifier, String model, IConfigureFrameworkConfiguration configureBuilder);

    /**
     * Creates a new FrameworkConfiguration that will be added to the ITask
     * @param identifier the identifier of the framework
     * @param model the model of the framework
     * @return the IFrameWorkConfigurationBuilder that allows the dev to add PreProcesses that should get executed on the Framework
     */
    ITaskBuilder addFrameworkConfiguration(String identifier, String model);


    /**
     * creates the new Task with the given parameters
     * @return the newly created instance of T
     */
    Task buildTask();

}