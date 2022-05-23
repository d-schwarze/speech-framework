package de.speech.core.annotation.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for accessing a selection of {@code java.lang.reflect} functionalities.
 */
public final class ReflectionUtils {

    /**
     * Creates a new instance of a given class.
     * @param clazz class that should be instantiated
     * @param <T> type instance
     * @return instance of the given class
     * @throws DynamicInstantiationError raised if an exception occurs during the instantiation
     */
    public static <T> T createInstanceOfClass(Class<T> clazz) throws DynamicInstantiationError {
        return createInstanceOfClass(clazz, null, null);
    }

    /**
     * Creates a new instance of a given class.
     * @param clazz class that should be instantiated
     * @param parameterTypes types of the constructor parameters
     * @param parameters constructor parameters
     * @param <T> type instance
     * @return instance of the given class
     * @throws DynamicInstantiationError raised if an exception occurs during the instantiation
     * 
     * @see Class#getDeclaredConstructor(Class[]) 
     * @see java.lang.reflect.Constructor#newInstance(Object...)
     */
    public static <T> T createInstanceOfClass(Class<T> clazz, Class<?>[] parameterTypes, Object[] parameters) throws DynamicInstantiationError {

        if (clazz == null) {
            throw new IllegalArgumentException();
        }

        try {
            return clazz.getDeclaredConstructor(parameterTypes).newInstance(parameters);
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new DynamicInstantiationError(e);
        }
    }

    /**
     * Reads all default values that are present in an annotation.
     *
     * @param annotation that default values should be read
     * @return all default annotation values
     */
    public static Map<String, Object> getDefaultAnnotationValues(Class<? extends Annotation> annotation) {

        Map<String, Object> annotationValues = new HashMap<>();

        for (Method method : annotation.getDeclaredMethods()) {
            Object defaultValue = method.getDefaultValue();
            if (defaultValue != null)
                annotationValues.put(method.getName(), defaultValue);
        }

        return annotationValues;
    }

    /**
     * Determines whether a given class directly implements a given interface. Transitive implementations are
     * not recognized.
     *
     * @param clazz class that should be checked for the interface
     * @param interfaze interface that should be present on the class
     * @return true if {@code clazz} implements {@code interfaze}
     */
    public static boolean containsInterface(Class<?> clazz, Class<?> interfaze) {
        for (Class<?> i : clazz.getInterfaces()) {
            if (i.equals(interfaze)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines whether a given class directly extends a given super class.
     * That means if {@code ClassA extends ClassB} and {@code clazz=ClassA, superClass=ClassB}, it would be true
     * but transitive extensions would not be recognized.
     *
     * @param clazz class that should extend the super class
     * @param superClass that should be extended by the class
     * @return true if {@code clazz} extends {@code superClass}
     */
    public static boolean hasSuperClass(Class<?> clazz, Class<?> superClass) {
        return clazz.getSuperclass().equals(superClass);
    }

    /**
     * Determines whether a given class either implements or extends a given parent class.
     * @param possibleDerivative class that should be a derivative of {@code parent}
     * @param parent interface or class that should be implemented/extended
     * @return true if {@code possibleDerivative} is a derivative of {@code parent}
     */
    public static boolean isDerivativeOf(Class<?> possibleDerivative, Class<?> parent) {
        return possibleDerivative.equals(parent) || containsInterface(possibleDerivative, parent) || hasSuperClass(possibleDerivative, parent);
    }
}
