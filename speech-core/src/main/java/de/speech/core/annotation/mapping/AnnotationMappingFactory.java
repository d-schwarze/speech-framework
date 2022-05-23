package de.speech.core.annotation.mapping;

import de.speech.core.annotation.reflection.ClassSearcher;
import de.speech.core.annotation.reflection.PackageClassSearcher;
import de.speech.core.annotation.reflection.ReflectionUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Factory class for creating new {@linkplain AnnotationMapping annotation mappings}. Already created mappings are
 * cached and reused.
 */
public class AnnotationMappingFactory {

    /**
     * Global cache reuse mappings.
     */
    private AnnotationMappingCache globalFactoryCache = new AnnotationMappingCache();

    private ClassSearcher classSearcher;

    public AnnotationMappingFactory() throws IOException {
        this(new PackageClassSearcher(""));
    }


    public AnnotationMappingFactory(ClassSearcher classSearcher) {
        this.classSearcher = classSearcher;
    }


    /**
     * Creates a new {@linkplain AnnotationMapping} for a given annotation.
     * @param annotation annotation that has to be present in each mapped class
     * @return mappings that contain the annotation
     */
    public List<AnnotationMapping> createAnnotationMappings(Class<? extends Annotation> annotation) {

        List<AnnotationMapping> cachedMappings = globalFactoryCache.getAnnotationMappingsByAnnotation(annotation);

        if (cachedMappings.size() > 0) {
            return cachedMappings;
        }

        List<AnnotationMapping> createdAnnotationMappings = initializeAnnotationMappings(annotation);

        globalFactoryCache.getAnnotationMappings().addAll(createdAnnotationMappings);

        return createdAnnotationMappings;
    }

    /**
     * Initializes all new {@linkplain AnnotationMapping} for a given {@code annotation}. Default annotation values
     * are overridden if the value was changed on a particular class.
     *
     * @param annotation for which all mappings should be created
     * @return the newly initialized annotation mappings
     */
    protected List<AnnotationMapping> initializeAnnotationMappings(Class<? extends Annotation> annotation) {

        Map<Class<?>, Map<String, Object>> annotatedClassesWithAnnotationValues = classSearcher.findAnnotatedClassesWithAnnotationValues(annotation);

        List<AnnotationMapping> initializedAnnotationMappings = new ArrayList<>();

        annotatedClassesWithAnnotationValues.forEach((clazz, annotationValues) -> {
            Map<String, Object> finalAnnotationValues = ReflectionUtils.getDefaultAnnotationValues(annotation);

            annotationValues.forEach(finalAnnotationValues::put);

            AnnotationMapping mapping = new AnnotationMapping(annotation, finalAnnotationValues, clazz);


            initializedAnnotationMappings.add(mapping);
        });

        return initializedAnnotationMappings;
    }


}
