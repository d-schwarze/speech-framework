package de.speech.dev.builder.frameworkConfigurationBuilder.implementation;

import de.speech.core.framework.FrameworkManager;
import de.speech.core.framework.IFramework;
import de.speech.core.task.implementation.FrameworkConfiguration;
import de.speech.dev.builder.frameworkConfigurationBuilder.IFrameworkConfigurationBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class implements the IFrameworkConfigurationBuilder Interface and is used to build a FrameworkConfiguration
 */
public class DefaultFrameworkConfigurationBuilder implements IFrameworkConfigurationBuilder {
    private List<String> preProcesses = new ArrayList<>();
    private IFramework framework;

    /**
     * Creates a instance of DefaultFrameworkConfigurationBuilder the parameters are used to search if a framework with thouse
     * parameters is already in the system
     * @param identifier the identifier of the Framework
     * @param model the model of the Framework
     */
    public DefaultFrameworkConfigurationBuilder(String identifier, String model) {
        this.framework = FrameworkManager.getInstance().findFramework(identifier, model);
    }

    /**
     * Adds preProcesses to the instance of DefaultFrameworkConfigurationBuilder
     * @param preProcesses the identifiers of the preProcesses that should be executed for the one Framework
     * @return this (see Builder-Pattern for more information)
     */
    @Override
    public DefaultFrameworkConfigurationBuilder addPreProcesses(String... preProcesses) {
        Collections.addAll(this.preProcesses, preProcesses);


        return this;
    }

    /**
     * Builds the new FrameworkConfiguration with the given parameters
     * @return the new FrameworkConfiguration
     */
    public FrameworkConfiguration build() {
        return new FrameworkConfiguration(framework, preProcesses);
    }
}