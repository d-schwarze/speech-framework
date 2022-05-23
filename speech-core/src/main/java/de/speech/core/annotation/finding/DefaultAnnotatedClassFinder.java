package de.speech.core.annotation.finding;

import de.speech.core.annotation.mapping.AnnotationMapping;
import de.speech.core.annotation.mapping.AnnotationMappingCache;
import de.speech.core.annotation.reflection.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Default implementation of {@linkplain AnnotatedClassFinder}. As underlying set of mappings, an
 * {@linkplain AnnotationMappingCache} is used. A findable class has to be part of an
 * {@linkplain de.speech.core.annotation.mapping.AnnotationMapping} and in the cache.
 */
public class DefaultAnnotatedClassFinder extends AnnotatedClassFinder  {

    /**
     * Cache where all findable annotated classes are stored.
     */
    private AnnotationMappingCache annotationMappingCache;

    public DefaultAnnotatedClassFinder(AnnotationMappingCache annotationMappingCache) {
        this.annotationMappingCache = annotationMappingCache;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> List<Class<? extends T>> findAnnotatedClasses(Class<?> parentClass, Map<Class<? extends Annotation>, Map<String, Object>> annotationsWithValues) {
        assert(annotationsWithValues != null);

        //Holds on to all annotations that were found for a specific class
        Map<Class<?>, Set<Class<? extends Annotation>>> tempMapping = new HashMap<>();

        List<Class<? extends T>> annotatedClasses = new ArrayList<>();

        for (AnnotationMapping mapping : annotationMappingCache.getAnnotationMappings()) {
            if (isAnnotationMappingSuitable(mapping, parentClass, annotationsWithValues)) {

                tempMapping.computeIfAbsent(mapping.getMappedElement(), k -> new HashSet<>());

                tempMapping.get(mapping.getMappedElement()).add(mapping.getAnnotation());

                if (tempMapping.get(mapping.getMappedElement()).size() == annotationsWithValues.size()) {
                    annotatedClasses.add((Class<? extends T>) mapping.getMappedElement());
                }
            }
        }

        return annotatedClasses;
    }

    /**
     * Determines whether a given {@linkplain AnnotationMapping mapping} is suitable for the given parameters.
     * @param mapping that should be checked
     * @param parentClass {@linkplain #isParentClassValid(Class, Class) checks mapping parent for suitability}
     * @param annotationsWithValues {@linkplain #areAnnotationValuesPresent(Map, Map) checks mapping annotations for suitability}
     * @return true if a mapping is suitable for the given parameters
     */
    private boolean isAnnotationMappingSuitable(AnnotationMapping mapping, Class<?> parentClass, Map<Class<? extends Annotation>, Map<String, Object>> annotationsWithValues) {

        //Check if annotation is present
        if (!isAnnotationPresent(mapping.getAnnotation(), annotationsWithValues.keySet())) return false;

        //Check if parent is valid
        if (!isParentClassValid(mapping.getMappedElement(), parentClass)) return false;

        //Check if annotation values equals
        if (!areAnnotationValuesPresent(annotationsWithValues.get(mapping.getAnnotation()), mapping.getAnnotationValues())) return false;

        return true;
    }

    /**
     * Checks whether a class is a child of a given parent class.<br>
     * Note: in case {@code parent=null} true is returned.
     * @param child child of {@code parent}
     * @param parent parent of {@code child}
     * @return true if {@code child} is really a children of {@code parent} or if {@code parent = null}
     * @see ReflectionUtils#isDerivativeOf(Class, Class)
     */
    private boolean isParentClassValid(Class<?> child, Class<?> parent) {
        if (parent == null) {
            return true;
        }

        return ReflectionUtils.isDerivativeOf(child, parent);
    }

    /**
     * Determines whether an annotations is present on given set of annotations.
     * @param annotation annotation that should be part of {@code targetAnnotations}
     * @param targetAnnotations annotations that are compared
     * @return true if the {@code annotation} is present
     */
    private boolean isAnnotationPresent(Class<? extends Annotation> annotation, Set<Class<? extends Annotation>> targetAnnotations) {
        return targetAnnotations.contains(annotation);
    }

    /**
     * TargetValues has to contain all values -> values are a subset targetValues
     * @param values values that should be present at {@code targetValues}
     * @param targetValues values that are compared
     * @return true if {@code values} are present on {@code targetValues}
     */
    private boolean areAnnotationValuesPresent(Map<String, Object> values, Map<String, Object> targetValues) {

        AtomicBoolean annotationValuesValid = new AtomicBoolean(true);

        values.forEach((key, value) -> {
            Object targetValue = targetValues.get(key);
            if  (targetValue != null && value != null) {
               if (!targetValue.equals(value)) {
                   annotationValuesValid.set(false);
               }
            }

            if (targetValue == null ^ value == null) {
                annotationValuesValid.set(false);
            }
        });

        return annotationValuesValid.get();
    }
}
