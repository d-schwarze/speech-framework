package de.speech.core.annotation.reflection;

import de.speech.core.annotation.reflection.testclasses.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReflectionUtilsTest {

    @Test
    public final void testCreateInstanceOfClass_WithParameters() throws DynamicInstantiationError {
        String parameter = "Test";
        ValidWithParameterClass obj = ReflectionUtils.createInstanceOfClass(ValidWithParameterClass.class, new Class<?>[]{ String.class }, new Object[]{ parameter });

        assertEquals(ValidWithParameterClass.class, obj.getClass());
        assertEquals(parameter, obj.getParameter());
    }

    @Test
    public final void testCreateInstanceOfClass_WithNoParameters() throws DynamicInstantiationError {
        ValidWithNoParametersClass obj = ReflectionUtils.createInstanceOfClass(ValidWithNoParametersClass.class, null, null);

        assertEquals(ValidWithNoParametersClass.class, obj.getClass());

    }

    @Test
    public final void testCreateInstanceOfClass_ClassParameter_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> ReflectionUtils.createInstanceOfClass(null));
    }

    @Test
    public final void testCreateInstanceOfClass_PrivateConstructor_DynamicInstantiationError() {
        assertThrows(DynamicInstantiationError.class, () -> ReflectionUtils.createInstanceOfClass(PrivateConstructorClass.class));
    }

    @Test
    public final void testCreateInstanceOfClass_NoDeclaredConstructor_DynamicInstantiationError() throws DynamicInstantiationError {
        NoDeclaredConstructorClass obj = ReflectionUtils.createInstanceOfClass(NoDeclaredConstructorClass.class);

        assertEquals(NoDeclaredConstructorClass.class, obj.getClass());
    }

    @Test
    public final void testCreateInstanceOfClass_TooFewParametersGiven_DynamicInstantiationError() {
        assertThrows(DynamicInstantiationError.class, () ->
            ReflectionUtils.createInstanceOfClass(ConstructorWithLessParametersClass.class,
                                                  new Class[]{String.class, Integer.class},
                                                  new Object[]{"TestParameter"})
        );
    }

    @Test
    public final void testCreateInstanceOfClass_TooManyParametersGiven_DynamicInstantiationError() {
        assertThrows(DynamicInstantiationError.class, () ->
                ReflectionUtils.createInstanceOfClass(ConstructorWithLessParametersClass.class,
                                                      new Class[]{String.class, Integer.class},
                                                      new Object[]{"TestParameter", 4, "Test"})
        );
    }

    @Test
    public final void testContainsInterface_InterfaceIsMissing() {
        assertFalse(ReflectionUtils.containsInterface(MissingInterfaceClass.class, ValidInterface.class));
    }

    @Test
    public final void testHasSuperClass_True() {
        assertTrue(ReflectionUtils.hasSuperClass(WithSuperClass.class, SuperClass.class));
    }

    @Test
    public final void testHasSuperClass_False() {
        assertFalse(ReflectionUtils.hasSuperClass(WithoutSuperClass.class, SuperClass.class));
    }

    class MissingInterfaceClass {

    }

    interface ValidInterface {

    }

    class WithSuperClass extends SuperClass{

    }

    class WithoutSuperClass {

    }

    class SuperClass {

    }
}
