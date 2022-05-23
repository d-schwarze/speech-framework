package de.speech.core.framework;

/**
 *
 */
public class Framework implements IFramework{

    private final String model;
    private final String identifier;

    public Framework(String identifier, String model) {
        this.identifier = identifier;
        this.model = model;
    }

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }
}
