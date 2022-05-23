package de.speech.core.application.annotation;

import de.speech.core.annotation.AnnotationSystemAdapter;
import de.speech.core.application.configuration.SpeechConfiguration;
import de.speech.core.logging.Loggable;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class SpeechAnnotationSystem extends AnnotationSystemAdapter implements Loggable {

    public SpeechAnnotationSystem() {

        this.getParent().createMappingsForAnnotation(ApplicationConfiguration.class);

    }

    /**
     * Finds a {@linkplain de.speech.core.application.annotation.ApplicationConfiguration user annotated} configuration
     * @return user annotated configuration or null if no annotated configuration was found
     */
    public SpeechConfiguration getSpeechConfiguration() {

        Class<? extends SpeechConfiguration> speechConfigurationClass =
                getParent().getAnnotatedClassFinder()
                        .findAnnotatedClasses()
                        .annotation(ApplicationConfiguration.class)
                        .parent(SpeechConfiguration.class)
                        .getOne();

        SpeechConfiguration configuration = null;

        if (speechConfigurationClass != null) {
            try {
                configuration = speechConfigurationClass.getDeclaredConstructor().newInstance();
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
            }
        }

        return configuration;
    }
}
