package de.speech.core.annotation.adaptions.postprocessing;


import de.speech.core.annotation.AnnotationSystemAdapter;
import de.speech.core.annotation.reflection.DynamicInstantiationError;
import de.speech.core.annotation.reflection.ReflectionUtils;
import de.speech.core.logging.Loggable;
import de.speech.core.postprocessing.IPostProcessFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * AnnotationSystem adaption for the post processing phase. All class that may be annotated with annotations for
 * the post process can be accessed here and dynamically loaded.
 */
public class PostProcessingAnnotationSystem extends AnnotationSystemAdapter implements Loggable {

    public PostProcessingAnnotationSystem() {
        this.getParent().createMappingsForAnnotation(PostProcessFactory.class);
    }

    /**
     * Finds one with {@linkplain PostProcessFactory} annotated class that implements {@linkplain IPostProcessFactory}.
     *
     * @return annotated post process
     */
    public IPostProcessFactory getAnnotatedPostProcessFactory() {

        Class<? extends IPostProcessFactory> postProcessFactoryClass =
                this.getParent().getAnnotatedClassFinder()
                                .findAnnotatedClasses()
                                .annotation(PostProcessFactory.class)
                                .parent(IPostProcessFactory.class)
                                .getOne();

        IPostProcessFactory postProcessFactory = null;
        if (postProcessFactoryClass != null) {
            try {
                postProcessFactory = ReflectionUtils.createInstanceOfClass(postProcessFactoryClass);
            } catch (DynamicInstantiationError e) {
                LOGGER.warning(e.getMessage());
            }
        }

        return postProcessFactory;
    }

    /**
     * Finds all with {@linkplain PostProcessFactory} annotated classes that implement {@linkplain IPostProcessFactory}.
     *
     * @return annotated post process factories
     */
    public List<IPostProcessFactory> getAnnotatedPostProcessFactories() {

        List<Class<? extends IPostProcessFactory>> postProcessClasses =
                this.getParent().getAnnotatedClassFinder()
                                .findAnnotatedClasses()
                                .annotation(PostProcessFactory.class)
                                .parent(IPostProcessFactory.class)
                                .getAll();

        return instantiatePostProcessFactoryClasses(postProcessClasses);
    }

    /**
     * Instantiates an object for each class that inherits from {@linkplain IPostProcessFactory}.
     *
     * @param postProcessFactoryClasses classes that for each an object should be instantiated
     * @return instantiated objects for the given classes
     */
    private List<IPostProcessFactory> instantiatePostProcessFactoryClasses(List<Class<? extends IPostProcessFactory>> postProcessFactoryClasses) {
        List<IPostProcessFactory> postProcessFactories = new ArrayList<>();

        for (Class<? extends IPostProcessFactory> postProcessFactoryClass : postProcessFactoryClasses) {
            if (postProcessFactoryClass != null) {
                try {
                    IPostProcessFactory postProcessFactory = ReflectionUtils.createInstanceOfClass(postProcessFactoryClass);
                    postProcessFactories.add(postProcessFactory);
                } catch (DynamicInstantiationError e) {
                    LOGGER.warning(e.getMessage());
                }
            }
        }

        return postProcessFactories;
    }
}
