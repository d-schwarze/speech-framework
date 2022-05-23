package de.speech.core.annotation.finding.selectors;

/**
 * Selector that restricts available methods with its own method definitions.
 * Selector is used for selecting annotation values.
 */
public interface AnnotationValueSelector extends AnnotationWithParentSelector {

    /**
     * Adds a new annotation value that should be present on the annotation.
     * For instance a class is annotated with {@code @PostProcessFactory(parallel=false)}.
     * So you would call {@code withValue("parallel", false)}.
     *
     * @param valueName annotation value name
     * @param value annotation value
     * @return selector for selecting additionally values or an new annotation
     * {@linkplain AnnotationWithParentSelector see AnnotationWithParentSelector}
     */
    AnnotationValueSelector withValue(String valueName, Object value);
}
