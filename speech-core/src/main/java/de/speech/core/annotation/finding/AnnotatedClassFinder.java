package de.speech.core.annotation.finding;

import de.speech.core.annotation.finding.selectors.AnnotationSelector;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * This interface provides method for finding derivatives of an class which are annotated by a set of annotations.
 * For instance you have Class A and you want to find all classes that inherit Class A and have a specific set of
 * annotations.
 */
public abstract class AnnotatedClassFinder {

    /**
     * Creates a new {@linkplain AnnotatedClassFindingBuilder} to easily find annotated classes with a set of
     * parameters.
     *
     * @return restricted builder part
     */
    public AnnotationSelector findAnnotatedClasses() {
        return new AnnotatedClassFindingBuilder(this);
    }

    /**
     * Finds all annotated classes with the given parameters.
     *
     * @param parentClass class that all annotated classes should extend. May be {@code null} if not needed.
     * @param annotationsWithValues all annotations with their values that should be present on the annotations.
     * @param <T> parentClass type
     * @return all annotated classes that are suitable for the given parameters
     */
    public abstract <T> List<Class<? extends T>> findAnnotatedClasses(
            Class<?> parentClass,
            Map<Class<? extends Annotation>, Map<String, Object>> annotationsWithValues);
}
