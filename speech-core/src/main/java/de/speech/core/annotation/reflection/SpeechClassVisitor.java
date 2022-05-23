package de.speech.core.annotation.reflection;


import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom class visitor for extracting information of class files like interface or present annotation.
 */
public class SpeechClassVisitor extends ClassVisitor {

    private String className;

    private String superClassName;

    private String[] interfaces;

    private List<String> annotations;

    private Map<String, SpeechAnnotationVisitor> annotationVisitors;

    public SpeechClassVisitor() {
        super(Opcodes.ASM9);

        this.annotations = new ArrayList<>();
        this.annotationVisitors = new HashMap<>();
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        className = name.replace('/', '.');
        superClassName = superName.replace('/', '.');
        this.interfaces = interfaces;

        for (int i = 0; i < interfaces.length; i++) {
            this.interfaces[i] = this.interfaces[i].replace('/', '.');
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        //"Lde/test/Annotation;" -> "de.test.Annotation"
        String annotation = descriptor
                                .substring(1, descriptor.length() - 1)
                                .replace('/', '.');

        annotations.add(annotation);

        SpeechAnnotationVisitor annotationVisitor = new SpeechAnnotationVisitor();
        annotationVisitors.put(annotation, annotationVisitor);

        return annotationVisitor;
    }

    public String getClassName() {
        return this.className;
    }

    public String getSuperClassName() {
        return this.superClassName;
    }

    public String[] getInterfaces() {
        return this.interfaces;
    }

    public List<String> getAnnotations() {
        return this.annotations;
    }

    public Map<String, Object> getAnnotationValues(String annotation) {
        SpeechAnnotationVisitor annotationVisitor = annotationVisitors.get(annotation);


        if (annotationVisitor != null) {
            return annotationVisitor.getAnnotationValues();
        }

        return null;
    }

    public Map<String, Object> getAnnotationValues(Class<? extends Annotation> annotation) {
        return getAnnotationValues(annotation.getName());
    }
}
