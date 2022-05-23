package de.speech.core.task.implementation;

import de.speech.core.framework.IFramework;
import de.speech.core.task.IFrameworkConfiguration;

import java.util.List;

/**
 * This class combines a Framework with PreProcesses that should be executed before the processed Data gets send to the Framework
 */
public class FrameworkConfiguration implements IFrameworkConfiguration {
    private final List<String> preProcesses;
    private final IFramework framework;

    /**
     * A Builder for a FrameworkConfiguration
     * @param framework the Framework
     * @param preProcesses the preProcesses that should be executed for this Framework
     */
    public FrameworkConfiguration(IFramework framework, List<String> preProcesses) {
        this.framework = framework;
        this.preProcesses = preProcesses;
    }

    /**
     * A getter for the PreProcesses
     * @return the preProcesses
     */
    @Override
    public List<String> getPreprocesses() {
        return preProcesses;
    }

    /**
     * A getter for the Framework
     * @return the Framework
     */
    @Override
    public IFramework getFramework() {
        return framework;
    }
}
