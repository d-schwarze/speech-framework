package de.speech.core.annotation.adaptions.postprocessing;

import de.speech.core.annotation.annotatedtestclasses.MultiAnnotatedClass;
import de.speech.core.annotation.annotatedtestclasses.TestPostProcessFactory2;
import de.speech.core.postprocessing.IPostProcess;
import de.speech.core.postprocessing.IPostProcessFactory;
import de.speech.core.postprocessing.PostProcessor;
import de.speech.core.postprocessing.TestPostProcessFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PostProcessingAnnotationSystemTest {

    private static PostProcessingAnnotationSystem annotationSystem;

    @BeforeAll
    public static void initializeTestSuit() {

        annotationSystem = new PostProcessingAnnotationSystem();

    }

    @Test
    public final void testGetAnnotatedPostProcessFactory() {

        IPostProcessFactory postProcessFactory = annotationSystem.getAnnotatedPostProcessFactory();

        TestPostProcessFactory2 testPostProcessFactory2 = new TestPostProcessFactory2();
        MultiAnnotatedClass multiAnnotatedClass = new MultiAnnotatedClass();

        IPostProcess actual = postProcessFactory.createPostProcess();

        assertTrue((actual == testPostProcessFactory2.POST_PROCESS_OBJ) ||
                (actual == multiAnnotatedClass.POST_PROCESS_OBJ) ||
                (postProcessFactory.getClass().equals(TestPostProcessFactory.class)));

    }

    @Test
    public final void testGetAnnotatedPostProcessFactories() {
        List<IPostProcessFactory> postProcessFactories = annotationSystem.getAnnotatedPostProcessFactories();

        assertEquals(3, postProcessFactories.size());
    }

    @Test
    public final void testPostProcessor_WithAnnotatedFactories() {
        PostProcessor pp = new PostProcessor(null);

        assertNotEquals(0, pp.getPostProcessFactories().size());
    }


}
