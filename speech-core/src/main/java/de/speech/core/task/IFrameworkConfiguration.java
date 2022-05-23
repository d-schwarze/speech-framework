package de.speech.core.task;

import de.speech.core.framework.IFramework;

import java.util.List;

/**
 * This interface defines an object that stores the IAudioFilter for the IFramework that should get executed before the IFramework
 */
public interface IFrameworkConfiguration {

    /**
     * a getter for the List of IAudioFilter that get executed before the framework
     * @return the List of String identifiers for the IAudioFilters.
     */
    List<String> getPreprocesses();

    /**
     * a getter for the IFramework that
     * @return the IFramework.
     */
    IFramework getFramework();
}
