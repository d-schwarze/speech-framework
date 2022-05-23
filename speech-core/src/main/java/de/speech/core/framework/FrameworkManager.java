package de.speech.core.framework;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton to manage all frameworks.
 */
public class FrameworkManager {

    private final static FrameworkManager instance = new FrameworkManager();

    private final List<IFramework> managedFrameworks;

    private FrameworkManager() {
        managedFrameworks = new ArrayList<>();
    }

    /**
     * A getter for the only instance of the singleton.
     * @return frameworkManager
     */
    public static FrameworkManager getInstance() { return instance; }

    /**
     * Finds the framework with the identifier and with the model. Creates one, if no exist.
     * @param identifier identifier of the framework
     * @param model model loaded on the framework.
     * @return Framework
     */
    public synchronized IFramework findFramework(String identifier, String model) {
        for (IFramework framework : managedFrameworks) {
            if (framework.getModel().equals(model) && framework.getIdentifier().equals(identifier)) {
                return framework;
            }
        }

        IFramework f =  new Framework(identifier, model);

        managedFrameworks.add(f);
        return f;
    }
}