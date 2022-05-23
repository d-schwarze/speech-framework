package de.speech.core.annotation.finding.selectors;

import java.lang.annotation.Annotation;

/**
 * Selector that restricts available methods with its own method definitions.
 * Selector is used for selecting annotations.
 */
public interface AnnotationSelector {

    /**
     * Select an annotation
     * @param annotation annotation that should be selected
     * @return selector for selecting annotation values
     */
    AnnotationValueSelector annotation(Class<? extends Annotation> annotation);
}
