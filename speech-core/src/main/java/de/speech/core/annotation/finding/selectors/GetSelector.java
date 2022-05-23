package de.speech.core.annotation.finding.selectors;

import java.util.List;

/**
 * Selector that restricts available methods with its own method definitions.
 * Selector is used for getting all classes that are suitable for the given parameters.
 */
public interface GetSelector<T> {

    List<Class<? extends T>> getAll();


    Class<? extends T> getOne();
}
