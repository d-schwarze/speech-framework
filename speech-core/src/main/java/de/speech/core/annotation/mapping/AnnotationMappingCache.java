package de.speech.core.annotation.mapping;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for storing a collection of {@linkplain AnnotationMapping}. This class can be used as a cache for
 * {@linkplain AnnotationMapping}.
 */
public class AnnotationMappingCache {

    /**
     * Stored {@linkplain AnnotationMapping}
     */
    private List<AnnotationMapping> mappedAnnotations;

    public AnnotationMappingCache() {
        mappedAnnotations = new ArrayList<>();
    }

    /**
     * Adds a new mapping to the cache
     * @param annotationMapping new mapping
     */
    public void addMappedAnnotation(AnnotationMapping annotationMapping) {
        if (!this.mappedAnnotations.contains(annotationMapping))
            this.mappedAnnotations.add(annotationMapping);
    }

    /**
     * Adds new mappings to the cache
     * @param annotationMappings new mappings
     */
    public void addMappedAnnotations(List<AnnotationMapping> annotationMappings) {
        for (AnnotationMapping mapping : annotationMappings) {
            this.addMappedAnnotation(mapping);
        }
    }

    /**
     * Find all {@linkplain AnnotationMapping mappings} by its {@linkplain AnnotationMapping#getAnnotation()}.
     * @param annotation annotation which should be present at each mapping
     * @return mappings with {@code annotation}
     */
    public List<AnnotationMapping> getAnnotationMappingsByAnnotation(Class<? extends Annotation> annotation) {
        return mappedAnnotations
                .stream()
                .filter(mapping -> mapping.getAnnotation().equals(annotation))
                .collect(Collectors.toList());
    }

    /**
     * Gets all cached mappings.
     * @return cached mappings
     */
    public List<AnnotationMapping> getAnnotationMappings() {
        return mappedAnnotations;
    }
}
