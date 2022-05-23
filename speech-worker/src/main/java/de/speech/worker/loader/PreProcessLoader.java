package de.speech.worker.loader;

import de.speech.worker.local.preprocessing.IPreProcess;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PreProcessLoader {

    private final List<IPreProcess> preProcesses;

    public PreProcessLoader(Path path) throws LoadingException {
        JarLoader<IPreProcess> jarLoader = new JarLoader<>(path, IPreProcess.class);
        List<Class<IPreProcess>> preprocessClasses = jarLoader.getClasses();

        preProcesses = preprocessClasses.stream()
                .map(clazz -> {
                    try {
                        return clazz.getDeclaredConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<IPreProcess> getPreProcesses() {
        return preProcesses;
    }
}
