package de.speech.core.annotation.reflection;

import de.speech.core.annotation.adaptions.postprocessing.PostProcessFactory;
import de.speech.core.annotation.annotatedtestclasses.TestAnnotationWithValue;
import de.speech.core.annotation.annotatedtestclasses.TestPostProcessFactory2;
import de.speech.core.annotation.annotatedtestclasses.ValueAnnotatedClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class PackageClassSearcherTest {

    private PackageClassSearcher packageClassSearcher;

    private ClassLoader classLoader;

    private String packageTestExample1 = "de.speech.core.annotation.reflection.testexample.testexample1";

    private String packageTestExample2 = "de.speech.core.annotation.reflection.testexample.testexample2";

    private List<String> packageTestExample1Elements;

    private List<String> packageTestExample2Elements;

    @BeforeEach
    public final void initializeTest() throws IOException, URISyntaxException {
        packageClassSearcher = new MockPackageClassSearcher();

        classLoader = Thread.currentThread().getContextClassLoader();

        packageTestExample1Elements = new ArrayList<>();
        packageTestExample1Elements.add("de.speech.core.annotation.reflection.testexample.testexample1.SearchableClass1");
        packageTestExample1Elements.add("de.speech.core.annotation.reflection.testexample.testexample1.SearchableClass2");
        packageTestExample1Elements.add("de.speech.core.annotation.reflection.testexample.testexample1.SearchableInterface1");

        packageTestExample2Elements = new ArrayList<>();
        packageTestExample2Elements.add("de.speech.core.annotation.reflection.testexample.testexample2.SearchableAnnotation1");
        packageTestExample2Elements.add("de.speech.core.annotation.reflection.testexample.testexample2.SearchableClass3");
        packageTestExample2Elements.add("de.speech.core.annotation.reflection.testexample.testexample2.SearchableClass4");
        packageTestExample2Elements.add("de.speech.core.annotation.reflection.testexample.testexample2.SearchableEnum1");

    }

    @Test
    public final void testVisitClassFilesFromPackageTextExample1() throws IOException {

        List<SpeechClassVisitor> visitors = packageClassSearcher.visitClassFilesFromPackages(classLoader, new String[]{ packageTestExample1 });

        List<String> actualElements = visitors.stream().map(SpeechClassVisitor::getClassName).sorted().collect(Collectors.toList());

        assertEquals(packageTestExample1Elements, actualElements, String.format("Amount of elements that were found: %d", actualElements.size()));
    }

    @Test
    public final void testVisitClassFilesFromPackageTextExample2() throws IOException {

        List<SpeechClassVisitor> visitors = packageClassSearcher.visitClassFilesFromPackages(classLoader, new String[]{ packageTestExample2 });

        List<String> actualElements = visitors.stream().map(SpeechClassVisitor::getClassName).sorted().collect(Collectors.toList());

        assertEquals(packageTestExample2Elements, actualElements, String.format("Amount of elements that were found: %d", actualElements.size()));
    }

    @Test
    public final void testVisitClassFilesFromPackages() throws IOException {
        List<String> expectedElements = new ArrayList<>();
        expectedElements.addAll(packageTestExample1Elements);
        expectedElements.addAll(packageTestExample2Elements);

        List<SpeechClassVisitor> visitors = packageClassSearcher.visitClassFilesFromPackages(classLoader, new String[]{ packageTestExample1, packageTestExample2 });

        List<String> actualElements = visitors.stream().map(SpeechClassVisitor::getClassName).sorted().collect(Collectors.toList());

        assertEquals(expectedElements, actualElements, String.format("Amount of elements that were found: %d", actualElements.size()));
    }

    @Test
    public final void testVisitClassFilesFromLocation() throws IOException {

        String location = "de/speech/core/annotation/reflection/testexample/testexample1";

        List<SpeechClassVisitor> visitors = packageClassSearcher.visitClassFilesFromPackages(classLoader, new String[]{ location });

        List<String> actualElements = visitors.stream().map(SpeechClassVisitor::getClassName).sorted().collect(Collectors.toList());

        assertEquals(packageTestExample1Elements, actualElements, String.format("Amount of elements that were found: %d", actualElements.size()));

    }

    @Test
    public final void testVisitFileAndAnnotation() throws IOException {

        File file = new File("../speech-core/build/classes/java/test/de/speech/core/annotation/annotatedtestclasses/TestPostProcessFactory2.class");
        File[] files = file.listFiles();

        if (files != null) {
            for (File f : files) {
                System.out.println(f.getName());
            }
        }

        List<SpeechClassVisitor> visitors = new ArrayList<>();

        packageClassSearcher.visitFile(file, visitors);

        assertEquals(visitors.get(0).getClassName(), "de.speech.core.annotation.annotatedtestclasses.TestPostProcessFactory2");
        assertEquals(visitors.get(0).getAnnotations().get(0), "de.speech.core.annotation.adaptions.postprocessing.PostProcessFactory");

    }

    @Test
    public final void testFindAnnotatedClasses() throws IOException {

        PackageClassSearcher searcher = new PackageClassSearcher("de.speech.core.annotation");

        List<Class<?>> elements = searcher.findAnnotatedClasses(PostProcessFactory.class);

        assertTrue(elements.contains(TestPostProcessFactory2.class));

    }

    @Test
    public final void testGetAnnotatedMetaDataOfAnnotatedElement() throws IOException {
        PackageClassSearcher searcher = new PackageClassSearcher("de.speech.core.annotation");

        Map<String, Object> annotationValues = searcher.getAnnotatedValuesFromAnnotatedElement(ValueAnnotatedClass.class, TestAnnotationWithValue.class);

        String testValue = (String) annotationValues.get("test");

        assertEquals("tut", testValue);

    }

    @Test
    public final void testGetAnnotatedValuesFormAnnotationElement_ElementNotPresent_Null() throws IOException {
        PackageClassSearcher searcher = new PackageClassSearcher(new String[]{});

        assertNull(searcher.getAnnotatedValuesFromAnnotatedElement(ValueAnnotatedClass.class, TestAnnotationWithValue.class));
    }

    @Test
    public final void testGetAnnotatedValuesFormAnnotationElement_AnnotationNotPresent_Null() throws IOException {
        PackageClassSearcher searcher = new PackageClassSearcher(new String[]{});
        SpeechClassVisitor visitor = new SpeechClassVisitor();
        visitor.visit(0, 0, ValueAnnotatedClass.class.getName(), null, ValueAnnotatedClass.class.getSuperclass().getName(), new String[]{});
        searcher.getCachedClassFiles().add(visitor);

        assertNull(searcher.getAnnotatedValuesFromAnnotatedElement(ValueAnnotatedClass.class, TestAnnotationWithValue.class));
    }
}
