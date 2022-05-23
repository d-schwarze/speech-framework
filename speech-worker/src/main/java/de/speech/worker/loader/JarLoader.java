package de.speech.worker.loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public final class JarLoader<T> {

    private List<Class<T>> classList;

    public JarLoader(Path path, Class<T> type) throws LoadingException {
        File start = path.toFile();
        List<File> files;
        try {
            files = getFiles(start);
        } catch (IOException e) {
            throw new LoadingException();
        }
        List<URL> urls = new ArrayList<>();
        List<JarFile> jarFiles = files.stream()
                .map(file -> {
                    try {
                        urls.add(file.toURI().toURL());
                        return new JarFile(file);
                    } catch (IOException e) {
                        return null;
                        // skip failing
                    }
                })
                .filter(Objects::nonNull) // filter out failed
                .collect(Collectors.toList());

        ClassLoader classLoader = new URLClassLoader(urls.toArray(URL[]::new), this.getClass().getClassLoader());
        Thread.currentThread().setContextClassLoader(classLoader);

        classList = new ArrayList<>();
        jarFiles.forEach(jarFile -> {
            jarFile.stream()
                    .filter(jarEntry -> jarEntry.getName().endsWith(".class"))
                    .map(jarEntry -> {
                        try {
                            return classLoader.loadClass(jarEntry.getName().replaceAll("/", ".").replace(".class", ""));
                        } catch (ClassNotFoundException | NoClassDefFoundError e) {
                            System.out.println("class not found exception");
                            return null;
                            // skip failing
                        }
                    })
                    .filter(Objects::nonNull) // filter out failed
                    .filter(clazz -> Arrays.asList(clazz.getInterfaces()).contains(type))
                    .forEach(clazz -> classList.add((Class<T>) clazz));
        });
    }

    private List<File> getFiles(File start) throws IOException {
        if (!start.isDirectory()) {
            return Collections.singletonList(start);
        }

        return Files.walk(start.toPath())
                .map(Path::toFile)
                .filter(File::isFile)
                .filter(file -> file.getName().endsWith(".jar"))
                .collect(Collectors.toList());
    }

    public List<Class<T>> getClasses() {
        return classList;
    }
}
