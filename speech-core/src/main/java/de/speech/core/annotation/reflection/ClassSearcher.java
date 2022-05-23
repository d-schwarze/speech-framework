package de.speech.core.annotation.reflection;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * Interface to search compiled java files (classfiles, i.e.: ClassA.class) for particular information.<br>
 * For instance finding all Annotations that are present on a class.
 */
public interface ClassSearcher {

    /**
     * Classfile suffix
     */
    String CLASS_IDENTIFIER = ".class";

    /**
     * Find all classes that are annotated with an {@code annotation}.
     * @param annotation that should be present on the classes
     * @return all annotated classes
     */
    List<Class<?>> findAnnotatedClasses(Class<? extends Annotation> annotation);

    /**
     * Find all values that are changed on a particular annotation of a class.
     * @param annotatedElement class that is annotated with {@code annotation}
     * @param annotation annotation whose changed values should be extracted
     * @return all annotation values of a particular class and a particular annotation
     */
    Map<String, Object> getAnnotatedValuesFromAnnotatedElement(
            Class<?> annotatedElement,
            Class<? extends Annotation> annotation);

    /**
     * Finds all classes that are annotated with {@code annotation} and also its values
     * @see #getAnnotatedValuesFromAnnotatedElement(Class annotatedElement, Class annotation) 
     * @see #findAnnotatedClasses(Class annotation)
     * @param annotation that should be present on the classes
     * @return all annotated elements with the all annotation values of {@code annotation}
     */
    Map<Class<?>, Map<String, Object>> findAnnotatedClassesWithAnnotationValues(Class<? extends Annotation> annotation);

}
