package de.speech.core.task.result.implementation;

import de.speech.core.framework.IFramework;
import de.speech.core.task.IFrameworkConfiguration;

import java.util.List;

public class FrameworkConfiguration implements IFrameworkConfiguration {

    private List<String> preprocesses;
    private IFramework framework;

    public FrameworkConfiguration(IFramework framework, List<String> preprocesses) {
        this.preprocesses = preprocesses;
        this.framework = framework;
    }

    @Override
    public List<String> getPreprocesses() {
        return null;
    }

    @Override
    public IFramework getFramework() {
        return null;
    }
}
