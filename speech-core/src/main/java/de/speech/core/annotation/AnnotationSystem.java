package de.speech.core.annotation;

import de.speech.core.annotation.finding.AnnotatedClassFinder;
import de.speech.core.annotation.mapping.AnnotationMappingCache;

import java.lang.annotation.Annotation;

/**
 * Provides an easy to use integration for {@code de.speech.core.annotation}.
 *
 * Extend this class to provide a custom AnnotationSystem or use the global AnnotationSystem that is accessible
 * via {@linkplain #getGlobal()} in each class of this project.
 *
 * In case you need a more adapted and specialized version of an {@linkplain AnnotationSystem}, you can extend
 * {@linkplain AnnotationSystemAdapter} and provide some specialized functionalities on top of the provided
 *  {@linkplain #getAnnotatedClassFinder() AnnotatedClassFinder} and
 *  {@linkplain #getAnnotationMappingCache() AnnotationMappingCache}.
 *
 */
public abstract class AnnotationSystem {

    /**
     * Getter for the annotated element finder
     * @return annotated element finder
     */
    public abstract AnnotatedClassFinder getAnnotatedClassFinder();

    /**
     * Getter for the annotation mapping cache
     * @return annotation mapping cache
     */
    public abstract AnnotationMappingCache getAnnotationMappingCache();

    public abstract void createMappingsForAnnotation(Class<? extends Annotation> annotation);

    /**
     * Global annotation system which can be accessed via {@linkplain #getGlobal()}.
     */
    private static AnnotationSystem globalAnnotationSystem;

    /**
     * Gets the global annotation system. A singleton-pattern is used.
     * @return global annotation system
     */
    public static AnnotationSystem getGlobal() {
        if (globalAnnotationSystem == null) {
            globalAnnotationSystem = new DefaultAnnotationSystem();
        }
        return globalAnnotationSystem;
    }
}
