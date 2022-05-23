package de.speech.core.annotation.mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;

/**
 * This class maps an {@linkplain Annotation} to an {@linkplain AnnotatedElement}.
 */
public class AnnotationMapping {

    /**
     * Annotation that should be present on {@linkplain #mappedElement}
     */
    private Class<? extends Annotation> annotation;

    /**
     * All {@linkplain AnnotatedElement AnnotatedElements}, where {@linkplain #annotation} is present.
     */
    private Class<?> mappedElement;

    /**
     * Meta data provided by {@linkplain #annotation}
     */
    private Map<String, Object> annotationValues;

    public AnnotationMapping(
            Class<? extends Annotation> annotation,
            Map<String, Object> annotationValues,
            Class<?> mappedElement) {

        this.annotation = annotation;
        this.annotationValues = annotationValues;
        this.mappedElement = mappedElement;
    }

    /**
     * Getter for {@linkplain #annotation}
     * @return {@linkplain #annotation}
     */
    public Class<? extends Annotation> getAnnotation() {
        return this.annotation;
    }

    /**
     * Getter for {@linkplain #mappedElement}
     * @return {@linkplain #mappedElement}
     */
    public Class<?> getMappedElement() {
        return this.mappedElement;
    }

    /**
     * Getter for {@linkplain #annotationValues}
     * @return {@linkplain #annotationValues}
     */
    public Map<String, Object> getAnnotationValues() {
        return annotationValues;
    }
}
