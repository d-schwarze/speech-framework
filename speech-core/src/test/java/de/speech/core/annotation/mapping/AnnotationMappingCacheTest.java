package de.speech.core.annotation.mapping;

import de.speech.core.annotation.adaptions.postprocessing.PostProcessFactory;
import de.speech.core.annotation.annotatedtestclasses.MultiAnnotatedClass;
import de.speech.core.annotation.annotatedtestclasses.TestAnnotation;
import de.speech.core.annotation.annotatedtestclasses.TestPostProcessFactory2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnnotationMappingCacheTest {

    private static AnnotationMappingCache cache;

    private static AnnotationMapping mapping1;
    private static AnnotationMapping mapping2;
    private static AnnotationMapping mapping3;

    @BeforeAll
    public static void initializeTestSuit() {
        cache = new AnnotationMappingCache();


        mapping1 = new AnnotationMapping(PostProcessFactory.class, null, TestPostProcessFactory2.class);
        mapping2 = new AnnotationMapping(PostProcessFactory.class, null, MultiAnnotatedClass.class);
        mapping3 = new AnnotationMapping(TestAnnotation.class, null, MultiAnnotatedClass.class);

        cache.addMappedAnnotation(mapping1);
        cache.addMappedAnnotation(mapping2);
        cache.addMappedAnnotation(mapping3);
    }

    @Test
    public void testGetAnnotationMappingsByAnnotation() {

        List<AnnotationMapping> mappings = cache.getAnnotationMappingsByAnnotation(PostProcessFactory.class);

        assertTrue(mappings.contains(mapping1));
        assertTrue(mappings.contains(mapping2));
        assertFalse(mappings.contains(mapping3));
    }

}
