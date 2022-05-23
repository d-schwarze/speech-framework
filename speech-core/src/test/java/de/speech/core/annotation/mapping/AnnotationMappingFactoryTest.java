package de.speech.core.annotation.mapping;

import de.speech.core.annotation.adaptions.postprocessing.PostProcessFactory;
import de.speech.core.annotation.annotatedtestclasses.MultiAnnotatedClass;
import de.speech.core.annotation.annotatedtestclasses.TestAnnotation;
import de.speech.core.annotation.annotatedtestclasses.TestPostProcessFactory2;
import de.speech.core.annotation.reflection.ClassSearcher;
import de.speech.core.annotation.reflection.PackageClassSearcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnnotationMappingFactoryTest {

    private ClassSearcher searcher;

    private AnnotationMappingFactory factory;

    @BeforeEach
    public void initializeTest() throws IOException {
        searcher = new PackageClassSearcher("de.speech.core.annotation");
        factory = new AnnotationMappingFactoryMock(searcher);
    }

    @Test
    public void testInitializeAnnotationMappings() {

        List<AnnotationMapping> mappings = factory.initializeAnnotationMappings(PostProcessFactory.class);

        boolean containsTestPostProcessClass = false;

        for (AnnotationMapping mapping : mappings) {
            if (mapping.getMappedElement().equals(TestPostProcessFactory2.class)) {
                containsTestPostProcessClass = true;
                break;
            }
        }

        assertTrue(containsTestPostProcessClass, "With @PostProcessFactory annotated class TestPostProcessFactory was not found.");
    }

    @Test
    public void testInitializeAnnotationMappingsWithMultipleAnnotations() {

        List<AnnotationMapping> mappings = factory.initializeAnnotationMappings(PostProcessFactory.class);

        boolean containsMultiAnnotatedClass = false;

        for (AnnotationMapping mapping : mappings) {
            if (mapping.getMappedElement().equals(MultiAnnotatedClass.class)) {
                containsMultiAnnotatedClass = true;
                break;
            }
        }

        assertTrue(containsMultiAnnotatedClass, "With @PostProcessFactory annotated class MultiAnnotatedClass was not found.");

        mappings = factory.initializeAnnotationMappings(TestAnnotation.class);

        containsMultiAnnotatedClass = false;

        for (AnnotationMapping mapping : mappings) {
            if (mapping.getMappedElement().equals(MultiAnnotatedClass.class)) {
                containsMultiAnnotatedClass = true;
                break;
            }
        }

        assertTrue(containsMultiAnnotatedClass, "With @TestAnnotation annotated class TestPostProcessFactory was not found.");
    }

    @Test
    public void testCreateAnnotationMappingsWithAlreadyCachedAnnotationMappings() {

        //Cache Classes annotated with @PostProcessFactory
        List<AnnotationMapping> cachedMappings = factory.createAnnotationMappings(PostProcessFactory.class);

        List<AnnotationMapping> newMappings = factory.createAnnotationMappings(PostProcessFactory.class);

        for (AnnotationMapping mapping : newMappings) {
            boolean found = false;

            for (AnnotationMapping m : cachedMappings) {
                if (m == mapping) {
                    found = true;
                    break;
                }
            }

            assertTrue(found, "Already cached AnnotationMapping was created.");
        }
    }

    @Test
    public final void testAnnotationMappingCache_DefaultConstructor() throws IOException {
        AnnotationMappingFactory defaultFactory = new AnnotationMappingFactory();

        List<AnnotationMapping> mappings = defaultFactory.createAnnotationMappings(TestAnnotation.class);

        assertNotEquals(0, mappings.size());
    }
}
