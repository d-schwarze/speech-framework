package de.speech.core.annotation.finding;

import de.speech.core.annotation.finding.selectors.AnnotationSelector;
import de.speech.core.annotation.finding.selectors.AnnotationValueSelector;
import de.speech.core.annotation.finding.selectors.GetSelector;
import de.speech.core.annotation.finding.selectors.ParentSelector;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder class to easily find annotated classes with a given set of parameters like annotation values or a
 * super class.
 */
public class AnnotatedClassFindingBuilder implements AnnotationSelector, AnnotationValueSelector, GetSelector, ParentSelector {

    /**
     * Values that should be present on the annotations
     * @see AnnotationValueSelector#withValue(String, Object)
     */
    private Map<Class<? extends Annotation>, Map<String, Object>> annotationsWithValues = new HashMap<>();

    /**
     * Internal variable that determines to which annotation newly added values should be mapped
     */
    private Class<? extends Annotation> currentAnnotation;

    /**
     * Class that should be extended by all annotated classes. Null if it should not be checked.
     */
    private Class<?> parentClass;

    /**
     * Finder for finally finding all annotated classes with the given parameters.
     */
    private AnnotatedClassFinder finder;

    public AnnotatedClassFindingBuilder(AnnotatedClassFinder finder) {
        this.finder = finder;
    }

    @Override
    public AnnotationValueSelector annotation(Class<? extends Annotation> annotation) {
        currentAnnotation = annotation;

        if (annotationsWithValues.get(annotation) == null) {
            annotationsWithValues.put(annotation, new HashMap<>());
        }

        return this;
    }


    @Override
    public AnnotationValueSelector withValue(String valueName, Object value) {

        Map<String, Object> annotationValues = annotationsWithValues.get(currentAnnotation);
        annotationValues.put(valueName, value);

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> GetSelector<T> parent(Class<T> parent) {
        parentClass = parent;

        return this;
    }

    @Override
    public List<Class<?>> getAll() {
        return finder.findAnnotatedClasses(parentClass, annotationsWithValues);
    }

    @Override
    public Class<?> getOne() {
        List<Class<?>> classes = getAll();

        if (classes != null && classes.size() > 0) {
            return classes.get(0);
        }

        return null;
    }
}
