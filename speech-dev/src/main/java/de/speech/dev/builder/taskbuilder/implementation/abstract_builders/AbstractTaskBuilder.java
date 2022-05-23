package de.speech.dev.builder.taskbuilder.implementation.abstract_builders;

import de.speech.core.postprocessing.IPostProcessFactory;
import de.speech.core.task.IFrameworkConfiguration;
import de.speech.core.task.implementation.FrameworkConfiguration;
import de.speech.dev.builder.frameworkConfigurationBuilder.IConfigureFrameworkConfiguration;
import de.speech.dev.builder.frameworkConfigurationBuilder.implementation.DefaultFrameworkConfigurationBuilder;
import de.speech.dev.builder.taskbuilder.ITaskBuilder;

import java.util.ArrayList;

/**
 * This abstract class implements functionalities that every TaskBuilder has
 * @param <B> The return type of every method. Should be the implementing class
 */
abstract public class AbstractTaskBuilder <B extends AbstractTaskBuilder<B>> implements ITaskBuilder{

    protected ArrayList<IFrameworkConfiguration> frameworkConfigurations = new ArrayList<>();
    protected ArrayList<IPostProcessFactory> postProcessesFactories = new ArrayList<>();
    protected int taskId;


    private static int taskIdCounter = 0;

    /**
     * Creates a AbstractTaskBuilder and sets its TaskId automatically
     */
    AbstractTaskBuilder() {
        this.taskId = taskIdCounter;
        taskIdCounter++;
    }


    /**
     * Adds a new FrameworkConfiguration to the instance with PreProcesses.
     * @param identifier the identifier of the framework
     * @param model the model of the framework
     * @param configureBuilder use "builder -> builder.addPreProcesses(String... preProcesses)" to add preProcesses to the added Framework
     * @return this (see Builder-Pattern for more information)
     */
    @Override
    public B addFrameworkConfiguration(String identifier, String model, IConfigureFrameworkConfiguration configureBuilder) {
        if (configureBuilder == null) {
            addFrameworkConfiguration(identifier, model);
        } else {
            DefaultFrameworkConfigurationBuilder frameworkConfigurationBuilder = new DefaultFrameworkConfigurationBuilder(identifier, model);
            configureBuilder.configure(frameworkConfigurationBuilder);

            FrameworkConfiguration frameworkConfiguration = frameworkConfigurationBuilder.build();
            frameworkConfigurations.add(frameworkConfiguration);
        }

        return (B) this;
    }

    /**
     * Adds a new FrameworkConfiguration to the instance without PreProcesses.
     * @param identifier the identifier of the framework
     * @param model the model of the framework
     * @return this (see Builder-Pattern for more information)
     */
    @Override
    public B addFrameworkConfiguration(String identifier, String model) {
        FrameworkConfiguration fwc =  new DefaultFrameworkConfigurationBuilder(identifier, model).build();
        frameworkConfigurations.add(fwc);

        return (B) this;
    }

    /**
     * Adds a postProcess to the instance
     * @param postProcessFactory the PostProcessFactory that will be added to the instance
     * @return this (see Builder-Pattern for more information)
     */
    @Override
    public B addPostProcessFactory(IPostProcessFactory postProcessFactory) {
        this.postProcessesFactories.add(postProcessFactory);

        return (B) this;
    }
}
