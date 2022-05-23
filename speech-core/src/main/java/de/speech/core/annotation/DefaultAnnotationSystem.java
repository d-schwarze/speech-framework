package de.speech.core.annotation;

import de.speech.core.annotation.finding.AnnotatedClassFinder;
import de.speech.core.annotation.finding.DefaultAnnotatedClassFinder;
import de.speech.core.annotation.mapping.AnnotationMappingCache;
import de.speech.core.annotation.mapping.AnnotationMappingFactory;
import de.speech.core.annotation.reflection.PackageClassSearcher;
import de.speech.core.logging.Loggable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.logging.Level;

/**
 * Basic implementation of {@linkplain AnnotationSystem}.
 */
public class DefaultAnnotationSystem extends AnnotationSystem implements Loggable {

    private AnnotatedClassFinder annotatedElementFinder;

    private AnnotationMappingCache annotationMappingCache;

    private AnnotationMappingFactory annotationMappingFactory;

    public DefaultAnnotationSystem() {
        this("");
    }

    public DefaultAnnotationSystem(String... packages) {
        this(new AnnotationMappingCache(), packages);
    }

    public DefaultAnnotationSystem(AnnotationMappingCache annotationMappingCache, String... packages) {
        this.annotationMappingCache = annotationMappingCache;
        annotatedElementFinder = new DefaultAnnotatedClassFinder(annotationMappingCache);
        try {
            annotationMappingFactory = new AnnotationMappingFactory(new PackageClassSearcher(packages));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    @Override
    public void createMappingsForAnnotation(Class<? extends Annotation> annotation) {

        this.annotationMappingCache.addMappedAnnotations(this.annotationMappingFactory.createAnnotationMappings(annotation));

    }

    @Override
    public AnnotatedClassFinder getAnnotatedClassFinder() {
        return this.annotatedElementFinder;
    }

    @Override
    public AnnotationMappingCache getAnnotationMappingCache() {
        return this.annotationMappingCache;
    }
}
