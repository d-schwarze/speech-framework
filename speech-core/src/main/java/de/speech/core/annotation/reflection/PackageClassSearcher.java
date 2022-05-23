package de.speech.core.annotation.reflection;

import de.speech.core.logging.Loggable;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * ClassSearcher that searches for classes in given packages.
 */
public class PackageClassSearcher implements ClassSearcher, Loggable {

    /**
     * ClassLoader for getting the corresponding {@code Class} for a given classfile or its name.
     */
    private ClassLoader classLoader;

    /**
     * Packages that are searched for classes
     */
    private String[] packageNames;

    /**
     * All classfiles as {@linkplain SpeechClassVisitor} that were already searched.
     */
    private List<SpeechClassVisitor> cachedClassFiles;

    public PackageClassSearcher(String packageName) throws IOException {
        this(new String[] { packageName });
    }

    public PackageClassSearcher(String... packageNames) throws IOException {
        this(Thread.currentThread().getContextClassLoader(), packageNames);
    }

    public PackageClassSearcher(ClassLoader classLoader, String... packageNames) throws IOException {
        this.packageNames = packageNames;
        this.classLoader = classLoader;
        this.cachedClassFiles = new ArrayList<>();
        this.cachedClassFiles = visitClassFilesFromPackages(this.classLoader, this.packageNames);
    }

    /**
     * Visits all class files given by some packages. Visited files are represented as {@linkplain SpeechClassVisitor}.
     * For instance location="de.speech.core" for getting all classes of this package
     * @param classLoader for loading the packages
     * @param packageNames names of the packages
     * @return all visited classes
     * @throws IOException If I/O error occurs
     */
    protected List<SpeechClassVisitor> visitClassFilesFromPackages(ClassLoader classLoader, String[] packageNames) throws IOException {
        List<SpeechClassVisitor> classFiles = new ArrayList<>();

        for (String packageName : packageNames) {
            String location = packageName.replace('.', '/');
            classFiles.addAll(visitClassFilesFromLocation(classLoader, location));
        }

        return classFiles;
    }

    /**
     * Visits all class files given by a location. A location is a path (see {@linkplain ClassLoader#getResources(String)}).
     * @param classLoader which accesses the class files
     * @param location to the class files
     * @return all visited classes
     * @throws IOException If I/O error occurs
     */
    protected List<SpeechClassVisitor> visitClassFilesFromLocation(ClassLoader classLoader, String location) throws IOException {
        List<SpeechClassVisitor> speechClassVisitors = new ArrayList<>();
        List<File> files = new ArrayList<>();

        Enumeration<URL> resources = classLoader.getResources(location);

        while (resources.hasMoreElements()) {

            URL resource = resources.nextElement();

            try {
                String path = resource.toURI().getPath();
                if (path != null) {
                    File file = new File(path);
                    files.add(file);
                }
            } catch (URISyntaxException e) {
                LOGGER.warning(e.getMessage());
            }
        }

        for (File f : files) {
            visitFile(f, speechClassVisitors);
        }

        return speechClassVisitors;
    }

    /**
     * Visits a particular file. File may be either a directory or real file.
     * In case its a directory, all children files are visited. In case its a real file it is only visited
     * if it is a class file.
     * @param file file that should be visited
     * @param speechClassVisitors list to which all visited classes should be added
     * @throws IOException If I/O error occurs
     */
    protected void visitFile(File file, List<SpeechClassVisitor> speechClassVisitors) throws IOException {
        if (file.isDirectory()) {
            visitDirectory(file, speechClassVisitors);
        } else if (file.getName().endsWith(CLASS_IDENTIFIER)) {
            visitClassFile(file, speechClassVisitors);
        }
    }

    /**
     * Visits a directory by visiting all child files.
     *
     * @param dir directory that should be visited
     * @param speechClassVisitors list to which all visited classes should be added
     * @throws IOException If I/O error occurs
     */
    private void visitDirectory(File dir, List<SpeechClassVisitor> speechClassVisitors) throws IOException {
        File[] children = dir.listFiles();

        if (children != null) {
            for (File child : children) {
                visitFile(child, speechClassVisitors);
            }
        }
    }

    /**
     * Visits a particular class file.
     *
     * @param classFile class file that should be visited
     * @param speechClassVisitors list to which all visited classes should be added
     * @throws IOException If I/O error occurs
     */
    private void visitClassFile(File classFile, List<SpeechClassVisitor> speechClassVisitors) throws IOException {
        FileInputStream in = new FileInputStream(classFile);

        SpeechClassVisitor visitor = new SpeechClassVisitor();
        ClassReader reader = new ClassReader(in);

        reader.accept(visitor, 0);

        speechClassVisitors.add(visitor);
    }

    /**
     * Finds all annotated classes.
     *
     * @param annotation that has to be present on the class
     * @return annotated classes
     */
    public List<Class<?>> findAnnotatedClasses(Class<? extends Annotation> annotation) {
        List<Class<?>> annotatedClasses = new ArrayList<>();

        for (SpeechClassVisitor visitor : this.cachedClassFiles) {
            if (visitor.getAnnotations().contains(annotation.getName())) {
                try {
                    Class<?> annotatedClass = classLoader.loadClass(visitor.getClassName());
                    annotatedClasses.add(annotatedClass);
                } catch (ClassNotFoundException e) {
                    LOGGER.warning(e.getMessage());
                }
            }
        }

        return annotatedClasses;
    }

    /**
     * Reads annotation values of an annotation that was added to a specific class.
     *
     * @param annotatedElement element that contains the annotation where the meta data should be read
     * @param annotation annotation that values should be read
     * @return all meta data of the annotation (methodName -> metaData)
     */
    public Map<String, Object> getAnnotatedValuesFromAnnotatedElement(
            Class<?> annotatedElement,
            Class<? extends Annotation> annotation) {

        for (SpeechClassVisitor scv : this.cachedClassFiles) {
            if (scv.getClassName().equals(annotatedElement.getName())) {
                return scv.getAnnotationValues(annotation);
            }
        }

        return null;
    }

    @Override
    public Map<Class<?>, Map<String, Object>> findAnnotatedClassesWithAnnotationValues(Class<? extends Annotation> annotation) {
        Map<Class<?>, Map<String, Object>> annotatedClassesWithAnnotationValues = new HashMap<>();

        for (SpeechClassVisitor visitor : this.cachedClassFiles) {
            if (visitor.getAnnotations().contains(annotation.getName())) {
                try {
                    Class<?> annotatedClass = classLoader.loadClass(visitor.getClassName());
                    Map<String, Object> annotatedValues = visitor.getAnnotationValues(annotation);

                    annotatedClassesWithAnnotationValues.put(annotatedClass, annotatedValues);
                } catch (ClassNotFoundException e) {
                    LOGGER.warning(e.getMessage());
                }
            }
        }

        return annotatedClassesWithAnnotationValues;
    }

    public List<SpeechClassVisitor> getCachedClassFiles() {
        return cachedClassFiles;
    }
}
