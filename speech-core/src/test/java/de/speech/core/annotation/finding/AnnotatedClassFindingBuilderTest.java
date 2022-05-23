package de.speech.core.annotation.finding;

import de.speech.core.annotation.adaptions.postprocessing.PostProcessFactory;
import de.speech.core.annotation.annotatedtestclasses.MultiAnnotatedClass;
import de.speech.core.annotation.annotatedtestclasses.TestAnnotation;
import de.speech.core.annotation.annotatedtestclasses.TestAnnotationWithValue;
import de.speech.core.annotation.annotatedtestclasses.TestPostProcessFactory2;
import de.speech.core.annotation.mapping.AnnotationMapping;
import de.speech.core.annotation.mapping.AnnotationMappingCache;
import de.speech.core.annotation.mapping.AnnotationMappingFactory;
import de.speech.core.annotation.mapping.AnnotationMappingFactoryMock;
import de.speech.core.annotation.reflection.ClassSearcher;
import de.speech.core.annotation.reflection.DynamicInstantiationError;
import de.speech.core.annotation.reflection.PackageClassSearcher;
import de.speech.core.annotation.reflection.ReflectionUtils;
import de.speech.core.postprocessing.IPostProcessFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AnnotatedClassFindingBuilderTest {

    private AnnotatedClassFinder finder;

    private AnnotationMappingCache cache;

    private AnnotationMapping mapping1;
    private AnnotationMapping mapping2;
    private AnnotationMapping mapping3;


    @BeforeEach
    public final void initializeTestSuit() {

        cache = new AnnotationMappingCache();

        mapping1 = new AnnotationMapping(PostProcessFactory.class, new HashMap<>(), TestPostProcessFactory2.class);
        mapping2 = new AnnotationMapping(PostProcessFactory.class, new HashMap<>(), MultiAnnotatedClass.class);
        mapping3 = new AnnotationMapping(TestAnnotation.class, new HashMap<>(), MultiAnnotatedClass.class);

        cache.addMappedAnnotation(mapping1);
        cache.addMappedAnnotation(mapping2);
        cache.addMappedAnnotation(mapping3);

        finder = new DefaultAnnotatedClassFinder(cache);
    }

    @Test
    public final void testFindAnnotatedClass() throws DynamicInstantiationError {

        Class<? extends IPostProcessFactory> postProcessFactoryClass =
                finder.findAnnotatedClasses().annotation(PostProcessFactory.class)
                                             .parent(IPostProcessFactory.class)
                                             .getOne();

        assertNotNull(postProcessFactoryClass);
        assertEquals(postProcessFactoryClass, TestPostProcessFactory2.class);

        IPostProcessFactory actualPostProcess = ReflectionUtils.createInstanceOfClass(postProcessFactoryClass);

        TestPostProcessFactory2 expectedPostProcessFactory = new TestPostProcessFactory2();

        assertEquals(expectedPostProcessFactory.createPostProcess(), actualPostProcess.createPostProcess());
    }

    @Test
    public final void testFindAnnotatedClasses() {
        List<Class<? extends IPostProcessFactory>> postProcessFactoryClasses =
                finder.findAnnotatedClasses().annotation(PostProcessFactory.class)
                                             .parent(IPostProcessFactory.class)
                                             .getAll();

        assertEquals(2, postProcessFactoryClasses.size());
    }

    @Test
    public final void testFindAnnotatedClassWithSetOfAnnotations() throws DynamicInstantiationError {
        Set<Class<? extends Annotation>> annotationSet = new HashSet<>();
        annotationSet.add(PostProcessFactory.class);
        annotationSet.add(TestAnnotation.class);

        Class<? extends IPostProcessFactory> postProcessFactoryClass =
                finder.findAnnotatedClasses().annotation(PostProcessFactory.class)
                                             .annotation(TestAnnotation.class)
                                             .parent(IPostProcessFactory.class)
                                             .getOne();

        assertNotNull(postProcessFactoryClass);
        assertEquals(postProcessFactoryClass, MultiAnnotatedClass.class);

        IPostProcessFactory actualPostProcess = ReflectionUtils.createInstanceOfClass(postProcessFactoryClass);

        MultiAnnotatedClass expectedPostProcess = new MultiAnnotatedClass();

        assertEquals(expectedPostProcess.createPostProcess(), actualPostProcess.createPostProcess());
    }

    @Test
    public final void testFindAnnotatedClassesWithSetOfAnnotations() {
        Set<Class<? extends Annotation>> annotationSet = new HashSet<>();
        annotationSet.add(PostProcessFactory.class);
        annotationSet.add(TestAnnotation.class);

        List<Class<? extends IPostProcessFactory>> postProcessFactoryClasses =
                finder.findAnnotatedClasses().annotation(PostProcessFactory.class)
                                             .annotation(TestAnnotation.class)
                                             .parent(IPostProcessFactory.class)
                                             .getAll();

        assertEquals(1, postProcessFactoryClasses.size());
    }

    @Test
    public final void testFindAnnotatedClassesWithAnnotationValues() {
        Map<String, Object> values = new HashMap<>();
        values.put("parallel", true);
        AnnotationMapping mapping = new AnnotationMapping(PostProcessFactory.class, values, TestPostProcessFactory2.class);

        cache.addMappedAnnotation(mapping);

        values = new HashMap<>();
        values.put("parallel", false);
        mapping = new AnnotationMapping(PostProcessFactory.class, values, TestPostProcessFactory2.class);

        cache.addMappedAnnotation(mapping);

        values = new HashMap<>();
        values.put("parallel", false);
        mapping = new AnnotationMapping(PostProcessFactory.class, values, TestPostProcessFactory2.class);

        cache.addMappedAnnotation(mapping);

        List<Class<? extends IPostProcessFactory>> postProcessFactoryClasses =
                finder.findAnnotatedClasses().annotation(PostProcessFactory.class)
                                             .withValue("parallel", true)
                                             .parent(IPostProcessFactory.class)
                                             .getAll();

        assertEquals(1, postProcessFactoryClasses.size());


    }

    @Test
    public final void testFindAnnotatedClassesWithAnnotationValues2() throws IOException {
        ClassSearcher searcher = new PackageClassSearcher("de.speech.core.annotation");
        AnnotationMappingFactory factory = new AnnotationMappingFactoryMock(searcher);

        List<AnnotationMapping> mappings = factory.createAnnotationMappings(TestAnnotationWithValue.class);

        AnnotationMappingCache cache = new AnnotationMappingCache();

        for (AnnotationMapping m : mappings) {
            cache.addMappedAnnotation(m);
        }

        finder = new DefaultAnnotatedClassFinder(cache);

        List<Class<? extends IPostProcessFactory>> postProcessFactoryClasses =
                finder.findAnnotatedClasses().annotation(TestAnnotationWithValue.class)
                                             .withValue("test", "tut")
                                             .parent(IPostProcessFactory.class)
                                             .getAll();

        assertEquals(1, postProcessFactoryClasses.size());
    }

    @Test
    public final void testFindAnnotatedClasses_NoMatchingClass_Null() {
        finder = new DefaultAnnotatedClassFinder(new AnnotationMappingCache());

        assertNull(finder.findAnnotatedClasses().annotation(TestAnnotationWithValue.class)
                                                .withValue("test", "tut")
                                                .parent(IPostProcessFactory.class)
                                                .getOne());
    }
}
