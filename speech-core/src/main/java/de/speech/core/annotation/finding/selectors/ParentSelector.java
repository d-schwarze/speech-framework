package de.speech.core.annotation.finding.selectors;

/**
 * Selector that restricts available methods with its own method definitions.
 * Selector is used defining a parent class that should be inherit by all annotated classes.
 */
public interface ParentSelector extends GetSelector {
    /**
     * Selects a parent class
     * @param parent parent class that all annotated classes should inherit
     * @param <T> parent type
     * @return selector for getting all classes with the selected parameters
     */
    <T> GetSelector<T> parent(Class<T> parent);

}
