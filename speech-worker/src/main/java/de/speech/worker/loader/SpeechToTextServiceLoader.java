package de.speech.worker.loader;

import de.fraunhofer.iosb.spinpro.annotations.Service;
import de.fraunhofer.iosb.spinpro.speechtotext.ISpeechToTextService;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;

public class SpeechToTextServiceLoader {

    private final SpeechToTextServiceWrapper result;

    public SpeechToTextServiceLoader(Path path) throws LoadingException {
        JarLoader<ISpeechToTextService> jarLoader = new JarLoader<>(path, ISpeechToTextService.class);
        List<Class<ISpeechToTextService>> serviceClasses = jarLoader.getClasses();

        if (serviceClasses.size() == 0) {
            throw new LoadingException("No ISpeechToTextService implementations were found");
        }

        Class<ISpeechToTextService> classToUse = serviceClasses.get(0);
        ISpeechToTextService service;
        try {
            service = classToUse.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new LoadingException("Error while instantiating the ISpeechToTextService");
        }
        if (!classToUse.isAnnotationPresent(Service.class)) {
            throw new LoadingException("The Service annotation is missing");
        }

        Service annotation = classToUse.getAnnotation(Service.class);
        result = new SpeechToTextServiceWrapper(service, annotation);
    }

    public SpeechToTextServiceWrapper getResult() {
        return result;
    }
}
