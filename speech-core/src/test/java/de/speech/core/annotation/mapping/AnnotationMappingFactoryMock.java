package de.speech.core.annotation.mapping;

import de.speech.core.annotation.reflection.ClassSearcher;

import java.lang.annotation.Annotation;
import java.util.List;

public class AnnotationMappingFactoryMock extends AnnotationMappingFactory {


    public AnnotationMappingFactoryMock(ClassSearcher classSearcher) {
        super(classSearcher);
    }

    public List<AnnotationMapping> initializeAnnotationMapping(Class<? extends Annotation> annotation) {
        return super.initializeAnnotationMappings(annotation);
    }

}
